//
//  RestSyncConnection.h
//  openam-ios-rest-sdk
//
//  Created by Phill on 30/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface RestSyncConnection : NSObject

@property (nonatomic, strong, readonly) NSDictionary *response;

- (instancetype)initWithRequest:(NSURLRequest *)request;

- (NSDictionary *)startWithError:(NSError *)error;

@end
