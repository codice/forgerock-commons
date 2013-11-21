//
//  DataStoreLoginViewController.h
//  OpenAMSSO
//
//  Created by Phill on 12/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <openam-ios-rest-sdk/AuthenticationCallbackResponse.h>
#import <openam-ios-rest-sdk/RootLoginViewController.h>

@interface DataStoreLoginViewController : UIViewController

@property (weak, nonatomic) IBOutlet UILabel *userNameLabel;
@property (weak, nonatomic) IBOutlet UILabel *passwordLabel;
@property (weak, nonatomic) IBOutlet UITextField *userNameField;
@property (weak, nonatomic) IBOutlet UITextField *passwordField;

@property (strong, nonatomic) AuthenticationCallbackResponse *response;
@property (strong, nonatomic) id<RootLoginViewController> delegate;

@end
