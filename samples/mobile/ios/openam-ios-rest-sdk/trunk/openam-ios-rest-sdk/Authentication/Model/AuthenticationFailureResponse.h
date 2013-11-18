//
//  AuthenticationFailureResponse.h
//  OpenAMRESTSDK
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface AuthenticationFailureResponse : NSObject

@property (strong, nonatomic, readonly) NSString *errorMessage;
@property (strong, nonatomic, readonly) NSString *failureUrl;

// designated initialiser
- (instancetype)initWithData:(NSDictionary *)data;

- (NSDictionary *)asData;

@end
