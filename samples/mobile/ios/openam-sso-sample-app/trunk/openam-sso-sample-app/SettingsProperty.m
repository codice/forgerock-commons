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
