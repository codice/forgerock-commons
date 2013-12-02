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
@end

@implementation ServerSettings

@synthesize baseUri;
@synthesize realm;
@synthesize authService;
@synthesize clientId;
@synthesize clientSecret;
@synthesize redirectionUrl;
@synthesize scope;

+ (id)allocWithZone:(NSZone *)zone {
    return [self instance];
}

+ (ServerSettings *)instance {
    static ServerSettings *singleton = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        singleton = [[super allocWithZone:nil] init];
    });
    return singleton;
}

- (NSDictionary *)settings {
    if (!_settings) _settings = [self loadSettings];
    return _settings;
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
    NSString *propsPath = [[paths objectAtIndex:0] stringByAppendingPathComponent:@"OAuth2Props.cache"];
    
    [NSKeyedArchiver archiveRootObject:self.settings toFile:propsPath];
}

- (NSMutableDictionary *)loadSettings {
    
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES);
    NSString *propsPath = [[paths objectAtIndex:0] stringByAppendingPathComponent:@"OAuth2Props.cache"];
    
    NSMutableDictionary *settings = [NSKeyedUnarchiver unarchiveObjectWithFile:propsPath];
    if (!settings) {
        settings = [[NSMutableDictionary alloc] init];
    }
    return settings;
}

- (BOOL)isConfigured {
    return ([self isBaseUriConfigured] && [self isClientIdConfigured] && [self isClientSecretConfigured] && [self isRedirectionUrlConfigured] && [self isScopeConfigured]);
}

- (BOOL)isBaseUriConfigured {
    return [self.baseUri length] != 0;
}

- (BOOL)isClientIdConfigured {
    return [self.clientId length] != 0;
}

- (BOOL)isClientSecretConfigured {
    return [self.clientSecret length] != 0;
}

- (BOOL)isRedirectionUrlConfigured {
    return [self.redirectionUrl length] != 0;
}

- (BOOL)isScopeConfigured {
    return [self.scope length] != 0;
}

- (NSString *)baseUri {
    return [[NSUserDefaults standardUserDefaults] stringForKey:@"server_uri_preference"];//TODO constant up
}

- (void)setBaseUri:(NSString *)serverBaseUri {
    [[NSUserDefaults standardUserDefaults] setValue:serverBaseUri forKey:@"server_uri_preference"];//TODO constant up
}

- (NSString *)realm {
    NSString *r = [[NSUserDefaults standardUserDefaults] stringForKey:@"auth_realm_preference"];//TODO constant up
    if (!r) r = @"/";
    return r;
}

- (void)setRealm:(NSString *)authRealm {
    [[NSUserDefaults standardUserDefaults] setValue:authRealm forKey:@"auth_realm_preference"];//TODO constant up
}

- (NSString *)authService {
    NSString *service = [[NSUserDefaults standardUserDefaults] stringForKey:@"auth_service_preference"];//TODO constant up
    if (!service) service = @"ldapService";
    return service;
}

- (void)setAuthService:(NSString *)service {
    [[NSUserDefaults standardUserDefaults] setValue:service forKey:@"auth_service_preference"];//TODO constant up
}

- (NSString *)clientId {
    return [[NSUserDefaults standardUserDefaults] stringForKey:@"client_id_preference"];//TODO constant up
}

- (void)setClientId:(NSString *)oauthClientId {
    [[NSUserDefaults standardUserDefaults] setValue:oauthClientId forKey:@"client_id_preference"];//TODO constant up
}

- (NSString *)clientSecret {
    return [[NSUserDefaults standardUserDefaults] stringForKey:@"client_secret_preference"];//TODO constant up
}

- (void)setClientSecret:(NSString *)oauthClientSecret {
    [[NSUserDefaults standardUserDefaults] setValue:oauthClientSecret forKey:@"client_secret_preference"];//TODO constant up
}

- (NSString *)redirectionUrl {
    return [[NSUserDefaults standardUserDefaults] stringForKey:@"redirection_url_preference"];//TODO constant up
}

- (void)setRedirectionUrl:(NSString *)oauthRedirectionUrl {
    [[NSUserDefaults standardUserDefaults] setValue:oauthRedirectionUrl forKey:@"redirection_url_preference"];//TODO constant up
}

- (NSString *)scope {
    return [[NSUserDefaults standardUserDefaults] stringForKey:@"scope_preference"];//TODO constant up
}

- (void)setScope:(NSString *)oauthScope {
    [[NSUserDefaults standardUserDefaults] setValue:oauthScope forKey:@"scope_preference"];//TODO constant up
}

@end
