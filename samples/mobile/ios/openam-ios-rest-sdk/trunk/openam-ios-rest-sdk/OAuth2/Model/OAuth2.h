//
//  OAuth2.h
//  openam-ios-rest-sdk
//
//  Created by Phill on 21/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "OAuth2Delegate.h"

@interface OAuth2 : NSObject

//designated initialiser
- (instancetype)initWithDelegate:(id<OAuth2Delegate>)delegate;

- (void)getAccessTokenWithCode:(NSString *)code;

- (NSDictionary *)getTokenInfo:(NSString *) accessToken;

- (void)refreshAccessToken:(NSString *)refreshToken;

@end
