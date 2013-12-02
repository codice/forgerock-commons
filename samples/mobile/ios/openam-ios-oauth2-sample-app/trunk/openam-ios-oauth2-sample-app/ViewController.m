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

#import "ViewController.h"
#import "SettingsViewController.h"
#import "AuthViewController.h"
#import <openam-ios-rest-sdk/OpenAMRESTSDK.h>
#import "KeyChainWrapper.h"
#import "TokenInfoViewController.h"
#import "SimpleLogin.h"

@interface ViewController ()
@property (strong, nonatomic) NSTimer *refreshTimer;
@property (weak, nonatomic) IBOutlet UIView *tokenInfoViewContainer;
@end

@implementation ViewController

- (ServerSettings *)serverSettings {
    return [ServerSettings instance];
}

- (void)viewWillAppear:(BOOL)animated {
    if (![[ServerSettings instance] isConfigured]) {
        [self performSegueWithIdentifier:@"ToSettings" sender:self];
        return;
    }
    
    if (![self haveValidSSOToken]) {
        [[[SimpleLogin alloc] init] loginToServer:self.serverSettings.baseUri forUser:@"amadmin" withPassword:@"cangetin"];
    }
}

- (BOOL)haveValidSSOToken {
    NSString *ssoTokenId = [KeyChainWrapper searchKeychainCopyMatching:@"SSOTokenId"];
    return [[[OpenAMRESTSDK alloc] init] isTokenValid:ssoTokenId forServerInstance:[self.serverSettings baseUri]];
}

- (void)viewDidLoad {
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    if ([segue.identifier isEqualToString:@"ToOAuth"]) {
        AuthViewController *authVC = [segue.destinationViewController viewControllers][0];
        authVC.delegate = self;
    }
}

- (void)processGrantToken:(NSString *)grantToken {
    [[[OAuth2 alloc] initWithDelegate:self] getAccessTokenWithCode:grantToken];
}


- (NSURL *)openAMBaseUrl {
    return [NSURL URLWithString:[[ServerSettings instance] baseUri]];
}

- (NSURL *)redirectionUrl {
    return [NSURL URLWithString:[[ServerSettings instance] redirectionUrl]];
}

- (NSString *)scope {
    return [[ServerSettings instance] scope];
}

- (NSString *)realm {
   return [[ServerSettings instance] realm];
}

- (NSString *)clientId {
    return [[ServerSettings instance] clientId];
}

- (NSString *)clientSecret {
    return [[ServerSettings instance] clientSecret];
}

- (void)accessTokenCallback:(NSDictionary *)accessToken {
    NSLog(@"%@", accessToken);
    [self.serverSettings setValue:[accessToken valueForKey:@"access_token"] forKey:@"access_token"];
    [self.serverSettings setValue:[accessToken valueForKey:@"refresh_token"] forKey:@"refresh_token"];
    [self.serverSettings setValue:[accessToken valueForKey:@"expires_in"] forKey:@"expires_in"];

    // If the AM server instance can offer refresh tokens then uncomment this line to initiate timer to keep access token alive
    //    [self refreshTimer];
    [self reloadTokenInfoData];
}

- (void)enableRefreshTimer:(NSString *)accessToken {
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc]init];
    [dateFormatter setDateStyle:NSDateFormatterMediumStyle];
    [dateFormatter setTimeStyle:NSDateFormatterMediumStyle];
    NSDate *expiresDate = [dateFormatter dateFromString:[accessToken valueForKey:@"expires_in"]];
    
    NSTimeInterval expiresIn = [expiresDate timeIntervalSinceNow] - 5;
    [self.refreshTimer invalidate];
    self.refreshTimer = [NSTimer scheduledTimerWithTimeInterval:expiresIn target:self selector:@selector(handleRefreshTimer:) userInfo:nil repeats:NO];
}

- (void)handleRefreshTimer:(NSTimer *)timer {
    [timer invalidate];
    
    [[[OAuth2 alloc] initWithDelegate:self] refreshAccessToken:[self.serverSettings valueForKey:@"refresh_token"]];
}

- (void)refreshTokenCallback:(NSDictionary *)refreshToken {
    NSLog(@"%@", refreshToken);
    [self.serverSettings setValue:[refreshToken valueForKey:@"access_token"] forKey:@"access_token"];
    [self.serverSettings setValue:[refreshToken valueForKey:@"refresh_token"] forKey:@"refresh_token"];
    [self.serverSettings setValue:[refreshToken valueForKey:@"expires_in"] forKey:@"expires_in"];
    
    [self reloadTokenInfoData];
}
- (IBAction)revokeButtonAction:(id)sender {//TODO is there a way to tell server to revoke???? Must be...
    [self.serverSettings setValue:@"" forKey:@"access_token"];
    [self.serverSettings setValue:@"" forKey:@"refresh_token"];
    [self.serverSettings setValue:@"" forKey:@"expires_in"];
    
    [self reloadTokenInfoData];
}

- (void)reloadTokenInfoData {
    [[self.tokenInfoViewContainer subviews][0] reloadData];
}

@end
