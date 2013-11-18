//
//  DataStoreLogin.h
//  OpenAMRESTSDK
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "AuthenticationCallbackResponse.h"

@interface DataStoreLogin : NSObject

@property (strong, nonatomic, readonly) NSString *userNameLabel;
@property (strong, nonatomic, readonly) NSString *userNameField;
@property (strong, nonatomic, readonly) NSString *passwordLabel;
@property (strong, nonatomic, readonly) NSString *passwordField;

// designated initialiser
- (instancetype)initWithCallbacks:(NSArray *)callbacks;

- (AuthenticationCallbackResponse *)setUserName:(NSString *)username setPassword:(NSString *)password onResponse:(AuthenticationCallbackResponse *)response;

@end
