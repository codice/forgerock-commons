//
//  SettingsViewController.h
//  OpenAMOAuth2SampleApp
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ServerSettings.h"

@interface SettingsViewController : UITableViewController <UITableViewDataSource, UITextFieldDelegate>

@property (strong, nonatomic) ServerSettings* serverSettings;

@end
