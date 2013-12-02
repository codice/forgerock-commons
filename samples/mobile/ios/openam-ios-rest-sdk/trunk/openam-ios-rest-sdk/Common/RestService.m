//
//  RestService.m
//  openam-ios-rest-sdk
//
//  Created by Phill on 30/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import "RestService.h"
#import "RestConnection.h"
#import "HttpHelper.h"

@implementation RestService

+ (id)allocWithZone:(NSZone *)zone {
    return [self instance];
}

+ (RestService *)instance {
    static RestService *singleton = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        singleton = [[super allocWithZone:nil] init];
    });
    return singleton;
}

- (NSMutableURLRequest *)createRequest:(NSString *)url {
    return [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url] cachePolicy:NSURLRequestReloadIgnoringCacheData timeoutInterval:10];
}

- (void)startRequest:(NSURLRequest *)request onCompletion:(void (^)(NSDictionary *response, NSError *err))block {
    
    NSLog(@"startRequest:%@ :%@ :%@ :%@", request.HTTPMethod, request.URL.absoluteString, request.allHTTPHeaderFields, request.HTTPBody);//TODO only log when in debug mode...
    
    RestConnection *connection = [[RestConnection alloc] initWithRequest:request onCompletion:block];
    [connection start];
}

- (NSData *)convertToJson:(NSDictionary *)body {
    NSError *error;
    NSData * json = [NSJSONSerialization dataWithJSONObject:body options:NSJSONWritingPrettyPrinted error:&error];
    
    if (error) {
        NSLog(@"Failed to create json");
        //TODO ????
    }
    
    return json;
}

- (void)get:(NSString *)url withHeaders:(NSDictionary *)headers withParams:(NSDictionary *)params onCompletion:(void (^)(NSDictionary *response, NSError *err))block {
    
    NSLog(@"get:%@ :%@ :%@", url, headers, params);
    
    if (params) {
        NSString *encodedParams = [HttpHelper urlEncodeDictionary:params];
        url = [url stringByAppendingFormat:@"?%@", encodedParams];
    }
    
    NSMutableURLRequest *request = [self createRequest:url];//TODO only log when in debug mode...
    
    [request setAllHTTPHeaderFields:headers];
    [request setHTTPMethod:@"GET"];
    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];//TODO shouldn't really need this header but crest still expects it... I think...
    
    [self startRequest:request onCompletion:block];
}

- (void)post:(NSString *)url withHeaders:(NSDictionary *)headers withParams:(NSDictionary *)params withBody:(NSDictionary *)body onCompletion:(void (^)(NSDictionary *response, NSError *err))block {
    
    NSLog(@"post:%@ :%@ :%@ :%@", url, headers, params, body);
    
    if (params) {
        NSString *encodedParams = [HttpHelper urlEncodeDictionary:params];
        url = [url stringByAppendingFormat:@"?%@", encodedParams];
    }
    
    NSMutableURLRequest *request = [self createRequest:url];//TODO only log when in debug mode...
    NSData *json = [self convertToJson:body];
    
    [request setAllHTTPHeaderFields:headers];
    [request setHTTPMethod:@"POST"];
    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [request setHTTPBody:json];   //[body dataUsingEncoding:NSUTF8StringEncoding]];
    NSString *postLength = [NSString stringWithFormat:@"%lu", (unsigned long)[json length]];
    [request setValue:postLength forHTTPHeaderField:@"Content-Length"];
    
    [self startRequest:request onCompletion:block];
}

- (void)put:(NSString *)url withHeaders:(NSDictionary *)headers withParams:(NSDictionary *)params withBody:(NSDictionary *)body onCompletion:(void (^)(NSDictionary *response, NSError *err))block {
    
    NSLog(@"put:%@ :%@ :%@ :%@", url, headers, params, body);//TODO only log when in debug mode...
    
    if (params) {
        NSString *encodedParams = [HttpHelper urlEncodeDictionary:params];
        url = [url stringByAppendingFormat:@"?%@", encodedParams];
    }
    
    NSMutableURLRequest *request = [self createRequest:url];
    NSData *json = [self convertToJson:body];
    
    [request setAllHTTPHeaderFields:headers];
    [request setHTTPMethod:@"PUT"];
    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [request setHTTPBody:json];   //[body dataUsingEncoding:NSUTF8StringEncoding]];
    NSString *postLength = [NSString stringWithFormat:@"%lu", (unsigned long)[json length]];
    [request setValue:postLength forHTTPHeaderField:@"Content-Length"];
    
    [self startRequest:request onCompletion:block];
}

- (void)patch:(NSString *)url withHeaders:(NSDictionary *)headers withParams:(NSDictionary *)params withBody:(NSDictionary *)body onCompletion:(void (^)(NSDictionary *response, NSError *err))block {
    
    NSLog(@"patch:%@ :%@ :%@ :%@", url, headers, params, body);//TODO only log when in debug mode...
    
    if (params) {
        NSString *encodedParams = [HttpHelper urlEncodeDictionary:params];
        url = [url stringByAppendingFormat:@"?%@", encodedParams];
    }
    
    NSMutableURLRequest *request = [self createRequest:url];
    NSData *json = [self convertToJson:body];
    
    [request setAllHTTPHeaderFields:headers];
    [request setHTTPMethod:@"PATCH"];
    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [request setHTTPBody:json];   //[body dataUsingEncoding:NSUTF8StringEncoding]];
    NSString *postLength = [NSString stringWithFormat:@"%lu", (unsigned long)[json length]];
    [request setValue:postLength forHTTPHeaderField:@"Content-Length"];
    
    [self startRequest:request onCompletion:block];
}

- (void)delete:(NSString *)url withHeaders:(NSDictionary *)headers withParams:(NSDictionary *)params withBody:(NSDictionary *)body onCompletion:(void (^)(NSDictionary *response, NSError *err))block {
    
    NSLog(@"delete:%@ :%@ :%@ :%@", url, headers, params, body);//TODO only log when in debug mode...
    
    if (params) {
        NSString *encodedParams = [HttpHelper urlEncodeDictionary:params];
        url = [url stringByAppendingFormat:@"?%@", encodedParams];
    }
    
    NSMutableURLRequest *request = [self createRequest:url];
    NSData *json = [self convertToJson:body];
    
    [request setAllHTTPHeaderFields:headers];
    [request setHTTPMethod:@"DELETE"];
    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [request setHTTPBody:json];   //[body dataUsingEncoding:NSUTF8StringEncoding]];
    NSString *postLength = [NSString stringWithFormat:@"%lu", (unsigned long)[json length]];
    [request setValue:postLength forHTTPHeaderField:@"Content-Length"];
    
    [self startRequest:request onCompletion:block];
}

@end
