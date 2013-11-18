//
//  OpenAMRESTSDK.h
//  OpenAMRESTSDK
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "AuthenticationProcess.h"

@interface OpenAMRESTSDK : NSObject

extern NSString * const OPENAM_BASE_URL;

- (AuthenticationProcess *)authenticate;

@end
