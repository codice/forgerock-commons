//
//  AuthenticationSuccessResponseTests.m
//  openam-ios-rest-sdk
//
//  Created by Phill on 30/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "AuthenticationSuccessResponse.h"
#import "JsonTestHelper.h"

@interface AuthenticationSuccessResponseTests : XCTestCase

@end

@implementation AuthenticationSuccessResponseTests

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
    
    NSString *json = @"{\"tokenId\": \"TOKEN_ID\", \"successUrl\": \"SUCCESS_URL\"}";
    
    AuthenticationSuccessResponse *successResponse = [[AuthenticationSuccessResponse alloc] initWithData:[JsonTestHelper convertJsonStringToDictionary:json]];
    
    XCTAssertTrue([successResponse.tokenId isEqualToString:@"TOKEN_ID"]);
    XCTAssertTrue([successResponse.successUrl isEqualToString:@"SUCCESS_URL"]);
    XCTAssertTrue([successResponse.asData count] == 2);
}

@end
