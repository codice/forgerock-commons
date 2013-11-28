//
//  ServerSettings.m
//  OpenAMOAuth2SampleApp
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import "ServerSettings.h"

@interface ServerSettings()
@property (strong, nonatomic, readwrite) NSMutableDictionary *settings;
@property (nonatomic, readwrite, getter = isConfigured) BOOL configured;
@end

//TODO Document NOT THREAD SAFE!!

@implementation ServerSettings

- (instancetype)init {
    return nil;
}

// Designated Initialiser
- (instancetype)initAsSingleton {
    self = [super init];
    return self;
}

+ (ServerSettings *)getInstance {
    return [[ServerSettings alloc] initAsSingleton];
}

- (NSDictionary *)settings {
    if (!_settings) _settings = [self loadSettings];
    return _settings;
}

- (BOOL)isConfigured {
    if (![self.settings valueForKey:@"OPENAM_URL_SETTING_KEY"]) {
        return false;
    }
    if (![self.settings valueForKey:@"OPENAM_REALM_SETTING_KEY"]) {
        return false;
    }
    if (![self.settings valueForKey:@"OPENAM_AUTH_SERVICE_SETTING_KEY"]) {
        return false;
    }
    if (![self.settings valueForKey:@"OPENAM_COOKIE_NAME_SETTING_KEY"]) {
        return false;
    }
    if (![self.settings valueForKey:@"OAUTH2_CLIENT_ID_SETTING_KEY"]) {
        return false;
    }
    if (![self.settings valueForKey:@"OAUTH2_CLIENT_SECRET_SETTING_KEY"]) {
        return false;
    }
    if (![self.settings valueForKey:@"OAUTH2_REDIRECT_URI_SETTING_KEY"]) {
        return false;
    }
    if (![self.settings valueForKey:@"OAUTH2_SCOPE_SETTING_KEY"]) {
        return false;
    }
    
    return TRUE;
}

- (NSArray *)openamSettingsKeys {
    return [[NSArray alloc] initWithObjects:@"OPENAM_URL_SETTING_KEY",@"OPENAM_REALM_SETTING_KEY",@"OPENAM_AUTH_SERVICE_SETTING_KEY",@"OPENAM_COOKIE_NAME_SETTING_KEY",nil];
}

- (NSArray *)oauthSettingsKeys {
    return [[NSArray alloc] initWithObjects:@"OAUTH2_CLIENT_ID_SETTING_KEY",@"OAUTH2_CLIENT_SECRET_SETTING_KEY",@"OAUTH2_REDIRECT_URI_SETTING_KEY",@"OAUTH2_SCOPE_SETTING_KEY",nil];
}

- (void)setValue:(NSString *)value forKey:(NSString *)key {
    [self.settings setValue:value forKey:key];
}

- (void)setValuesForKeysWithDictionary:(NSDictionary *)keyedValues {
    [self.settings setValuesForKeysWithDictionary:keyedValues];
}

- (NSString *)valueForKey:(NSString *)key {
    return [self.settings valueForKey:key];
}

- (void)saveSettings {
    
    [self.settings setDictionary:self.settings];
    
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES);
    NSString *propsPath = [[paths objectAtIndex:0] stringByAppendingPathComponent:@"OAuth2Props.cache"];
    
    //    NSFileManager *fileManager = [NSFileManager defaultManager];
    //    NSArray *paths = [fileManager URLsForDirectory:NSCachesDirectory inDomains:NSUserDomainMask];
    //    NSURL *url = [paths firstObject];
    //    url = [url URLByAppendingPathComponent:@"OAuth2Props.cache"];
    //    NSString *s = [url absoluteString];
    
    [NSKeyedArchiver archiveRootObject:self.settings toFile:propsPath];
}

- (NSMutableDictionary *)loadSettings {
    
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES);
    NSString *propsPath = [[paths objectAtIndex:0] stringByAppendingPathComponent:@"OAuth2Props.cache"];
    
    //    NSFileManager *fileManager = [NSFileManager defaultManager];
    //    NSArray *paths = [fileManager URLsForDirectory:NSCachesDirectory inDomains:NSUserDomainMask];
    //    NSURL *url = [paths firstObject];
    //    url = [url URLByAppendingPathComponent:@"OAuth2Props.cache"];
    //    NSString *s = [url absoluteString];
    
    NSMutableDictionary *settings = [NSKeyedUnarchiver unarchiveObjectWithFile:propsPath];
    if (!settings) {
        settings = [[NSMutableDictionary alloc] init];
    }
    return settings;
}

@end
