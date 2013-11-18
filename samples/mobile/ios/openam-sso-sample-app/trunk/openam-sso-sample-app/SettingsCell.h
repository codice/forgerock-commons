//
//  SettingsCell.h
//  OpenAMOAuth2SampleApp
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <UIKit/UIKit.h>
//#import "SettingsViewController.h"

@interface SettingsCell : UITableViewCell

@property (weak, nonatomic) IBOutlet UILabel *textLabel;
@property (weak, nonatomic) IBOutlet UITextField *textValue;

//@property (nonatomic, copy) SettingsViewController *delegate;

@end
