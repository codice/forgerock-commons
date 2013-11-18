//
//  AuthenticationCallbackField.h
//  OpenAMRESTSDK
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface AuthenticationCallbackField : NSObject

@property (strong, nonatomic, readonly) NSString *name;
@property (strong, nonatomic) NSString *value;

// designated initialiser
- (instancetype)initWithData:(NSDictionary *)data;

@end
