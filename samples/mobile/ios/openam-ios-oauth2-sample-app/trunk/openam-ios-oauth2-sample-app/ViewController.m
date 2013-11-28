//
//  ViewController.m
//  OpenAMOAuth2SampleApp
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import "ViewController.h"
#import "SettingsViewController.h"
#import "AuthViewController.h"
#import <openam-ios-rest-sdk/OpenAMRESTSDK.h>
#import "KeyChainWrapper.h"
#import "TokenInfoViewController.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (BOOL)shouldPerformSegueWithIdentifier:(NSString *)identifier sender:(id)sender {
    
    BOOL isTokenValid = [[[OpenAMRESTSDK alloc] init] isTokenValid:[KeyChainWrapper searchKeychainCopyMatching:@"SSOTokenId"] forServerInstance:[self.serverSettings valueForKey:@"OPENAM_URL_SETTING_KEY"]];
    
    if ([identifier isEqualToString:@"ToOAuth"] && !isTokenValid) {
        
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"Error" message:@"You do not have a valid SSO Token Id. Login using the OpenAM SSO App first." delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alertView show];
        
        
        //TODO can only test this on an actual physical device!
//        NSURL *myURL = [NSURL URLWithString:@"sso://www.acme.com?Quarterly%20Report#200806231300"];
//        [[UIApplication sharedApplication] openURL:myURL];
        
        
//        NSURL *ourURL = [NSURL URLWithString:@"http://maps.google.com/maps?ll=-37.812022,144.969277"];
//        [[UIApplication sharedApplication] openURL:ourURL];
        return NO;
    }
    
    if ([identifier isEqualToString:@"ToTokenInfo"]) {
        
        NSString *accessToken = [self.serverSettings valueForKey:@"access_token"];
        
        NSDictionary *tokenInfo = [[[OAuth2 alloc] initWithDelegate:self] getTokenInfo:accessToken];

        if ([tokenInfo objectForKey:@"error"]) {
            UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"Error" message:@"You do not have a valid OAuth Token. Authorize first." delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [alertView show];
            
            return NO;
        }
    }
    
    return YES;
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    if ([segue.identifier isEqualToString:@"ToSettings"]) {
        SettingsViewController *settingsVC = [segue.destinationViewController viewControllers][0];
        settingsVC.serverSettings = self.serverSettings;
    } else if ([segue.identifier isEqualToString:@"ToOAuth"]) {
        AuthViewController *authVC = [segue.destinationViewController viewControllers][0];
        authVC.serverSettings = self.serverSettings;
        authVC.delegate = self;
    } else if ([segue.identifier isEqualToString:@"ToTokenInfo"]) {
        
        NSString *accessToken = [self.serverSettings valueForKey:@"access_token"];

        NSDictionary *tokenInfo = [[[OAuth2 alloc] initWithDelegate:self] getTokenInfo:accessToken];
        
        TokenInfoViewController *tokenInfoVC = [segue.destinationViewController viewControllers][0];
        tokenInfoVC.serverSettings = self.serverSettings;
        tokenInfoVC.tokenInfo = tokenInfo;
    }
}

- (void)processGrantToken:(NSString *)grantToken {
    [[[OAuth2 alloc] initWithDelegate:self] getAccessTokenWithCode:grantToken];
}


- (NSURL *)openAMBaseUrl{
    return [NSURL URLWithString:[self.serverSettings valueForKey:@"OPENAM_URL_SETTING_KEY"]];
}

- (NSURL *)redirectionUrl {
    return [NSURL URLWithString:[self.serverSettings valueForKey:@"OAUTH2_REDIRECT_URI_SETTING_KEY"]];
}

- (NSString *)scope {
    return [self.serverSettings valueForKey:@"OAUTH2_SCOPE_SETTING_KEY"];
}

- (NSString *)realm {
    return [self.serverSettings valueForKey:@"OPENAM_REALM_SETTING_KEY"];
}

- (NSString *)clientId {
    return [self.serverSettings valueForKey:@"OAUTH2_CLIENT_ID_SETTING_KEY"];
}

- (NSString *)clientSecret {
    return [self.serverSettings valueForKey:@"OAUTH2_CLIENT_SECRET_SETTING_KEY"];
}

- (void)accessTokenCallback:(NSDictionary *)accessToken {
    NSLog(@"%@", accessToken);
    [self.serverSettings setValue:[accessToken valueForKey:@"access_token"] forKey:@"access_token"];
    [self.serverSettings setValue:[accessToken valueForKey:@"refresh_token"] forKey:@"refresh_token"];
    [self.serverSettings setValue:[accessToken valueForKey:@"expires_in"] forKey:@"expires_in"];

//    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc]init];
//    [dateFormatter setDateStyle:NSDateFormatterMediumStyle];
//    [dateFormatter setTimeStyle:NSDateFormatterMediumStyle];
//    NSDate *expiresDate = [dateFormatter dateFromString:[accessToken valueForKey:@"expires_in"]];
    
//    NSTimeInterval expiresIn = [expiresDate timeIntervalSinceNow] - 5;
//    [self.refreshTimer invalidate];
//    self.refreshTimer = [NSTimer scheduledTimerWithTimeInterval:expiresIn target:self selector:@selector(handleRefreshTimer:) userInfo:nil repeats:NO];
}

- (void)handleRefreshTimer:(NSTimer *)timer {
    [timer invalidate];
    
    [[[OAuth2 alloc] initWithDelegate:self] refreshAccessToken:[self.serverSettings valueForKey:@"refresh_token"]];
}

- (void)refreshTokenCallback:(NSDictionary *)refreshToken {
//    NSLog(@"%@", accessToken);
//    [self.serverSettings setValue:[refreshToken valueForKey:@"access_token"] forKey:@"access_token"];
//    [self.serverSettings setValue:[refreshToken valueForKey:@"refresh_token"] forKey:@"refresh_token"];
//    [self.serverSettings setValue:[refreshToken valueForKey:@"expires_in"] forKey:@"expires_in"];
    NSString *s = @"";
}


@end
