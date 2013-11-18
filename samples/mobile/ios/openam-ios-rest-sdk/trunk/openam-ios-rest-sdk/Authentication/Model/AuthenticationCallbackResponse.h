//
//  AuthenticationCallbackResponse.h
//  OpenAMRESTSDK
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface AuthenticationCallbackResponse : NSObject

@property (strong, nonatomic, readonly) NSString *authId;
@property (strong, nonatomic, readonly) NSString *stage;
@property (strong, nonatomic, readonly) NSString *templateUrl;
@property (strong, nonatomic, readonly) NSArray *callbacks;

// designated initialiser
- (instancetype)initWithData:(NSDictionary *)data;

- (NSDictionary *)asData;

@end
