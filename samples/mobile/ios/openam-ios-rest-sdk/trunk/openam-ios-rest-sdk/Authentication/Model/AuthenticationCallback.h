//
//  AuthenticationCallback.h
//  OpenAMRESTSDK
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface AuthenticationCallback : NSObject

@property (strong, nonatomic, readonly) NSString *type;
@property (strong, nonatomic, readonly) NSArray *inputs;
@property (strong, nonatomic, readonly) NSArray *outputs;

// designated initialiser
- (instancetype)initWithData:(NSDictionary *)data;

@end
