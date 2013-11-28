//
//  AuthViewController.h
//  OpenAMOAuth2SampleApp
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ServerSettings.h"
#import <openam-ios-rest-sdk/OpenAMRESTSDK.h>
#import "ViewController.h"

@interface AuthViewController : UIViewController <UIWebViewDelegate, NSURLConnectionDataDelegate>

@property (weak, nonatomic) ServerSettings *serverSettings;
@property (weak, nonatomic) IBOutlet UIWebView *webView;

@property (strong, nonatomic) ViewController *delegate;

@end
