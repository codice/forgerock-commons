//
//  AuthenticationCallbackTests.m
//  openam-ios-rest-sdk
//
//  Created by Phill on 30/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "AuthenticationCallback.h"
#import "JsonTestHelper.h"

@interface AuthenticationCallbackTests : XCTestCase

@end

@implementation AuthenticationCallbackTests

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

    NSString *json = @"{\"type\": \"PasswordCallback\",\"output\": [{\"name\": \"prompt\",\"value\": \" Password: \"}],\"input\": [{\"name\": \"IDToken2\",\"value\": \"\"}]}";
    
    AuthenticationCallback *callback = [[AuthenticationCallback alloc] initWithData:[JsonTestHelper convertJsonStringToDictionary:json]];
    
    XCTAssertTrue([callback.type isEqualToString:@"PasswordCallback"]);
    XCTAssertTrue([callback.inputs count] == 1);
    XCTAssertTrue([callback.outputs count] == 1);
}

@end
