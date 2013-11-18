//
//  AuthenticationSuccessResponse.h
//  OpenAMRESTSDK
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface AuthenticationSuccessResponse : NSObject

@property (strong, nonatomic, readonly) NSString *tokenId;
@property (strong, nonatomic, readonly) NSString *successUrl;

// designated initialiser
- (instancetype)initWithData:(NSDictionary *)data;

- (NSDictionary *)asData;

@end