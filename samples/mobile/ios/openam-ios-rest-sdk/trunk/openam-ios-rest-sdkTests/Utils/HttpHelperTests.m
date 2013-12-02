//
//  HttpHelperTests.m
//  openam-ios-rest-sdk
//
//  Created by Phill on 30/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "HttpHelper.h"

@interface HttpHelperTests : XCTestCase

@end

@implementation HttpHelperTests

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

- (void)testUrlEncodeDictionary {
    
    NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
    [dictionary setValue:@"Test spaces" forKey:@"Key with spaces"];
    
    NSString *urlEncoded = [HttpHelper urlEncodeDictionary:dictionary];
    
    XCTAssertTrue([urlEncoded isEqualToString:@"Key%20with%20spaces=Test%20spaces"]);
}

- (void)testDecodeUrlParameters {
    
    NSDictionary *decodeded = [HttpHelper decodeUrlParameters:@"Key%20with%20spaces=Test%20spaces"];
    
    XCTAssertTrue([[decodeded valueForKey:@"Key with spaces"] isEqualToString:@"Test spaces"]);
}

- (void)testBase64Encode {
    
    NSString *encoded = [HttpHelper base64Encode:@"n432owu ji43pk2 wrfie wp3i rndjekws//*34 340-i o;'|]"];
    
    XCTAssertTrue([encoded isEqualToString:@"bjQzMm93dSBqaTQzcGsyIHdyZmllIHdwM2kgcm5kamVrd3MvLyozNCAzNDAtaSBvOyd8XQ=="]);
}

@end
