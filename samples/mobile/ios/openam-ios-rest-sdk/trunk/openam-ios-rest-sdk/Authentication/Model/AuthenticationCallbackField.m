//
//  AuthenticationCallbackField.m
//  OpenAMRESTSDK
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import "AuthenticationCallbackField.h"

@interface AuthenticationCallbackField()
@property (strong, nonatomic) NSDictionary *data;
@end

@implementation AuthenticationCallbackField

@synthesize value;

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

- (NSString *)name {
    return [self.data valueForKey:@"name"];
}

- (NSString *)value {
    return [self.data valueForKey:@"value"];
}

- (void)setValue:(NSString *)newValue {
    [self.data setValue:newValue forKey:@"value"];
}

@end
