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
