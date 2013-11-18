//
//  AuthenticationProcess.h
//  OpenAMRESTSDK
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "AuthenticationCallbackResponse.h"
#import "AuthenticationFailureResponse.h"
#import "AuthenticationSuccessResponse.h"

@protocol AuthenticationProcessDelegate <NSObject>

- (NSString *)authenticateTo;

- (void)responseReceivedWithCallbacks:(AuthenticationCallbackResponse *)response;

- (void)authenticationFailedWithResult:(AuthenticationFailureResponse *)response;

- (void)authenticationSucceededWithResult:(AuthenticationSuccessResponse *)response;

@end


@interface AuthenticationProcess : NSObject

- (void)start:(id <AuthenticationProcessDelegate>)delegate;

- (void)submitCallbacks:(NSDictionary *)callbacks;

@end
