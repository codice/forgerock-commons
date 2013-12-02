//
//  RestishService.h
//  openam-ios-rest-sdk
//
//  Created by Phill on 30/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface LegacyRestService : NSObject

+ (LegacyRestService *)instance;

- (void)get:(NSString *)url withHeaders:(NSDictionary *)headers withParams:(NSDictionary *)params onCompletion:(void (^)(NSDictionary *response, NSError *err))block;

- (NSDictionary *)get:(NSString *)url withHeaders:(NSDictionary *)headers withParams:(NSDictionary *)params;

- (void)post:(NSString *)url withHeaders:(NSDictionary *)headers withFormParams:(NSDictionary *)params onCompletion:(void (^)(NSDictionary *response, NSError *err))block;

@end
