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
#import "KeyChainWrapper.h"
#import <Security/Security.h>

@implementation KeyChainWrapper

static NSString *serviceName = @"org.forgerock.OpenAMSSO";

+ (NSMutableDictionary *)newSearchDictionary:(NSString *)identifier {
    NSMutableDictionary *searchDictionary = [[NSMutableDictionary alloc] init];
    
    [searchDictionary setObject:CFBridgingRelease(kSecClassGenericPassword) forKey:CFBridgingRelease(kSecClass)];
    
    NSData *encodedIdentifier = [identifier dataUsingEncoding:NSUTF8StringEncoding];
    [searchDictionary setObject:encodedIdentifier forKey:CFBridgingRelease(kSecAttrGeneric)];
    [searchDictionary setObject:encodedIdentifier forKey:CFBridgingRelease(kSecAttrAccount)];
    [searchDictionary setObject:serviceName forKey:CFBridgingRelease(kSecAttrService)];
    
    return searchDictionary;
}

+ (NSString *)searchKeychainCopyMatching:(NSString *)identifier {
    NSMutableDictionary *searchDictionary = [self newSearchDictionary:identifier];
    
    // Add search attributes
    [searchDictionary setObject:CFBridgingRelease(kSecMatchLimitOne) forKey:CFBridgingRelease(kSecMatchLimit)];
    
    // Add search return types
    [searchDictionary setObject:(id)kCFBooleanTrue forKey:CFBridgingRelease(kSecReturnData)];
    
    NSMutableDictionary *attributes = NULL;
    CFTypeRef typeRef = (__bridge CFTypeRef) attributes;
    SecItemCopyMatching((CFDictionaryRef)CFBridgingRetain(searchDictionary), &typeRef);
    
    return [[NSString alloc] initWithData:CFBridgingRelease(typeRef) encoding:NSUTF8StringEncoding];
}

+ (BOOL)createKeychainValue:(NSString *)password forIdentifier:(NSString *)identifier {
    NSMutableDictionary *dictionary = [self newSearchDictionary:identifier];
    
    NSData *passwordData = [password dataUsingEncoding:NSUTF8StringEncoding];
    [dictionary setObject:passwordData forKey:CFBridgingRelease(kSecValueData)];
    
    OSStatus status = SecItemAdd(CFBridgingRetain(dictionary), NULL);
    
    if (status == errSecSuccess) {
        return YES;
    }
    return NO;
}

+ (BOOL)updateKeychainValue:(NSString *)password forIdentifier:(NSString *)identifier {
    
    NSMutableDictionary *searchDictionary = [self newSearchDictionary:identifier];
    NSMutableDictionary *updateDictionary = [[NSMutableDictionary alloc] init];
    NSData *passwordData = [password dataUsingEncoding:NSUTF8StringEncoding];
    [updateDictionary setObject:passwordData forKey:CFBridgingRelease(kSecValueData)];
    
    OSStatus status = SecItemUpdate(CFBridgingRetain(searchDictionary), CFBridgingRetain(updateDictionary));
    
    if (status == errSecSuccess) {
        return YES;
    }
    return NO;
}

+ (void)deleteKeychainValue:(NSString *)identifier {
    NSMutableDictionary *searchDictionary = [self newSearchDictionary:identifier];
    SecItemDelete(CFBridgingRetain(searchDictionary));
}

@end
