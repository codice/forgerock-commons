/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2013 ForgeRock, AS.
 */

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
