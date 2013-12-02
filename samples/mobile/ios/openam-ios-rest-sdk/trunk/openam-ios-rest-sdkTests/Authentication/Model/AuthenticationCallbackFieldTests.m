//
//  AuthenticationCallbackFieldTests.m
//  openam-ios-rest-sdk
//
//  Created by Phill on 30/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "AuthenticationCallbackField.h"
#import "JsonTestHelper.h"

@interface AuthenticationCallbackFieldTests : XCTestCase

@end

@implementation AuthenticationCallbackFieldTests

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
    
    NSString *json = @"{\"name\": \"IDToken2\",\"value\": \"VALUE\"}";

    AuthenticationCallbackField *callbackField = [[AuthenticationCallbackField alloc] initWithData:[JsonTestHelper convertJsonStringToDictionary:json]];
    
    XCTAssertTrue([callbackField.name isEqualToString:@"IDToken2"]);
    XCTAssertTrue([callbackField.value isEqualToString:@"VALUE"]);
}

@end
