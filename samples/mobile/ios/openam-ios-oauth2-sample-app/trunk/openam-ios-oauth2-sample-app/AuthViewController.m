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

#import "AuthViewController.h"
#import "KeyChainWrapper.h"

@interface AuthViewController ()
@property (strong, nonatomic) NSURLRequest *lastRequest;
@end

@implementation AuthViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)loadView {
    [super loadView];
}

- (void)viewDidLoad {
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    
    //prepare and send request to autorization endpoint
    NSDictionary *params = [NSMutableDictionary dictionary];
    [params setValue:@"code" forKey:@"response_type"];
//    [params setValue:@"TOUCH" forKey:@"display"];
    [params setValue:[[ServerSettings instance] scope] forKey:@"scope"];
    [params setValue:[[ServerSettings instance] realm] forKey:@"realm"];
    [params setValue:[[ServerSettings instance] authService] forKey:@"service"];
    [params setValue:[[ServerSettings instance] clientId] forKey:@"client_id"];
    [params setValue:[[ServerSettings instance] redirectionUrl] forKey:@"redirect_uri"];
    
    NSLog(@"params=%@", params);
    
    NSString *authzURL = [NSString stringWithFormat:@"%@%@", [[ServerSettings instance] baseUri], @"/oauth2/authorize"];
    
    NSString *p = [HttpHelper urlEncodeDictionary:params];
    NSLog(@"p=%@", p);
    
    NSURL *url = [NSURL URLWithString:[authzURL stringByAppendingFormat:@"?%@", p]];
    NSMutableURLRequest *authRequest = [NSMutableURLRequest requestWithURL:url cachePolicy:NSURLRequestReloadIgnoringCacheData timeoutInterval:30];
    
    
    NSString *ssoTokenId = [KeyChainWrapper searchKeychainCopyMatching:@"SSOTokenId"];
    [self setCookieForSSOTokenId:ssoTokenId];
    
    [self.webView setDelegate:self];
    
    [UIApplication sharedApplication].networkActivityIndicatorVisible = YES;
    [self.webView loadRequest:authRequest];
}

- (void)setCookieForSSOTokenId:(NSString *)ssoTokenId {
    NSMutableDictionary *cookieProperties = [NSMutableDictionary dictionary];
    [cookieProperties setObject:@"iPlanetDirectoryPro" forKey:NSHTTPCookieName];//TODO make call to get cookie name
    [cookieProperties setObject:ssoTokenId forKey:NSHTTPCookieValue];
    [cookieProperties setObject:@".forgerock.com" forKey:NSHTTPCookieDomain];//TODO get from properties
    [cookieProperties setObject:[[ServerSettings instance] baseUri] forKey:NSHTTPCookieOriginURL];
    [cookieProperties setObject:@"/" forKey:NSHTTPCookiePath];
    
    NSHTTPCookie *cookie = [NSHTTPCookie cookieWithProperties:cookieProperties];
    [[NSHTTPCookieStorage sharedHTTPCookieStorage] setCookie:cookie];
}

- (void)deleteSSOTokenCookie {
    NSHTTPCookieStorage *storage = [NSHTTPCookieStorage sharedHTTPCookieStorage];
    for (NSHTTPCookie *cookie in [storage cookies]) {
        NSRange range = [cookie.name rangeOfString:@"iPlanetDirectoryPro"];//TODO make call to get cookie name
        if (range.location != NSNotFound) {
            //sso token cookie found
            [storage deleteCookie:cookie];
        }
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

// only used for getting authorization grant code (parameter in the URL)
- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
    
    if ([[request.URL absoluteString] hasPrefix:[[ServerSettings instance] redirectionUrl]]) {
        if (DEBUG)
            NSLog(@"URL: %@\n", [request.URL absoluteString]);

        [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;

        [self deleteSSOTokenCookie];

        //request access token
        NSRange range = [[request.URL absoluteString] rangeOfString:@"error="];
        if (range.location == NSNotFound) {

            NSString *code = [[HttpHelper decodeUrlParameters:request.URL.query] valueForKey:@"code"];
            [self.delegate processGrantToken:code];

        } else {
            UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Failed to Authorize." delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [alertView show];
        }

        [self dismissViewControllerAnimated:YES completion:nil];

        return NO;
    }
    return YES;
}

- (IBAction)cancelAction:(UIBarButtonItem *)sender {
    [self dismissViewControllerAnimated:YES completion:nil];
}

@end
