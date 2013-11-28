//
//  SettingsProperty.h
//  OpenAMOAuth2SampleApp
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SettingsProperty : NSObject

@property (strong, nonatomic, readonly) NSString *textLabel;
@property (strong, nonatomic, readonly) NSString *textPlaceholder;
@property (strong, nonatomic) NSString *textValue;
@property (nonatomic, readonly, getter = isSecure) BOOL secure;
@property (nonatomic, readonly) UIKeyboardType keyboardType;

+ (SettingsProperty *)initWithLabel:(NSString *)label placeHolder:(NSString *)placeholder value:(NSString *)value;

+ (SettingsProperty *)initWithLabel:(NSString *)label placeHolder:(NSString *)placeholder value:(NSString *)value keyboardType:(UIKeyboardType)keyboardType;

// designated initializer
+ (SettingsProperty *)initWithLabel:(NSString *)label placeHolder:(NSString *)placeholder value:(NSString *)value keyboardType:(UIKeyboardType)keyboardType secure:(BOOL)secure;

@end
