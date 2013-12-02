//
//  SimpleLogin.m
//  openam-ios-oauth2-sample-app
//
//  Created by Phill on 02/12/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import "SimpleLogin.h"
#import "ServerSettings.h"
#import "KeyChainWrapper.h"

@interface SimpleLogin()
@end

@implementation SimpleLogin

- (void)loginToServer:(NSString *)baseUri forUser:(NSString *)username withPassword:(NSString *)password {
    
    OpenAMRESTSDK *restSdk = [[OpenAMRESTSDK alloc] init];
    
    [[restSdk authenticate] startZPLAuthenticationToServer:[ServerSettings instance].baseUri forUser:username withPassword:password delegate:self];
}

- (void)authenticationSucceededWithResult:(AuthenticationSuccessResponse *)response {
    
    if (![KeyChainWrapper createKeychainValue:response.tokenId forIdentifier:@"SSOTokenId"]) {
        [KeyChainWrapper updateKeychainValue:response.tokenId forIdentifier:@"SSOTokenId"];
    }
}

- (void)authenticationFailedWithResult:(AuthenticationFailureResponse *)response {
    [self showFailedMessage];
}

- (void)showFailedMessage {
    UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Failed to Authenticate." delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [alertView show];
}

- (void)responseReceivedWithCallbacks:(AuthenticationCallbackResponse *)response {
    [self showFailedMessage];
}

- (void)authenticationFailed:(NSError *)error {
    [self showFailedMessage];
}

@end
