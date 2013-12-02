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
