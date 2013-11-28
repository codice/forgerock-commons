//
//  OpenAMRESTSDK.m
//  OpenAMRESTSDK
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

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
        return [[s componentsSeparatedByString:@"="][1] boolValue];
    } 
    
    //TODO should really return the error?
    return NO;
}

@end
