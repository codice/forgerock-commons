//
//  OAuth2.m
//  openam-ios-rest-sdk
//
//  Created by Phill on 21/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import "OAuth2.h"
#import "HttpHelper.h"

@interface OAuth2()
@property (strong, nonatomic) id<OAuth2Delegate> delegate;

@property (strong, nonatomic) NSDictionary *accessToken;

@property (strong, nonatomic) NSData *receivedData;
@end

@implementation OAuth2

- (instancetype)init {
    return nil;
}

- (instancetype)initWithDelegate:(id<OAuth2Delegate>)delegate {
    
    self = [super init];
    
    if (self) {
        self.delegate = delegate;
    }
    
    return self;
}

- (void)getAccessTokenWithCode:(NSString *)code {
    
    NSDictionary *params = [NSMutableDictionary dictionary];
    [params setValue:@"authorization_code" forKey:@"grant_type"];
    [params setValue:code forKey:@"code"];
    [params setValue:[[self.delegate redirectionUrl] absoluteString] forKey:@"redirect_uri"];
    [params setValue:[self.delegate scope] forKey:@"scope"];
    [params setValue:[self.delegate realm] forKey:@"realm"];
    
    [self getAccessTokenWithUrlParams:params forClientId:[self.delegate clientId] usingClientSecret:[self.delegate clientSecret]];
}

- (void)refreshAccessToken:(NSString *)refreshToken {
    
    NSDictionary *params = [NSMutableDictionary dictionary];
    [params setValue:@"refresh_token" forKey:@"grant_type"];
    [params setValue:refreshToken forKey:@"refresh_token"];
    [params setValue:[[self.delegate redirectionUrl] absoluteString] forKey:@"redirect_uri"];
    [params setValue:[self.delegate scope] forKey:@"scope"];
    [params setValue:[self.delegate realm] forKey:@"realm"];
    
    [self getAccessTokenWithUrlParams:params forClientId:[self.delegate clientId] usingClientSecret:[self.delegate clientSecret]];
}

- (void)getAccessTokenWithUrlParams:(NSDictionary *)params forClientId:(NSString *)clientId usingClientSecret:(NSString *)clientSecret {
    
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"%@%@", [[self.delegate openAMBaseUrl] absoluteString], @"/oauth2/access_token"]];
    
    NSString *post = [HttpHelper urlEncodeDictionary:params];
    NSData *postData = [post dataUsingEncoding:NSUTF8StringEncoding allowLossyConversion:YES];
    NSString *postLength = [NSString stringWithFormat:@"%lu", (unsigned long)[postData length]];
    
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];
    [request setHTTPMethod:@"POST"];
    [request setValue:postLength forHTTPHeaderField:@"Content-Length"];
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-Type"];
    //add authorization header
    NSString *authzUserPassword = [NSString stringWithFormat:@"%@:%@", clientId, clientSecret];
    NSString *authorizationHeader = [NSString stringWithFormat:@"Basic %@", [HttpHelper base64Encode:authzUserPassword]];
    [request setValue:authorizationHeader forHTTPHeaderField:@"Authorization"];
    
    [request setHTTPBody:postData];
    
    /* when we use https, we need to allow any HTTPS cerificates, so add the one line code, use it only for test!
     */
    //    [NSURLRequest setAllowsAnyHTTPSCertificate:YES forHost:[url host]];
    
    NSURLConnection *connection = [[NSURLConnection alloc] initWithRequest:request delegate:self];
    [connection start];
}

- (NSDictionary *)getTokenInfo:(NSString *) accessToken {
    
    NSString *tokenURL = [NSString stringWithFormat:@"%@%@", [self.delegate openAMBaseUrl], @"/oauth2/tokeninfo"];
    
    NSDictionary *params = [NSMutableDictionary dictionary];
    [params setValue:accessToken forKey:@"access_token"];
    NSString *p = [HttpHelper urlEncodeDictionary:params];
    
    NSURL *fullURL = [NSURL URLWithString:[tokenURL stringByAppendingFormat:@"?%@", p]];
    NSMutableURLRequest *tokenRequest = [NSMutableURLRequest requestWithURL:fullURL];
    [tokenRequest setHTTPMethod:@"GET"];
    
    NSHTTPURLResponse  *response = [[NSHTTPURLResponse alloc] init];
    NSError *error = [[NSError alloc] init];
    
    //TODO this should be in another thread or asynchronous request
    NSData *data = [NSURLConnection sendSynchronousRequest:tokenRequest returningResponse:&response error:&error];
    
    if (data) {
        NSDictionary *tokenInfo = [NSJSONSerialization JSONObjectWithData:data options:kNilOptions error:&error];
        return tokenInfo;
    }
    
    //TODO error?? send to delegate??
    
    return nil;
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response {
   
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data {
    self.receivedData = [[NSMutableData alloc] initWithData:data];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {
    // do something with error here
}

//reads access token received from server (access and refresh token requests)
- (void)connectionDidFinishLoading:(NSURLConnection *)connection {
    
    NSError *error = [[NSError alloc] init];
    NSDictionary *token = [NSMutableDictionary dictionaryWithDictionary:[NSJSONSerialization JSONObjectWithData:self.receivedData options:kNilOptions error:&error]];
    if (token) {
        
        if ([token objectForKey:@"access_token"]) {
            
            //convert expires_in to axpires at
            NSTimeInterval expiresIn = (NSTimeInterval)[[token valueForKey:@"expires_in"] intValue];
            
            NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
            [dateFormatter setDateStyle:NSDateFormatterMediumStyle];
            [dateFormatter setTimeStyle:NSDateFormatterMediumStyle];
            NSString *dateStr = [dateFormatter stringFromDate:[[NSDate alloc] initWithTimeIntervalSinceNow:expiresIn]];
                        [token setValue:dateStr forKey:@"expires_in"];
            
            self.accessToken = token;
            
            if ([token objectForKey:@"refresh_token"]) {
                //access token request
                [self.delegate accessTokenCallback:token];
            } else {
                //refresh token request
                [self.delegate refreshTokenCallback:token];
            }
            
        } else {
            //TODO
        }
    } else {
        //TODO
    }
}

@end
