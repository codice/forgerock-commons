//
//  DataStoreLoginTests.m
//  openam-ios-rest-sdk
//
//  Created by Phill on 30/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "DataStoreLogin.h"
#import "JsonTestHelper.h"
#import "AuthenticationCallback.h"
#import "AuthenticationCallbackField.h"

@interface DataStoreLoginTests : XCTestCase

@end

@implementation DataStoreLoginTests

- (void)setUp
{
    [super setUp];
    // Put setup code here; it will be run once, before the first test case.
}

- (void)tearDown
{
    // Put teardown code here; it will be run once, after the last test case.
    [super tearDown];
}

- (void)testInitAndSetProperties {
    
    NSString *jsonResponse = @"{\"authId\": \"AUTHID\",\"template\": \"\",\"stage\": \"DataStore1\",\"callbacks\": [{\"type\": \"NameCallback\",\"output\": [{\"name\": \"prompt\",\"value\": \" User Name: \"}],\"input\": [{\"name\": \"IDToken1\",\"value\": \"\"}]},{\"type\": \"PasswordCallback\",\"output\": [{\"name\": \"prompt\",\"value\": \" Password: \"}],\"input\": [{\"name\": \"IDToken2\",\"value\": \"\"}]}]}";
    
    AuthenticationCallbackResponse *response = [[AuthenticationCallbackResponse alloc] initWithData:[JsonTestHelper convertJsonStringToDictionary:jsonResponse]];
    
    DataStoreLogin *dataStoreLogin = [[DataStoreLogin alloc] initWithCallbacks:response.callbacks];
    
    [dataStoreLogin setUserName:@"USERNAME" setPassword:@"PASSWORD" onResponse:response];
    
    AuthenticationCallback *callback1 = response.callbacks[0];
    AuthenticationCallbackField *callback1InputField1 = callback1.inputs[0];
    XCTAssertTrue([callback1InputField1.value isEqualToString:@"USERNAME"]);
    
    AuthenticationCallback *callback2 = response.callbacks[1];
    AuthenticationCallbackField *callback2InputField1 = callback2.inputs[0];
    XCTAssertTrue([callback2InputField1.value isEqualToString:@"PASSWORD"]);
}

@end
