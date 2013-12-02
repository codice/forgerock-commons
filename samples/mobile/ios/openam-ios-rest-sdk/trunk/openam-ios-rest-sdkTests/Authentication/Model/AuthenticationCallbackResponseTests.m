//
//  AuthenticationCallbackResponseTests.m
//  openam-ios-rest-sdk
//
//  Created by Phill on 30/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "AuthenticationCallbackResponse.h"
#import "JsonTestHelper.h"

@interface AuthenticationCallbackResponseTests : XCTestCase

@end

@implementation AuthenticationCallbackResponseTests

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

- (void)testInitWithData {
    
    NSString *json = @"{\"authId\": \"AUTHID\",\"template\": \"TEMPLATE\",\"stage\": \"DataStore1\",\"callbacks\": [{\"type\": \"NameCallback\",\"output\": [{\"name\": \"prompt\",\"value\": \" User Name: \"}],\"input\": [{\"name\": \"IDToken1\",\"value\": \"\"}]},{\"type\": \"PasswordCallback\",\"output\": [{\"name\": \"prompt\",\"value\": \" Password: \"}],\"input\": [{\"name\": \"IDToken2\",\"value\": \"\"}]}]}";
    
    AuthenticationCallbackResponse *callbackResponse = [[AuthenticationCallbackResponse alloc] initWithData:[JsonTestHelper convertJsonStringToDictionary:json]];
    
    XCTAssertTrue([callbackResponse.authId isEqualToString:@"AUTHID"]);
    XCTAssertTrue([callbackResponse.stage isEqualToString:@"DataStore1"]);
    XCTAssertTrue([callbackResponse.templateUrl isEqualToString:@"TEMPLATE"]);
    XCTAssertTrue([callbackResponse.callbacks count] == 2);
    XCTAssertTrue([callbackResponse.asData count] == 4);
}

@end
