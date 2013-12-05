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

#import "SSOLoginViewController.h"
#import <openam-ios-rest-sdk/OpenAMRESTSDK.h>
#import "DataStoreLoginViewController.h"
#import "KeyChainWrapper.h"

@interface SSOLoginViewController ()
@property (strong, nonatomic) AuthenticationProcess *authenticationProcess;

@property (strong, nonatomic, readonly) ServerSettings* serverSettings;
@end

@implementation SSOLoginViewController

- (ServerSettings *)serverSettings {
    return [ServerSettings instance];
}

- (void)viewWillAppear:(BOOL)animated {
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    OpenAMRESTSDK *restSdk = [[OpenAMRESTSDK alloc] init];
    
    self.authenticationProcess = [restSdk authenticate];
    [self.authenticationProcess startAuthenticationToServer:[self.serverSettings valueForKey:@"OPENAM_URL_SETTING_KEY"] delegate:self];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (NSString *)authenticateTo {
    return [self.serverSettings valueForKey:@"OPENAM_URL_SETTING_KEY"];
}

- (void)responseReceivedWithCallbacks:(AuthenticationCallbackResponse *)response {
    
    if ([response.stage hasPrefix:@"DataStore"]) {
        [self performSegueWithIdentifier:@"ToDataStoreLogin" sender:response];
    }
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    if ([segue.identifier isEqualToString:@"ToDataStoreLogin"]) {
        DataStoreLoginViewController *dataStoreLoginVC = segue.destinationViewController;
        dataStoreLoginVC.response = sender;
        dataStoreLoginVC.delegate = self;
    }
}

- (void)authenticationFailedWithResult:(AuthenticationFailureResponse *)response {
    //TODO present error message
    UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Authentication Failed." delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [alertView show];
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)authenticationSucceededWithResult:(AuthenticationSuccessResponse *)response {
    
    if (![KeyChainWrapper createKeychainValue:response.tokenId forIdentifier:@"SSOTokenId"]) {
        [KeyChainWrapper updateKeychainValue:response.tokenId forIdentifier:@"SSOTokenId"];
    }
    
    self.serverSettings.ssoTokenId = response.tokenId;
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)callbacksCompleted:(AuthenticationCallbackResponse *)response {
    [self.authenticationProcess submitCallbacks:response.asData];
}

- (void)authenticationFailed:(NSError *)error {
    UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Authentication Failed." delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [alertView show];
    [self dismissViewControllerAnimated:YES completion:nil];
}

@end
