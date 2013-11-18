//
//  DataStoreLogin.m
//  OpenAMRESTSDK
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import "DataStoreLogin.h"
#import "AuthenticationCallback.h"
#import "AuthenticationCallbackField.h"

@interface DataStoreLogin()
@property (strong, nonatomic, readwrite) NSString *userNameLabel;
@property (strong, nonatomic, readwrite) NSString *userNameField;
@property (strong, nonatomic, readwrite) NSString *passwordLabel;
@property (strong, nonatomic, readwrite) NSString *passwordField;
@end

@implementation DataStoreLogin

- (instancetype)init {
    return nil;
}

- (instancetype)initWithCallbacks:(NSArray *)callbacks {
    
    self = [super init];
    
    if (self) {
        
        for (AuthenticationCallback *callback in callbacks) {
            AuthenticationCallbackField *outputField = callback.outputs[0];
            AuthenticationCallbackField *inputField = callback.inputs[0];
            if ([callback.type isEqualToString:@"NameCallback"]) {
                self.userNameLabel = outputField.value;
                self.userNameField = inputField.value;
            } else if ([callback.type isEqualToString:@"PasswordCallback"]) {
                self.passwordLabel = outputField.value;
                self.passwordField = inputField.value;
            }
        }
    }
    
    return self;
}

- (AuthenticationCallbackResponse *)setUserName:(NSString *)username setPassword:(NSString *)password onResponse:(AuthenticationCallbackResponse *)response {
    
    for (AuthenticationCallback *callback in response.callbacks) {
        AuthenticationCallbackField *inputField = callback.inputs[0];
        if ([callback.type isEqualToString:@"NameCallback"]) {
            inputField.value = username;
        } else if ([callback.type isEqualToString:@"PasswordCallback"]) {
            inputField.value = password;
        }
    }
    
    return response;
}

@end
