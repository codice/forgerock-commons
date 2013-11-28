//
//  OAuth2Delegate.h
//  openam-ios-rest-sdk
//
//  Created by Phill on 26/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol OAuth2Delegate <NSObject>

- (NSURL *)openAMBaseUrl;

- (NSURL *)redirectionUrl;

- (NSString *)scope;

- (NSString *)realm;

- (NSString *)clientId;

- (NSString *)clientSecret;

- (void)accessTokenCallback:(NSDictionary *)accessToken;

- (void)refreshTokenCallback:(NSDictionary *)refreshToken;

@end
