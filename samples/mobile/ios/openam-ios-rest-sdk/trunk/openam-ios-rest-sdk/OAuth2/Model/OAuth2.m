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

#import "OAuth2.h"
#import "HttpHelper.h"
#import "LegacyRestService.h"

@interface OAuth2()
@property (strong, nonatomic) id<OAuth2Delegate> delegate;

@property (strong, nonatomic) NSDictionary *accessToken;

@property (strong, nonatomic) NSData *receivedData;


@property (nonatomic, strong, readonly) LegacyRestService *restService;
@end

@implementation OAuth2

- (instancetype)init {
    return nil;
}

- (instancetype)initWithDelegate:(id<OAuth2Delegate>)delegate {
    
    self = [super init];
    
    if (self) {
        self.delegate = delegate;
    }
    
    return self;
}

- (LegacyRestService *)restService {
    return [LegacyRestService instance];
}

- (void)getAccessTokenWithCode:(NSString *)code {
    
    NSDictionary *params = [NSMutableDictionary dictionary];
    [params setValue:@"authorization_code" forKey:@"grant_type"];
    [params setValue:code forKey:@"code"];
    [params setValue:[[self.delegate redirectionUrl] absoluteString] forKey:@"redirect_uri"];
    [params setValue:[self.delegate scope] forKey:@"scope"];
    [params setValue:[self.delegate realm] forKey:@"realm"];
    
    [self getAccessTokenWithUrlParams:params forClientId:[self.delegate clientId] usingClientSecret:[self.delegate clientSecret]];
}

- (void)refreshAccessToken:(NSString *)refreshToken {
    
    NSDictionary *params = [NSMutableDictionary dictionary];
    [params setValue:@"refresh_token" forKey:@"grant_type"];
    [params setValue:refreshToken forKey:@"refresh_token"];
    [params setValue:[[self.delegate redirectionUrl] absoluteString] forKey:@"redirect_uri"];
    [params setValue:[self.delegate scope] forKey:@"scope"];
    [params setValue:[self.delegate realm] forKey:@"realm"];
    
    [self getAccessTokenWithUrlParams:params forClientId:[self.delegate clientId] usingClientSecret:[self.delegate clientSecret]];
}

- (void)getAccessTokenWithUrlParams:(NSDictionary *)params forClientId:(NSString *)clientId usingClientSecret:(NSString *)clientSecret {
    
    NSString *url = [NSString stringWithFormat:@"%@%@", [[self.delegate openAMBaseUrl] absoluteString], @"/oauth2/access_token"];
    
    //add authorization header
    NSString *authzUserPassword = [NSString stringWithFormat:@"%@:%@", clientId, clientSecret];
    NSString *authorizationHeader = [NSString stringWithFormat:@"Basic %@", [HttpHelper base64Encode:authzUserPassword]];
    NSLog(@"%@", authorizationHeader);
    
    NSMutableDictionary* headers = [[NSMutableDictionary alloc] init];
    [headers setValue:authorizationHeader forKey:@"Authorization"];
    
    [self.restService post:url withHeaders:headers withFormParams:params onCompletion:^(NSDictionary *response, NSError *err){
        
        if (response) {
            
            if ([response objectForKey:@"access_token"]) {
                
                //convert expires_in to axpires at
                NSTimeInterval expiresIn = (NSTimeInterval)[[response valueForKey:@"expires_in"] intValue];
                
                NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
                [dateFormatter setDateStyle:NSDateFormatterMediumStyle];
                [dateFormatter setTimeStyle:NSDateFormatterMediumStyle];
                NSString *dateStr = [dateFormatter stringFromDate:[[NSDate alloc] initWithTimeIntervalSinceNow:expiresIn]];
                [response setValue:dateStr forKey:@"expires_in"];
                
                if ([response objectForKey:@"refresh_token"]) {
                    //access token request
                    [self.delegate accessTokenCallback:response];
                } else {
                    //refresh token request
                    [self.delegate refreshTokenCallback:response];
                }
                
            } else {
                //TODO
            }
        } else {
            //TODO
        }

    }];
}

- (NSDictionary *)tokenInfoFfromServer:(NSString *)baseUri for:(NSString *)accessToken {
    
    NSString *tokenURL = [NSString stringWithFormat:@"%@%@", baseUri, @"/oauth2/tokeninfo"];
    
    NSDictionary *params = [NSMutableDictionary dictionary];
    [params setValue:accessToken forKey:@"access_token"];
    
    return [self.restService get:tokenURL withHeaders:nil withParams:params];
}

@end
