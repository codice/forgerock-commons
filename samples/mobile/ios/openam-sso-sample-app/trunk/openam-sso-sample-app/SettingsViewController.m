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
#import "SettingsProperty.h"
#import "SettingsCell.h"

@interface SettingsViewController ()
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (strong, nonatomic) NSDictionary *openamSettings;
@property (weak, nonatomic) UITextField *fieldInEditMode;

@property (strong, nonatomic, readonly) ServerSettings* serverSettings;

@end

@implementation SettingsViewController

- (ServerSettings *)serverSettings {
    return [ServerSettings instance];
}

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

- (NSDictionary *)openamSettings {
    if (!_openamSettings) {
        
        NSArray *contents = [NSArray arrayWithObjects:
                             [SettingsProperty initWithLabel:@"OpenAM URL"
                                                 placeHolder:@"http://<openam_fqdn>/openam"
                                                       value:[self.serverSettings valueForKey:@"OPENAM_URL_SETTING_KEY"]
                                                keyboardType:UIKeyboardTypeURL],
                             
                             [SettingsProperty initWithLabel:@"Realm"
                                                 placeHolder:@"/"
                                                       value:[self.serverSettings valueForKey:@"OPENAM_REALM_SETTING_KEY"]
                                                keyboardType:UIKeyboardTypeURL],
                             
                             [SettingsProperty initWithLabel:@"Authentication Service"
                                                 placeHolder:@"ldapService"
                                                       value:[self.serverSettings valueForKey:@"OPENAM_AUTH_SERVICE_SETTING_KEY"]],
                             
                             [SettingsProperty initWithLabel:@"Session Cookie"
                                                 placeHolder:@"iPlanetDirectoryPro"
                                                       value:[self.serverSettings valueForKey:@"OPENAM_COOKIE_NAME_SETTING_KEY"]],
                             
                             nil];
        
        NSArray *keys = self.serverSettings.openamSettingsKeys;
        
        _openamSettings = [[NSDictionary alloc] initWithObjects:contents forKeys:keys];
    }
    return _openamSettings;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.serverSettings.openamSettingsKeys.count;
}


//- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
//    // The header for the section is the region name -- get this from the region at the section index.
//    Region *region = [regions objectAtIndex:section];
//    return [region name];
//}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *displayCellId = @"SettingsCellId";
    SettingsCell *cell = [tableView dequeueReusableCellWithIdentifier:displayCellId];
    if (cell == nil) {
        cell = [[SettingsCell alloc] initWithStyle:UITableViewCellStyleDefault  reuseIdentifier:displayCellId];
    }
    
    NSDictionary *settings = self.openamSettings;
    NSArray *settingsKeys = self.serverSettings.openamSettingsKeys;
    
    SettingsProperty *property = [settings valueForKey:[settingsKeys objectAtIndex:indexPath.row]];
    cell.textLabel.text = property.textLabel;
    cell.textValue.delegate = self;
    cell.textValue.placeholder = property.textPlaceholder;
    cell.textValue.text = property.textValue;
    cell.textValue.keyboardType = property.keyboardType;
    return cell;
}

- (IBAction)saveSettings:(UIBarButtonItem *)sender {
    
    [self.fieldInEditMode resignFirstResponder];
    
    [self updateServerSettings:self.openamSettings];
    
    [self.serverSettings saveSettings];
    
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)updateServerSettings:(NSDictionary *)settings {
    
    for (NSString *key in [settings allKeys]) {
        NSString *value = [[settings valueForKey:key] textValue];
        [self.serverSettings setValue:value forKey:key];
    }
}

- (IBAction)cancelSettings:(UIBarButtonItem *)sender {
    
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    [textField resignFirstResponder];
    return YES;
}

- (void)textFieldDidBeginEditing:(UITextField *)textField {
    self.fieldInEditMode = textField;
}

- (void)textFieldDidEndEditing:(UITextField *)textField {
    
    SettingsCell *cell = (SettingsCell*)textField.superview.superview.superview; 
    
    NSIndexPath *indexPath = [self.tableView indexPathForCell:cell];
    
    NSDictionary *settings = self.openamSettings;
    NSArray *settingsKeys = self.serverSettings.openamSettingsKeys;
    
    SettingsProperty *property = [settings valueForKey:[settingsKeys objectAtIndex:indexPath.row]];
    property.textValue = textField.text;
}

@end