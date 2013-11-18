//
//  SettingsProperty.m
//  OpenAMOAuth2SampleApp
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import "SettingsProperty.h"

@interface SettingsProperty()
@property (strong, nonatomic, readwrite) NSString *textLabel;
@property (strong, nonatomic, readwrite) NSString *textPlaceholder;
@property (nonatomic, readwrite, getter = isSecure) BOOL secure;
@property (nonatomic, readwrite) UIKeyboardType keyboardType;
@end

@implementation SettingsProperty

+ (SettingsProperty *)initWithLabel:(NSString *)label placeHolder:(NSString *)placeholder value:(NSString *)value {
    return [self initWithLabel:label placeHolder:placeholder value:value keyboardType:UIKeyboardTypeDefault secure:NO];
}

+ (SettingsProperty *)initWithLabel:(NSString *)label placeHolder:(NSString *)placeholder value:(NSString *)value keyboardType:(UIKeyboardType)keyboardType {
    return [self initWithLabel:label placeHolder:placeholder value:value keyboardType:keyboardType secure:NO];
}

+ (SettingsProperty *)initWithLabel:(NSString *)label placeHolder:(NSString *)placeholder value:(NSString *)value  keyboardType:(UIKeyboardType)keyboardType secure:(BOOL)secure {
    SettingsProperty *settingsProperty = [[SettingsProperty alloc] init];
    if (settingsProperty) {
        settingsProperty.textLabel = label;
        settingsProperty.textPlaceholder = placeholder;
        settingsProperty.textValue = value;
        settingsProperty.keyboardType = keyboardType;
        settingsProperty.secure = secure;
    }
    return settingsProperty;
}

@end
