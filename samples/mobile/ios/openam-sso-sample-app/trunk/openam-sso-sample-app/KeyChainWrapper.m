//
//  KeyChainWrapper.m
//  OpenAMSSO
//
//  Created by Phill on 18/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

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
