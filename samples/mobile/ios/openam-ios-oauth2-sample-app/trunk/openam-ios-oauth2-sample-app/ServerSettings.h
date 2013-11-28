//
//  ServerSettings.h
//  OpenAMOAuth2SampleApp
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ServerSettings : NSObject

@property (nonatomic, readonly, getter = isConfigured) BOOL configured;
//TODO move else where
@property (strong, nonatomic) NSString *ssoTokenId;

+ (ServerSettings *)getInstance;
    
- (NSArray *)openamSettingsKeys;

- (NSArray *)oauthSettingsKeys;

- (void)setValue:(NSString *)value forKey:(NSString *)key;

- (void)setValuesForKeysWithDictionary:(NSDictionary *)keyedValues;

- (NSString *)valueForKey:(NSString *)key;

- (void)saveSettings;

@end
