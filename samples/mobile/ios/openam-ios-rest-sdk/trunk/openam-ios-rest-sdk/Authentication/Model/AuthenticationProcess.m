/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2013 ForgeRock, AS.
 */

#import "AuthenticationProcess.h"
#import "AuthenticationCallbackResponse.h"
#import "AuthenticationSuccessResponse.h"
#import "AuthenticationFailureResponse.h"
#import "RestService.h"

@interface AuthenticationProcess()
@property (strong, nonatomic) id <AuthenticationProcessDelegate> delegate;
@property (strong, nonatomic) NSString *authenticateUrl;

@property (nonatomic, strong, readonly) RestService *restService;
@end

@implementation AuthenticationProcess  //TODO locking to ensure that when a request has been sent no other can be made until that one has completed?

- (RestService *)restService {
    return [RestService instance];
}

- (void)startAuthenticationToServer:(NSString *)baseUri delegate:(id <AuthenticationProcessDelegate>)delegate {
    
    self.delegate = delegate;
    self.authenticateUrl = [NSString stringWithFormat:@"%@%@", baseUri, @"/json/authenticate"]; //TODO check to see if base URI has trailing slash!!
    
    [self initiateAuthenticationWithHeaders:nil];
}

- (void)startZPLAuthenticationToServer:(NSString *)baseUri forUser:(NSString *)username withPassword:(NSString *)password delegate:(id <AuthenticationProcessDelegate>)delegate {
    
    self.delegate = delegate;
    self.authenticateUrl = [NSString stringWithFormat:@"%@%@", baseUri, @"/json/authenticate"]; //TODO check to see if base URI has trailing slash!!
    
    NSDictionary *headers = [[NSDictionary alloc] initWithObjectsAndKeys:username, @"X-OpenAM-Username", password, @"X-OpenAM-Password", nil];
    
    [self initiateAuthenticationWithHeaders:headers];
}

- (void)initiateAuthenticationWithHeaders:(NSDictionary *)headers {
    [self.restService post:self.authenticateUrl withHeaders:headers withParams:nil withBody:[[NSDictionary alloc] init] onCompletion:[self completionBlock]];
}

- (void)submitCallbacks:(NSDictionary *)callbacks {
    [self.restService post:self.authenticateUrl withHeaders:nil withParams:nil withBody:callbacks onCompletion:[self completionBlock]];
}

- (void (^)(NSDictionary *response, NSError *err))completionBlock {
    return ^(NSDictionary *response, NSError *err) {
        // When the request completes, this block will be called.
        
        if (!err) {
            NSLog(@"Received data %@", response);
            
            if ([response valueForKey:@"tokenId"]) {
                [self.delegate authenticationSucceededWithResult:[[AuthenticationSuccessResponse alloc] initWithData:response]];
            } else if ([response valueForKey:@"errorMessage"]) {
                [self.delegate authenticationFailedWithResult:[[AuthenticationFailureResponse alloc] initWithData:response]];
            } else {
                [self.delegate responseReceivedWithCallbacks:[[AuthenticationCallbackResponse alloc] initWithData:response]];
            }
            
            NSLog(@"connectionDidFinishLoading");
        } else {
            [self.delegate authenticationFailed:err];
            NSLog(@"WHOOPS! Something went wrong!!");
        }
    };
}

@end