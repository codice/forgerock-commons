//
//  LoginViewController.h
//  OpenAMSSO
//
//  Created by Phill on 18/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ServerSettings.h"

@interface LoginViewController : UIViewController

@property (strong, nonatomic) ServerSettings *serverSettings;

@end
