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

#import "DataStoreLoginViewController.h"
#import <openam-ios-rest-sdk/AuthenticationCallback.h>
#import <openam-ios-rest-sdk/AuthenticationCallbackField.h>
#import <openam-ios-rest-sdk/DataStoreLogin.h>

@interface DataStoreLoginViewController ()
@property (strong, nonatomic) DataStoreLogin *dataStoreLogin;
@end

@implementation DataStoreLoginViewController

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
    
    self.userNameLabel.text = self.dataStoreLogin.userNameLabel;
    self.userNameField.text = self.dataStoreLogin.userNameField;
    self.passwordLabel.text = self.dataStoreLogin.passwordLabel;
    self.passwordField.text = self.dataStoreLogin.passwordField;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (DataStoreLogin *)dataStoreLogin {
    if (!_dataStoreLogin) _dataStoreLogin = [[DataStoreLogin alloc] initWithCallbacks:self.response.callbacks];
    return _dataStoreLogin;
}

- (IBAction)loginButtonAction:(id)sender {
    
    AuthenticationCallbackResponse *response = [self.dataStoreLogin setUserName:self.userNameField.text setPassword:self.passwordField.text onResponse:self.response];
    
    [self.delegate callbacksCompleted:response];
}

@end
