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

@interface ServerSettings : NSObject

@property (nonatomic, readonly, getter = isConfigured) BOOL configured;
@property (strong, nonatomic) NSString *baseUri;
@property (strong, nonatomic) NSString *realm;
@property (strong, nonatomic) NSString *authService;
@property (strong, nonatomic) NSString *clientId;
@property (strong, nonatomic) NSString *clientSecret;
@property (strong, nonatomic) NSString *redirectionUrl;
@property (strong, nonatomic) NSString *scope;

+ (ServerSettings *)instance;

- (void)setValue:(NSString *)value forKey:(NSString *)key;

- (NSString *)valueForKey:(NSString *)key;

@end
