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
#import "SSOLoginViewController.h"
#import "KeyChainWrapper.h"

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

//- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
//    if ([segue.identifier isEqualToString:@"ToSettings"]) {
//        SettingsViewController *settingsVC = [segue.destinationViewController viewControllers][0];
////        settingsVC.serverSettings = self.serverSettings;
//    } else if ([segue.identifier isEqualToString:@"ToAuthn"]) {
//        LoginViewController *loginVC = [segue.destinationViewController viewControllers][0];
////        loginVC.serverSettings = self.serverSettings;
//    }
//}

@end
