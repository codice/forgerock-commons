//
//  SimpleLogin.h
//  openam-ios-oauth2-sample-app
//
//  Created by Phill on 02/12/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <openam-ios-rest-sdk/OpenAMRESTSDK.h>

@interface SimpleLogin : NSObject <AuthenticationProcessDelegate>

- (void)loginToServer:(NSString *)baseUri forUser:(NSString *)username withPassword:(NSString *)password;

@end
