//
//  RestServiceTests.m
//  openam-ios-rest-sdk
//
//  Created by Phill on 30/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "OCMockObject.h"
#import "OCMockRecorder.h"
#import "OCMConstraint.h"
#import "OCMArg.h"
#import "NSNotificationCenter+OCMAdditions.h"

#import "RestService.h"

@interface RestServiceTests : XCTestCase {
    RestService *restService;
}

@end

@implementation RestServiceTests

- (void)setUp
{
    [super setUp];
    // Put setup code here; it will be run once, before the first test case.
    
    restService = [RestService instance];
}

- (void)tearDown
{
    // Put teardown code here; it will be run once, after the last test case.
    [super tearDown];
}

- (void)testThatAlwaysGetSameInstance {
    
    RestService *instance1 = [RestService instance];
    RestService *instance2 = [RestService instance];
    
    XCTAssertTrue(instance1 == instance2);
}

- (void)testThatAlwaysGetSameInstanceWithInit {
    
    RestService *instance1 = [[RestService alloc] init];
    RestService *instance2 = [[RestService alloc] init];
    
    XCTAssertTrue(instance1 == instance2);
}

- (void)testGetWithNoHeadersWithNoParamsOnCompletion {
// 
//    id mockRestService = [OCMockObject partialMockForObject:restService];
//    [[[mockRestService stub] andCall:@selector(mockCreateRequest:) onObject:self] createRequest:[OCMArg any]];
//    
    [restService get:@"http://phill.internal.forgerock." withHeaders:nil withParams:nil onCompletion:^(NSDictionary *response, NSError *err) {
        
        
        
    }];
    
    
}

- (NSMutableURLRequest *)mockCreateRequest:(NSString *)url {
    id mockRequest = [OCMockObject mockForClass:[NSURLRequest class]];
    
    [[[mockRequest stub] andReturn:nil] createRequest:[OCMArg any]];
    
    return mockRequest;
}

@end
