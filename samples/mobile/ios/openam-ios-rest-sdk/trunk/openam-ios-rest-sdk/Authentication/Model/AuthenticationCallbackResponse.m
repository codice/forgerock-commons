//
//  AuthenticationCallbackResponse.m
//  OpenAMRESTSDK
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import "AuthenticationCallbackResponse.h"
#import "AuthenticationCallback.h"

@interface AuthenticationCallbackResponse()
@property (strong, nonatomic, readwrite) NSArray *callbacks;
@property (strong, nonatomic) NSDictionary *data;
@end

@implementation AuthenticationCallbackResponse

- (instancetype)init {
    return nil;
}

- (instancetype)initWithData:(NSDictionary *)data {
    
    self = [super init];
    
    if (self) {
        
        self.data = data;
        
        NSMutableArray *callbacks = [[NSMutableArray alloc] init];
        for (NSDictionary *callback in [data valueForKey:@"callbacks"]) {
            
            AuthenticationCallback *authenticationCallback = [[AuthenticationCallback alloc] initWithData:callback];
            [callbacks addObject:authenticationCallback];
        }
        
        self.callbacks = callbacks;
    }
    
    return self;
}

- (NSString *)authId {
    return [self.data valueForKey:@"authId"];
}

- (NSString *)stage {
    return [self.data valueForKey:@"stage"];
}

- (NSString *)templateUrl {
    return [self.data valueForKey:@"template"];
}

- (NSArray *)callbacks {
    return _callbacks;
}

- (NSDictionary *)asData {
    return self.data;
}

@end
