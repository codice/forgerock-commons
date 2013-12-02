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

#import "OpenAMRESTSDK.h"

@interface OpenAMRESTSDK()

@end

@implementation OpenAMRESTSDK

const NSString * const OPENAM_BASE_URL = @"OPENAM_BASE_URL";

- (AuthenticationProcess *)authenticate {
    return [[AuthenticationProcess alloc] init];
}

- (BOOL)isTokenValid:(NSString *)ssoTokenId forServerInstance:(NSString *)openAMBaseUrl {
    
    NSURL *isTokenValidUrl = [NSURL URLWithString:[NSString stringWithFormat:@"%@%@", openAMBaseUrl, @"/identity/isTokenValid"]];
    
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:isTokenValidUrl cachePolicy:NSURLRequestReloadIgnoringCacheData timeoutInterval:10];
    
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[[@"tokenid=" stringByAppendingString:ssoTokenId] dataUsingEncoding:NSUTF8StringEncoding]];
    
    NSURLResponse *response = nil;
    NSError *error = nil;
    
    if (error == nil) {//TODO should not really be using synchronous request!...
        NSData *receivedData = [NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&error];
        NSString* s = [[NSString alloc] initWithData:receivedData encoding:NSUTF8StringEncoding];
        if ([s length] == 0) {
            return NO;
        }
        return [[s componentsSeparatedByString:@"="][1] boolValue];
    } 
    
    //TODO should really return the error?
    return NO;
}

@end
