//
//  HttpHelper.h
//  openam-ios-rest-sdk
//
//  Copyright (c) 2013 ForgeRock AS.
//

#import <Foundation/Foundation.h>

static const char base64Table[64] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

@interface HttpHelper : NSObject

+ (NSString *)urlEncodeDictionary:(NSDictionary *)dictionary;

+ (NSDictionary *)decodeUrlParameters:(NSString *)url;

+ (NSString *)base64Encode:(NSString *)string;

@end
