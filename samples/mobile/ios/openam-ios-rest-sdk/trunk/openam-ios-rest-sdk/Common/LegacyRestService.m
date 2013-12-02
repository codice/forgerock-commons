//
//  RestishService.m
//  openam-ios-rest-sdk
//
//  Created by Phill on 30/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import "LegacyRestService.h"
#import "RestConnection.h"
#import "RestSyncConnection.h"
#import "HttpHelper.h"

@implementation LegacyRestService

+ (id)allocWithZone:(NSZone *)zone {
    return [self instance];
}

+ (LegacyRestService *)instance {
    static LegacyRestService *singleton = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        singleton = [[super allocWithZone:nil] init];
    });
    return singleton;
}

- (NSMutableURLRequest *)createRequest:(NSString *)url {
    return [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url] cachePolicy:NSURLRequestReloadIgnoringCacheData timeoutInterval:10];
}

- (void)startAsyncRequest:(NSURLRequest *)request onCompletion:(void (^)(NSDictionary *response, NSError *err))block {
    RestConnection *connection = [[RestConnection alloc] initWithRequest:request onCompletion:block];
    [connection start];
}

- (NSDictionary *)startSyncRequest:(NSURLRequest *)request error:(NSError *)error {
    RestSyncConnection *connection = [[RestSyncConnection alloc] initWithRequest:request];
    return [connection startWithError:error];
}

- (void)get:(NSString *)url withHeaders:(NSDictionary *)headers withParams:(NSDictionary *)params onCompletion:(void (^)(NSDictionary *response, NSError *err))block {
    
    if (params) {
        NSString *encodedParams = [HttpHelper urlEncodeDictionary:params];
        url = [url stringByAppendingFormat:@"?%@", encodedParams];
    }
    
    NSMutableURLRequest *request = [self createRequest:url];
    [request setHTTPMethod:@"GET"];
    
    [self startAsyncRequest:request onCompletion:block];
}

- (NSDictionary *)get:(NSString *)url withHeaders:(NSDictionary *)headers withParams:(NSDictionary *)params {
    
    if (params) {
        NSString *encodedParams = [HttpHelper urlEncodeDictionary:params];
        url = [url stringByAppendingFormat:@"?%@", encodedParams];
    }
    
    NSMutableURLRequest *request = [self createRequest:url];
    [request setAllHTTPHeaderFields:headers];
    [request setHTTPMethod:@"GET"];
    
    NSError *error;
    return [self startSyncRequest:request error:error];
}

- (void)post:(NSString *)url withHeaders:(NSDictionary *)headers withFormParams:(NSDictionary *)params onCompletion:(void (^)(NSDictionary *response, NSError *err))block {
    
    NSData *postData = [@"" dataUsingEncoding:NSUTF8StringEncoding];
    if (params) {
        NSString *encodedParams = [HttpHelper urlEncodeDictionary:params];
        NSLog(@"encodedParams: %@", encodedParams);
        postData = [encodedParams dataUsingEncoding:NSUTF8StringEncoding];
    }
    
    NSMutableURLRequest *request = [self createRequest:url];

    [request setAllHTTPHeaderFields:headers];
    [request setHTTPMethod:@"POST"];
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-Type"];
    NSString *postLength = [NSString stringWithFormat:@"%lu", (unsigned long)[postData length]];
    [request setValue:postLength forHTTPHeaderField:@"Content-Length"];
    [request setHTTPBody:postData];
    
    NSLog(@"access_token request: %@, %@, %@, %@", request.URL.absoluteString, request.HTTPMethod, request.allHTTPHeaderFields, request.URL.query);
    
    /* when we use https, we need to allow any HTTPS cerificates, so add the one line code, use it only for test!
     */
    //    [NSURLRequest setAllowsAnyHTTPSCertificate:YES forHost:[url host]];
    
    [self startAsyncRequest:request onCompletion:block];
}

@end
