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

#import "ServerSettings.h"

@interface ServerSettings()
@property (strong, nonatomic, readwrite) NSMutableDictionary *settings;
@property (nonatomic, readwrite, getter = isConfigured) BOOL configured;
@end

@implementation ServerSettings

//- (instancetype)init {
//    return nil;
//}

+ (id)allocWithZone:(NSZone *)zone {
    return [self sharedStore];
}

+ (ServerSettings *)sharedStore {
    static ServerSettings *sharedStore = nil;
    if (!sharedStore)
        sharedStore = [[super allocWithZone:nil] init];
    
    return sharedStore;
}

- (instancetype)initAsSingleton {
    return [super init];
}

+ (ServerSettings *)instance {
    static ServerSettings *singleton = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        singleton = [[self alloc] initAsSingleton];
    });
    return singleton;
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

- (NSString *)valueForKey:(NSString *)key {
    return [self.settings valueForKey:key];
}

- (void)saveSettings {
    
    [self.settings setDictionary:self.settings];
    
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES);
    NSString *propsPath = [[paths objectAtIndex:0] stringByAppendingPathComponent:@"SSOProps.cache"];
    
    //    NSFileManager *fileManager = [NSFileManager defaultManager];
    //    NSArray *paths = [fileManager URLsForDirectory:NSCachesDirectory inDomains:NSUserDomainMask];
    //    NSURL *url = [paths firstObject];
    //    url = [url URLByAppendingPathComponent:@"OAuth2Props.cache"];
    //    NSString *s = [url absoluteString];
    
    [NSKeyedArchiver archiveRootObject:self.settings toFile:propsPath];
}

- (NSMutableDictionary *)loadSettings {
    
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES);
    NSString *propsPath = [[paths objectAtIndex:0] stringByAppendingPathComponent:@"SSOProps.cache"];
    
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
