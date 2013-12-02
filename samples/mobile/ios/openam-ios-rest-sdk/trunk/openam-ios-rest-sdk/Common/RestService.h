//
//  RestService.h
//  openam-ios-rest-sdk
//
//  Created by Phill on 30/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface RestService : NSObject

+ (RestService *)instance;

- (NSMutableURLRequest *)createRequest:(NSString *)url;

- (void)get:(NSString *)url withHeaders:(NSDictionary *)headers withParams:(NSDictionary *)params onCompletion:(void (^)(NSDictionary *response, NSError *err))block;

- (void)post:(NSString *)url withHeaders:(NSDictionary *)headers withParams:(NSDictionary *)params withBody:(NSDictionary *)body onCompletion:(void (^)(NSDictionary *response, NSError *err))block;

- (void)put:(NSString *)url withHeaders:(NSDictionary *)headers withParams:(NSDictionary *)params withBody:(NSDictionary *)body onCompletion:(void (^)(NSDictionary *response, NSError *err))block;

- (void)patch:(NSString *)url withHeaders:(NSDictionary *)headers withParams:(NSDictionary *)params withBody:(NSDictionary *)body onCompletion:(void (^)(NSDictionary *response, NSError *err))block;

- (void)delete:(NSString *)url withHeaders:(NSDictionary *)headers withParams:(NSDictionary *)params withBody:(NSDictionary *)body onCompletion:(void (^)(NSDictionary *response, NSError *err))block;

@end
