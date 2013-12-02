//
//  LegacyRestConnection.m
//  openam-ios-rest-sdk
//
//  Created by Phill on 30/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import "LegacyRestConnection.h"

static NSMutableArray *sharedConnectionList = nil;

@interface LegacyRestConnection()
@property (nonatomic, copy) NSURLRequest *request;
@property (nonatomic, copy) void (^completionBlock) (id obj, NSError *err);
@end

@implementation LegacyRestConnection

- (instancetype)initWithRequest:(NSURLRequest *)request onCompletion:(void (^)(NSData *response, NSError *err))block {
    self = [super init];
    if (self) {
        self.request = request;
        self.completionBlock = block;
    }
    return self;
}

- (void)start {
    // Initialize container for data collected from NSURLConnection
    container = [[NSMutableData alloc] init];
    
    // Spawn connection
    internalConnection = [[NSURLConnection alloc] initWithRequest:self.request delegate:self startImmediately:YES];
    
    // If this is the first connection started, create the array
    if (!sharedConnectionList)
        sharedConnectionList = [[NSMutableArray alloc] init];
    
    // Add the connection to the array so it doesn't get destroyed
    [sharedConnectionList addObject:self];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data {
    [container appendData:data];
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection {
    
    // Execute completionBlock
    if (self.completionBlock)
        self.completionBlock(container, nil);
    
    // Now, destroy this connection
    [sharedConnectionList removeObject:self];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {
    
    // Pass the error from the connection to the completionBlock
    if (self.completionBlock)
        self.completionBlock(nil, error);
    
    // Destroy this connection
    [sharedConnectionList removeObject:self];
}

@end
