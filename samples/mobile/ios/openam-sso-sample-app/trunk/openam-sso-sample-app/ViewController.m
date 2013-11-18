//
//  ViewController.m
//  OpenAMOAuth2SampleApp
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import "ViewController.h"
#import "SettingsViewController.h"
#import "SSOLoginViewController.h"
#import "KeyChainWrapper.h"
#import "LoginViewController.h"

@interface ViewController ()
@property (weak, nonatomic) IBOutlet UILabel *nameLabel;

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
}

- (void)viewDidAppear:(BOOL)animated {
    
    NSString *data = [KeyChainWrapper searchKeychainCopyMatching:@"SSOTokenId"];
    
    if (data) {
        self.nameLabel.text = data;
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    if ([segue.identifier isEqualToString:@"ToSettings"]) {
        SettingsViewController *settingsVC = [segue.destinationViewController viewControllers][0];
        settingsVC.serverSettings = self.serverSettings;
    } else if ([segue.identifier isEqualToString:@"ToAuthn"]) {
        LoginViewController *loginVC = [segue.destinationViewController viewControllers][0];
        loginVC.serverSettings = self.serverSettings;
    }
}

@end
