//
//  KeyChainWrapper.h
//  OpenAMSSO
//
//  Created by Phill on 18/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface KeyChainWrapper : NSObject

+ (NSString *)searchKeychainCopyMatching:(NSString *)identifier;

+ (BOOL)createKeychainValue:(NSString *)password forIdentifier:(NSString *)identifier;

+ (BOOL)updateKeychainValue:(NSString *)password forIdentifier:(NSString *)identifier;

+ (void)deleteKeychainValue:(NSString *)identifier;

@end
