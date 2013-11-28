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
//    self.authenticated = false;
    //    authenticated = [[appDelegate.serverProperties valueForKey:SETTINGS_OPENAM_URL] hasPrefix:@"https"];
//    [self.webView loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:@"about:blank"]]];
}

- (void)viewDidLoad {
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    
    //prepare and send request to autorization endpoint
    NSDictionary *params = [NSMutableDictionary dictionary];
    [params setValue:@"code" forKey:@"response_type"];
//    [params setValue:@"TOUCH" forKey:@"display"];
    [params setValue:[self.serverSettings valueForKey:@"OAUTH2_SCOPE_SETTING_KEY"] forKey:@"scope"];
    [params setValue:[self.serverSettings valueForKey:@"OPENAM_REALM_SETTING_KEY"] forKey:@"realm"];
    [params setValue:[self.serverSettings valueForKey:@"OPENAM_AUTH_SERVICE_SETTING_KEY"] forKey:@"service"];
    [params setValue:[self.serverSettings valueForKey:@"OAUTH2_CLIENT_ID_SETTING_KEY"] forKey:@"client_id"];
    [params setValue:[self.serverSettings valueForKey:@"OAUTH2_REDIRECT_URI_SETTING_KEY"] forKey:@"redirect_uri"];
    
    NSLog(@"params=%@", params);
    
    NSString *authzURL = [NSString stringWithFormat:@"%@%@", [self.serverSettings valueForKey:@"OPENAM_URL_SETTING_KEY"], @"/oauth2/authorize"];
    
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
    [cookieProperties setObject:[self.serverSettings valueForKey:@"OPENAM_COOKIE_NAME_SETTING_KEY"] forKey:NSHTTPCookieName];
    [cookieProperties setObject:ssoTokenId forKey:NSHTTPCookieValue];
    [cookieProperties setObject:@".forgerock.com" forKey:NSHTTPCookieDomain];//TODO get from properties
    [cookieProperties setObject:[self.serverSettings valueForKey:@"OPENAM_URL_SETTING_KEY"] forKey:NSHTTPCookieOriginURL];
    [cookieProperties setObject:@"/" forKey:NSHTTPCookiePath];
    
    NSHTTPCookie *cookie = [NSHTTPCookie cookieWithProperties:cookieProperties];
    [[NSHTTPCookieStorage sharedHTTPCookieStorage] setCookie:cookie];
}

- (void)deleteSSOTokenCookie {
    NSHTTPCookieStorage *storage = [NSHTTPCookieStorage sharedHTTPCookieStorage];
    for (NSHTTPCookie *cookie in [storage cookies]) {
        NSRange range = [cookie.name rangeOfString:[self.serverSettings valueForKey:@"OPENAM_COOKIE_NAME_SETTING_KEY"]];
        if (range.location != NSNotFound) {
            //sso token cookie found
            [storage deleteCookie:cookie];
        }
    }
    [[NSUserDefaults standardUserDefaults] synchronize];  //????
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

// only used for getting authorization grant code (parameter in the URL)
- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
    
    if ([[request.URL absoluteString] hasPrefix:[self.serverSettings valueForKey:@"OAUTH2_REDIRECT_URI_SETTING_KEY"]]) {
        if (DEBUG)
            NSLog(@"URL: %@\n", [request.URL absoluteString]);

        [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;

        [self deleteSSOTokenCookie];

        //request access token
        NSRange range = [[request.URL absoluteString] rangeOfString:@"error="];
        if (range.location == NSNotFound) {

            NSString *code = [[HttpHelper decodeUrlParameters:request.URL.query] valueForKey:@"code"];
            [self.delegate processGrantToken:code];
            //            [self.delegate processGrantToken:request.URL];


        } else {
            //handle error!  //TODO guess an error has occured and should be handled?...
        }

        //        UIViewController *b = [self.navigationController popViewControllerAnimated:NO];
        [self dismissViewControllerAnimated:YES completion:nil];

        return NO;
    }
    return YES;
}

- (IBAction)cancelAction:(UIBarButtonItem *)sender {
    [self dismissViewControllerAnimated:YES completion:nil];
}

@end
