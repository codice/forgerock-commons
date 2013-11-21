//
//  SSOLoginViewController.m
//  OpenAMSSO
//
//  Created by Phill on 12/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import "SSOLoginViewController.h"
#import <openam-ios-rest-sdk/OpenAMRESTSDK.h>
#import "DataStoreLoginViewController.h"
#import "KeyChainWrapper.h"

@interface SSOLoginViewController ()
@property (strong, nonatomic) AuthenticationProcess *authenticationProcess;
@end

@implementation SSOLoginViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    
    OpenAMRESTSDK *restSdk = [[OpenAMRESTSDK alloc] init];
    
    self.authenticationProcess = [restSdk authenticate];
    
    [self.authenticationProcess start:self];
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
    
    NSLog(@"responseReceivedWithCallbacks");
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    if ([segue.identifier isEqualToString:@"ToDataStoreLogin"]) {
        DataStoreLoginViewController *dataStoreLoginVC = segue.destinationViewController;
        dataStoreLoginVC.response = sender;
        dataStoreLoginVC.delegate = self;
    }
}

- (void)authenticationFailedWithResult:(AuthenticationFailureResponse *)response {
    [self dismissViewControllerAnimated:NO completion:nil];
    NSLog(@"authenticationFailedWithResult");
}

- (void)authenticationSucceededWithResult:(AuthenticationSuccessResponse *)response {
    
    if (![KeyChainWrapper createKeychainValue:response.tokenId forIdentifier:@"SSOTokenId"]) {
        [KeyChainWrapper updateKeychainValue:response.tokenId forIdentifier:@"SSOTokenId"];
    }
    
    self.serverSettings.ssoTokenId = response.tokenId;
    [self dismissViewControllerAnimated:NO completion:nil];
    NSLog(@"authenticationSucceededWithResult");
}

- (void)callbacksCompleted:(AuthenticationCallbackResponse *)response {
    [self.authenticationProcess submitCallbacks:response.asData];
}

@end
