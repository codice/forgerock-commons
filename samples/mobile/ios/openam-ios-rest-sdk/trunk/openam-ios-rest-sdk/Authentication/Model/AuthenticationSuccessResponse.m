//
//  AuthenticationSuccessResponse.m
//  OpenAMRESTSDK
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import "AuthenticationSuccessResponse.h"

@interface AuthenticationSuccessResponse()
@property (strong, nonatomic) NSDictionary *data;
@end

@implementation AuthenticationSuccessResponse

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

- (NSString *)tokenId {
    return [self.data valueForKey:@"tokenId"];
}

- (NSString *)successUrl {
    return [self.data valueForKey:@"successUrl"];
}

- (NSDictionary *)asData {
    return self.data;
}

@end
