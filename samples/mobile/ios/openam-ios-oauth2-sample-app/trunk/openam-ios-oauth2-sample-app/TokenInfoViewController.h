//
//  TokenInfoViewController.h
//  openam-ios-oauth2-sample-app
//
//  Created by Phill on 27/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ServerSettings.h"

@interface TokenInfoViewController : UITableViewController

@property (strong, nonatomic) ServerSettings* serverSettings;
@property (strong, nonatomic) NSDictionary *tokenInfo;

@end
