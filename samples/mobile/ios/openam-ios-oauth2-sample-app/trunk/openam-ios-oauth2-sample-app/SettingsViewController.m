//
//  SettingsViewController.m
//  OpenAMOAuth2SampleApp
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import "SettingsViewController.h"
#import "SettingsProperty.h"
#import "SettingsCell.h"

@interface SettingsViewController ()
@property (strong, nonatomic) NSArray *openamSettingsKeys;
@property (strong, nonatomic) NSArray *oauthSettingsKeys;
@property (strong, nonatomic) NSDictionary *openamSettings;
@property (strong, nonatomic) NSDictionary *oauthSettings;
@property (weak, nonatomic) UITextField *fieldInEditMode;
@end

@implementation SettingsViewController

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

- (NSArray *)openamSettingsKeys{
    if (!_openamSettingsKeys) _openamSettingsKeys = self.serverSettings.openamSettingsKeys;
    return _openamSettingsKeys;
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
        
        NSArray *keys = self.serverSettings.openamSettingsKeys;//self.openamSettingsKeys;
        
        _openamSettings = [[NSDictionary alloc] initWithObjects:contents forKeys:keys];
    }
    return _openamSettings;
}

- (NSArray *)oauthSettingsKeys{
    if (!_oauthSettingsKeys) _oauthSettingsKeys = self.serverSettings.oauthSettingsKeys;
    return _oauthSettingsKeys;
}

- (NSDictionary *)oauthSettings {
    if (!_oauthSettings) {
        
        NSArray *contents = [NSArray arrayWithObjects:
                             [SettingsProperty initWithLabel:@"Client ID" //TODO localisation
                                                 placeHolder:@""
                                                       value:[self.serverSettings valueForKey:@"OAUTH2_CLIENT_ID_SETTING_KEY"]],
                             
                             [SettingsProperty initWithLabel:@"Client Password"
                                                 placeHolder:@"********"
                                                       value:[self.serverSettings valueForKey:@"OAUTH2_CLIENT_SECRET_SETTING_KEY"]
                                                keyboardType:UIKeyboardTypeDefault secure:YES],
                             
                             [SettingsProperty initWithLabel:@"Redirection URL"
                                                 placeHolder:@"http://example.com/oauth"
                                                       value:[self.serverSettings valueForKey:@"OAUTH2_REDIRECT_URI_SETTING_KEY"]
                                                keyboardType:UIKeyboardTypeURL],
                             
                             [SettingsProperty initWithLabel:@"Scope"
                                                 placeHolder:@"cn mail"
                                                       value:[self.serverSettings valueForKey:@"OAUTH2_SCOPE_SETTING_KEY"]],
                             
                             nil];
        
        NSArray *keys = self.serverSettings.oauthSettingsKeys;//self.oauthSettingsKeys;
        
        _oauthSettings = [[NSDictionary alloc] initWithObjects:contents forKeys:keys];
    }
    
    return _oauthSettings;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    switch (section) {
        case 0: return self.openamSettings.count;
        case 1: return self.oauthSettings.count;
    }
    return 0;
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
    
    NSDictionary *settings;
    NSArray *settingsKeys;
    switch (indexPath.section) {
        case 0: {
            settings = self.openamSettings;
            settingsKeys = self.openamSettingsKeys;
            break;
        }
        case 1: {
            settings = self.oauthSettings;
            settingsKeys = self.oauthSettingsKeys;
            break;
        }
    }
    
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
    [self updateServerSettings:self.oauthSettings];
    
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
    
    SettingsCell *cell = (SettingsCell*)textField.superview.superview.superview; //TODO revist this when have second section to see what indexPath.row is for second section..?
    
    NSIndexPath *indexPath = [self.tableView indexPathForCell:cell];
    
    NSDictionary *settings;
    NSArray *settingsKeys;
    switch (indexPath.section) {
        case 0: {
            settings = self.openamSettings;
            settingsKeys = self.openamSettingsKeys;
            break;
        }
        case 1: {
            settings = self.oauthSettings;
            settingsKeys = self.oauthSettingsKeys;
            break;
        }
    }
    
    SettingsProperty *property = [settings valueForKey:[settingsKeys objectAtIndex:indexPath.row]];
    property.textValue = textField.text;
}

@end