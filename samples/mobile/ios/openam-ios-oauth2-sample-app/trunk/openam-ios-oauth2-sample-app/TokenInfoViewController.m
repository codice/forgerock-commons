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

#import "TokenInfoViewController.h"
#import <openam-ios-rest-sdk/OpenAMRESTSDK.h>

@interface TokenInfoViewController ()
@property (nonatomic, weak, readonly) ServerSettings *serverSettings;
@property (strong, nonatomic) NSDictionary *tokenInfo;
@end

@implementation TokenInfoViewController

- (ServerSettings *)serverSettings {
    return [ServerSettings instance];
}

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

- (NSDictionary *)tokenInfo {
    NSString *accessToken = [self.serverSettings valueForKey:@"access_token"];
    if ([accessToken length] == 0) {
        return nil;
    }
    if (!_tokenInfo) {
        if (accessToken) {
            _tokenInfo = [[[OAuth2 alloc] initWithDelegate:nil] tokenInfoFfromServer:self.serverSettings.baseUri for:accessToken];
        }
    }
    return _tokenInfo;
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
