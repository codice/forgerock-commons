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
