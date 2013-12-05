//
//  MainViewController.m
//  openam-sso-sample-app
//
//  Created by Phill on 28/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import "MainViewController.h"
#import "SettingsCell.h"

@interface MainViewController ()

@end

@implementation MainViewController

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];

    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;
 
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2;
}

- (NSString*)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    switch (section) {
        case 0: return @"User Details";
        case 1: return nil;
    }
    return nil;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    switch (section) {
        case 0: {
            return 1;
        }
        case 1: {
            return 3;
        }
    }
    return 0;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    UITableViewCell *cell;
    
    switch (indexPath.section) {
        case 0: {
            SettingsCell *settingsCell = [tableView dequeueReusableCellWithIdentifier:@"UserProfileCellId" forIndexPath:indexPath];
            if (indexPath.row == 0) {
                settingsCell.textLabel.text = @"User";
                settingsCell.textValue.text = @"???";
            }
            cell = settingsCell;
        }
        case 1: {
            if (indexPath.row == 0) {
                cell = [tableView dequeueReusableCellWithIdentifier:@"AuthenticateCellId" forIndexPath:indexPath];
            } else if (indexPath.row == 1) {
                cell = [tableView dequeueReusableCellWithIdentifier:@"LogoutCellId" forIndexPath:indexPath];
            } else if (indexPath.row == 2) {
                cell = [tableView dequeueReusableCellWithIdentifier:@"SettingsCellId" forIndexPath:indexPath];
            }
        }
    }
    
    return cell;
}


@end
