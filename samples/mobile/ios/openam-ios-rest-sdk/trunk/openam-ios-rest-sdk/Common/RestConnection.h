//
//  RestConnection.h
//  openam-ios-rest-sdk
//
//  Created by Phill on 30/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface RestConnection : NSObject <NSURLConnectionDataDelegate, NSURLConnectionDataDelegate> {
    
    NSURLConnection *internalConnection;
    NSMutableData *container;
}

- (instancetype)initWithRequest:(NSURLRequest *)request onCompletion:(void (^)(NSDictionary *response, NSError *err))block;

- (void)start;

@end
