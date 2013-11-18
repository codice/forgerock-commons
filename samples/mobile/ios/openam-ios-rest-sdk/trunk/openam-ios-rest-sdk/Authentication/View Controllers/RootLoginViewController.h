//
//  RootLoginViewController.h
//  OpenAMRESTSDK
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "AuthenticationCallbackResponse.h"

@protocol RootLoginViewController <NSObject>

- (void)callbacksCompleted:(AuthenticationCallbackResponse *)response;

@end
