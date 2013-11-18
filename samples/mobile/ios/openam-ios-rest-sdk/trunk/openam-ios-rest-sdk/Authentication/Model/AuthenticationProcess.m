//
//  AuthenticationProcess.m
//  OpenAMRESTSDK
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import "AuthenticationProcess.h"
#import "AuthenticationCallbackResponse.h"
#import "AuthenticationSuccessResponse.h"
#import "AuthenticationFailureResponse.h"

@interface AuthenticationProcess() <NSURLConnectionDataDelegate>
@property (strong, nonatomic) id <AuthenticationProcessDelegate> delegate;
@property (strong, nonatomic) NSURL *authenticateUrl;
@property (strong, nonatomic) NSData *receivedData;
@property (strong, nonatomic) NSDictionary *jsonResponse;
@property (strong, nonatomic) NSURLConnection *connection;
@property (nonatomic) BOOL isComplete;
@end

@implementation AuthenticationProcess  //TODO locking to ensure that when a request has been sent no other can be made until that one has completed?

- (void)start:(id <AuthenticationProcessDelegate>)delegate {
    
    self.delegate = delegate;
    self.authenticateUrl = [NSURL URLWithString:[NSString stringWithFormat:@"%@%@", [self.delegate authenticateTo], @"/json/authenticate"]];
    
    [self initiateAuthentication];
}

- (void)initiateAuthentication {
    
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:self.authenticateUrl cachePolicy:NSURLRequestReloadIgnoringCacheData timeoutInterval:10];
    
    [request setHTTPMethod:@"POST"];
    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [request setHTTPBody:[@"{}" dataUsingEncoding:NSUTF8StringEncoding]];
    self.connection = [[NSURLConnection alloc] initWithRequest:request delegate:self];
}

- (void)submitCallbacks:(NSDictionary *)callbacks {
    
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:self.authenticateUrl cachePolicy:NSURLRequestReloadIgnoringCacheData timeoutInterval:10];
    
    [request setHTTPMethod:@"POST"];
    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    
    NSError *error;
    NSData *json = [NSJSONSerialization dataWithJSONObject:callbacks options:NSJSONWritingPrettyPrinted error:&error];
    
    [request setHTTPBody:json];
    self.connection = [[NSURLConnection alloc] initWithRequest:request delegate:self];
}

////////////////////////////////////////////////////////////////////////
//
// NSURLConnectionDataDelegate
//
- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response {
    //    self.receivedData = [[NSMutableData alloc] init];
    NSLog(@"didRecieveResponse");
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data {
    self.receivedData = [[NSMutableData alloc] initWithData:data];
    NSLog(@"didReceiveData");
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {
    NSLog(@"WHOOPS! Something went wrong"); //TODO
    //    UIAlertView* alert = [[UIAlertView alloc] initWithTitle:@"Error"
    //                                                    message:error.description
    //                                                   delegate:nil
    //                                          cancelButtonTitle:@"Close"
    //                                          otherButtonTitles:nil];
    //
    //    [alert show];
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection {
    NSString* s = [[NSString alloc] initWithData:self.receivedData encoding:NSUTF8StringEncoding];
    NSLog(@"Received data %@", s);
    
    NSDictionary *jsonResponse = [NSJSONSerialization JSONObjectWithData:self.receivedData options:NSJSONReadingMutableContainers error:nil];
    
    if ([jsonResponse valueForKey:@"tokenId"]) {
        [self.delegate authenticationSucceededWithResult:[[AuthenticationSuccessResponse alloc] initWithData:jsonResponse]];
    } else if ([jsonResponse valueForKey:@"errorMessage"]) {
        [self.delegate authenticationFailedWithResult:[[AuthenticationFailureResponse alloc] initWithData:jsonResponse]];
    } else {
        [self.delegate responseReceivedWithCallbacks:[[AuthenticationCallbackResponse alloc] initWithData:jsonResponse]];
    }
    
    NSLog(@"connectionDidFinishLoading");
}
//
// NSURLConnectionDataDelegate
//
////////////////////////////////////////////

@end