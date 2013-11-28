//
//  OpenAMRESTSDK.h
//  OpenAMRESTSDK
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RootLoginViewController.h"
#import "AuthenticationProcess.h"
#import "AuthenticationSuccessResponse.h"
#import "AuthenticationFailureResponse.h"
#import "AuthenticationCallbackResponse.h"
#import "AuthenticationCallback.h"
#import "AuthenticationCallbackField.h"
#import "DataStoreLogin.h"
#import "HttpHelper.h"
#import "OAuth2.h"
#import "OAuth2Delegate.h"

@interface OpenAMRESTSDK : NSObject <NSURLConnectionDelegate>

extern NSString * const OPENAM_BASE_URL;

- (AuthenticationProcess *)authenticate;

- (BOOL)isTokenValid:(NSString *)ssoTokenId forServerInstance:(NSString *)openAMBaseUrl;

@end
