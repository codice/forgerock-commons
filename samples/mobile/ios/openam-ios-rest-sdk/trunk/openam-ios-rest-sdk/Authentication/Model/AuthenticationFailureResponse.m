//
//  AuthenticationFailureResponse.m
//  OpenAMRESTSDK
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import "AuthenticationFailureResponse.h"

@interface AuthenticationFailureResponse()
@property (strong, nonatomic) NSDictionary *data;
@end

@implementation AuthenticationFailureResponse

- (instancetype)init {
    return nil;
}

- (instancetype)initWithData:(NSDictionary *)data {
    
    self = [super init];
    
    if (self) {
        self.data = data;
    }
    
    return self;
}

- (NSString *)errorMessage {
    return [self.data valueForKey:@"errorMessage"];
}

- (NSString *)failureUrl {
    return [self.data valueForKey:@"failureUrl"];
}

- (NSDictionary *)asData {
    return self.data;
}

@end
