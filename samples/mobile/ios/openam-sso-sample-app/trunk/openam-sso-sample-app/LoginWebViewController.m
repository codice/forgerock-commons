//
//  LoginWebViewController.m
//  OpenAMSSO
//
//  Created by Phill on 18/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import "LoginWebViewController.h"
#import "HttpHelper.h"
#import "KeyChainWrapper.h"

@interface LoginWebViewController ()

@end

@implementation LoginWebViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    
    NSDictionary *paramMap = [NSMutableDictionary dictionary];
    //TODO add in parameters from settings
    
    NSString *authnURL = [NSString stringWithFormat:@"%@%@", [self.serverSettings valueForKey:@"OPENAM_URL_SETTING_KEY"], @"/XUI"];
    
    NSString *params = [HttpHelper urlEncodeDictionary:paramMap];
    
    NSURL *url = [NSURL URLWithString:[authnURL stringByAppendingFormat:@"?%@", params]];
    NSMutableURLRequest *authRequest = [NSMutableURLRequest requestWithURL:url cachePolicy:NSURLRequestReloadIgnoringCacheData timeoutInterval:30];;
    
    [self.webView setDelegate:self];
    [self.webView loadRequest:authRequest];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
    
    if ([[request.URL absoluteString] rangeOfString:@"/XUI"].location == NSNotFound) {
        
        NSHTTPCookieStorage *storage = [NSHTTPCookieStorage sharedHTTPCookieStorage];
        for (NSHTTPCookie *cookie in [storage cookies]) {
            NSRange range = [cookie.name rangeOfString:[self.serverSettings valueForKey:@"OPENAM_COOKIE_NAME_SETTING_KEY"]];
            if (range.location != NSNotFound) {
                if (![KeyChainWrapper createKeychainValue:cookie.value forIdentifier:@"SSOTokenId"]) {
                    [KeyChainWrapper updateKeychainValue:cookie.value forIdentifier:@"SSOTokenId"];
                }
            }
        }
        
        [self dismissViewControllerAnimated:YES completion:nil];
        return NO;
    }
    
    return YES;
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response {
    
}

- (IBAction)cancelButtonAction:(UIBarButtonItem *)sender {
    [self dismissViewControllerAnimated:YES completion:nil];
}


@end
