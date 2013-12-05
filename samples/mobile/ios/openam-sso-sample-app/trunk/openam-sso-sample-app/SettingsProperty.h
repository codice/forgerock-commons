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
