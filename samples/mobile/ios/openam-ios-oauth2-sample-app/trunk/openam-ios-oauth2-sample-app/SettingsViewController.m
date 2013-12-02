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

#import "SettingsViewController.h"

@interface SettingsViewController ()
@property (weak, nonatomic) IBOutlet UITextField *baseUriField;
@property (weak, nonatomic) IBOutlet UITextField *clientIdField;
@property (weak, nonatomic) IBOutlet UITextField *clientSecretField;
@property (weak, nonatomic) IBOutlet UITextField *redirectionUrlField;
@property (weak, nonatomic) IBOutlet UITextField *scopeField;
@property (weak, nonatomic) UITextField *fieldInEditMode;
@end

@implementation SettingsViewController

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
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)doneButtonAction:(id)sender {
    
    [self.fieldInEditMode resignFirstResponder];
    
    //Save to settings
    [ServerSettings instance].baseUri = self.baseUriField.text;
    [ServerSettings instance].clientId = self.clientIdField.text;
    [ServerSettings instance].clientSecret = self.clientSecretField.text;
    [ServerSettings instance].redirectionUrl = self.redirectionUrlField.text;
    [ServerSettings instance].scope = self.scopeField.text;
    
    // Pop
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    [textField resignFirstResponder];
    return YES;
}

- (void)textFieldDidBeginEditing:(UITextField *)textField {
    self.fieldInEditMode = textField;
}

@end