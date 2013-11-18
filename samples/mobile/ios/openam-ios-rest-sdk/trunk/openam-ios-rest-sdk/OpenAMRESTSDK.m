//
//  OpenAMRESTSDK.m
//  OpenAMRESTSDK
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import "OpenAMRESTSDK.h"

@implementation OpenAMRESTSDK

const NSString * const OPENAM_BASE_URL = @"OPENAM_BASE_URL";

- (AuthenticationProcess *)authenticate {
    return [[AuthenticationProcess alloc] init];
}

@end
