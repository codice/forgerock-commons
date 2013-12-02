//
//  RestSyncConnection.m
//  openam-ios-rest-sdk
//
//  Created by Phill on 30/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import "RestSyncConnection.h"

@interface RestSyncConnection()
@property (nonatomic, copy) NSURLRequest *request;
@end

@implementation RestSyncConnection

- (instancetype)initWithRequest:(NSURLRequest *)request {
    self = [super init];
    if (self) {
        self.request = request;
    }
    return self;
}

- (NSDictionary *)startWithError:(NSError *)error {
    
    NSURLResponse *response = nil;
    
    NSData *receivedData = [NSURLConnection sendSynchronousRequest:self.request returningResponse:&response error:&error];
    
    if (error) {
        return nil;
    }
    
    return [NSJSONSerialization JSONObjectWithData:receivedData options:NSJSONReadingMutableContainers error:&error];
}

@end
