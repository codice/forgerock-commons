//
//  TokenInfoViewController.m
//  openam-ios-oauth2-sample-app
//
//  Created by Phill on 27/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import "TokenInfoViewController.h"
#import "SettingsCell.h"

@interface TokenInfoViewController ()

@end

@implementation TokenInfoViewController

- (id)initWithStyle:(UITableViewStyle)style {
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    UIRefreshControl *refreshControl = [[UIRefreshControl alloc] init];
    [refreshControl addTarget:self action:@selector(refreshView) forControlEvents:UIControlEventValueChanged];
    self.refreshControl = refreshControl;
}

- (void)refreshView {
    [self.tableView reloadData];
    [self.refreshControl endRefreshing];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    NSLog(@"access_token=%@", [self.serverSettings valueForKey:@"access_token"]);
    NSLog(@"refresh_token=%@", [self.serverSettings valueForKey:@"refresh_token"]);
    NSLog(@"token_info=%@", self.tokenInfo);
    
    switch (section) {
        case 0: return 4;
        case 1: return [[self.tokenInfo valueForKey:@"scope"] count];
    }
    return 0;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"TokenInfoCellId";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
    
    NSString *title;
    NSString *value;
    switch (indexPath.section) {
        case 0: {
            if (indexPath.row == 0) {
                title = @"Access Token";
                value = [self.serverSettings valueForKey:@"access_token"];
            } else if (indexPath.row == 1) {
                title = @"Token Type";
                value = [self.tokenInfo valueForKey:@"token_type"];
            } else if (indexPath.row == 2) {
                title = @"Refresh Token";
                value = [self.serverSettings valueForKey:@"refresh_token"];
            } else if (indexPath.row == 3) {
                title = @"Expires at";
                value = [self.serverSettings valueForKey:@"expires_in"];
            }
            
            break;
        }
        case 1: {
            NSString *key = [[self.tokenInfo valueForKey:@"scope"] objectAtIndex:indexPath.row];
            title = key;
            value = [self.tokenInfo valueForKey:key];
            break;
        }
    }
    
    cell.textLabel.text = title;
    cell.detailTextLabel.text = value;
    
    return cell;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    if (section == 0) {
        return @"Token";
    } else {
        return @"Scope";
    }
}

- (IBAction)backAction:(UIBarButtonItem *)sender {
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (IBAction)revokeAction:(UIBarButtonItem *)sender {//TODO is there a way to tell server to revoke???? Must be...
    [self.serverSettings setValue:@"" forKey:@"access_token"];
    [self.serverSettings setValue:@"" forKey:@"refresh_token"];
    [self.serverSettings setValue:@"" forKey:@"expires_in"];

    [self dismissViewControllerAnimated:YES completion:nil];
}

@end
