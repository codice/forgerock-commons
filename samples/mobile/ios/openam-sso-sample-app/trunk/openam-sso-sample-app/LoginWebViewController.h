//
//  LoginWebViewController.h
//  OpenAMSSO
//
//  Created by Phill on 18/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ServerSettings.h"
#import "LoginViewController.h"

@interface LoginWebViewController : LoginViewController <UIWebViewDelegate, NSURLConnectionDataDelegate>

@property (weak, nonatomic) IBOutlet UIWebView *webView;

@end
