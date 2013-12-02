//
//  JsonTestHelper.h
//  openam-ios-rest-sdk
//
//  Created by Phill on 30/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface JsonTestHelper : NSObject

+ (NSDictionary *)convertJsonStringToDictionary:(NSString *)jsonString;

@end
