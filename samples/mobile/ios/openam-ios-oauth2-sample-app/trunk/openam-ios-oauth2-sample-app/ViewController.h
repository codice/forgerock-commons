//
//  ViewController.h
//  OpenAMOAuth2SampleApp
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ServerSettings.h"
#import <openam-ios-rest-sdk/OpenAMRESTSDK.h>

@interface ViewController : UIViewController <OAuth2Delegate>

@property (strong, nonatomic) ServerSettings* serverSettings;

@property (strong, nonatomic) NSTimer *refreshTimer;

- (void)processGrantToken:(NSString *)grantToken;

@end

