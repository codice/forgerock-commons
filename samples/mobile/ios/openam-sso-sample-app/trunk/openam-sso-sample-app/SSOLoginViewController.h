//
//  SSOLoginViewController.h
//  OpenAMSSO
//
//  Created by Phill on 12/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <openam-ios-rest-sdk/RootLoginViewController.h>
#import <openam-ios-rest-sdk/AuthenticationProcess.h>
#import "ServerSettings.h"
#import "LoginViewController.h"

@protocol SSOLoginViewControllerDelegate <NSObject>
- (void)loginSucessful:(id)controller didFinishEnteringItem:(NSString *)item;
@end

@interface SSOLoginViewController : LoginViewController <RootLoginViewController, AuthenticationProcessDelegate>

@end
