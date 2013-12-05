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

#import "LoginWebViewController.h"
#import "HttpHelper.h"
#import "KeyChainWrapper.h"

@interface LoginWebViewController ()

@property (strong, nonatomic, readonly) ServerSettings* serverSettings;
@end

@implementation LoginWebViewController

- (ServerSettings *)serverSettings {
    return [ServerSettings instance];
}

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
    
    if ([[request.URL absoluteString] rangeOfString:@"/XUI"].location == NSNotFound) {//TODO check for existance of cookie instead
        
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
