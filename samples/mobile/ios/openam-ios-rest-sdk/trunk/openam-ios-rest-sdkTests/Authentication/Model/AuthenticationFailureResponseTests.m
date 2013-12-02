//
//  AuthenticationFailureResponseTests.m
//  openam-ios-rest-sdk
//
//  Created by Phill on 30/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "AuthenticationFailureResponse.h"
#import "JsonTestHelper.h"

@interface AuthenticationFailureResponseTests : XCTestCase

@end

@implementation AuthenticationFailureResponseTests

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
    
    NSString *json = @"{\"errorMessage\": \"ERROR_MESSAGE\", \"failureUrl\": \"FAILURE_URL\"}";

    AuthenticationFailureResponse *failureResponse = [[AuthenticationFailureResponse alloc] initWithData:[JsonTestHelper convertJsonStringToDictionary:json]];
    
    XCTAssertTrue([failureResponse.errorMessage isEqualToString:@"ERROR_MESSAGE"]);
    XCTAssertTrue([failureResponse.failureUrl isEqualToString:@"FAILURE_URL"]);
    XCTAssertTrue([failureResponse.asData count] == 2);
}

@end
