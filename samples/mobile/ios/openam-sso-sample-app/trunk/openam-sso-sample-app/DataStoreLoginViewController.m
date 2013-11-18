//
//  DataStoreLoginViewController.m
//  OpenAMSSO
//
//  Created by Phill on 12/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import "DataStoreLoginViewController.h"
#import "AuthenticationCallback.h"
#import "AuthenticationCallbackField.h"
#import "DataStoreLogin.h"

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
    if (!_dataStoreLogin) _dataStoreLogin = [[DataStoreLogin alloc] initWithCallbacks:self.response.callbacks];;
    return _dataStoreLogin;
}

- (IBAction)loginButtonAction:(id)sender {
    
    AuthenticationCallbackResponse *response = [self.dataStoreLogin setUserName:self.userNameField.text setPassword:self.passwordField.text onResponse:self.response];
    
    [self.delegate callbacksCompleted:response];
}

@end
