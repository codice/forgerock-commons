/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2013 ForgeRock, AS.
 */

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
