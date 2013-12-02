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
