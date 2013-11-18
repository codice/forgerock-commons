//
//  AuthenticationCallback.m
//  OpenAMRESTSDK
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import "AuthenticationCallback.h"
#import "AuthenticationCallbackField.h"

@interface AuthenticationCallback()
@property (strong, nonatomic, readwrite) NSArray *inputs;
@property (strong, nonatomic, readwrite) NSArray *outputs;
@property (strong, nonatomic) NSDictionary *data;
@end

@implementation AuthenticationCallback

- (instancetype)init {
    return nil;
}

- (instancetype)initWithData:(NSDictionary *)data {
    
    self = [super init];
    
    if (self) {
        
        self.data = data;
        
        NSMutableArray *inputFields = [[NSMutableArray alloc] init];
        for (NSDictionary *field in [data valueForKey:@"input"]) {
            AuthenticationCallbackField *callbackField = [[AuthenticationCallbackField alloc] initWithData:field];
            [inputFields addObject:callbackField];
        }
        self.inputs = inputFields;
        
        
        NSMutableArray *outputFields = [[NSMutableArray alloc] init];
        for (NSDictionary *field in [data valueForKey:@"output"]) {
            AuthenticationCallbackField *callbackField = [[AuthenticationCallbackField alloc] initWithData:field];
            [outputFields addObject:callbackField];
        }
        self.outputs = outputFields;
    }
    
    return self;
}

- (NSArray *)type {
    return [self.data valueForKey:@"type"];
}

@end
