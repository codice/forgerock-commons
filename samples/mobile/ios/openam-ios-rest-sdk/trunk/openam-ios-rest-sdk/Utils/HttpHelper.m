//
//  HttpHelper.m
//  openam-ios-rest-sdk
//
//  Created by Phill on 21/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import "HttpHelper.h"

@implementation HttpHelper

+ (NSString*)urlEncodeDictionary:(NSDictionary*)dictionary {
    
    NSMutableString *url = [[NSMutableString alloc] init];
    BOOL firstParam = true;
    
    NSLog(@"%@", dictionary);
    
    if (dictionary) {
        NSEnumerator *keys = [dictionary keyEnumerator];
        for (NSString *key in keys) {
            NSString *format;
            if (firstParam) {
                format = @"%@=%@";
                firstParam = false;
            } else {
                format = @"&%@=%@";
            }
            NSString *value = [dictionary objectForKey:key];
            [url appendFormat:format, [self urlEncode:key], [self urlEncode:value]];
        }
    }
    
    return [[NSString alloc] initWithString:url];
}

+ (NSString *)urlEncode:(NSString *)param {
    return [param stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
}

+ (NSDictionary*)decodeUrlParameters:(NSString*)url {
    NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
    NSArray *params = [url componentsSeparatedByString:@"&"];
    for (int i = 0; i < [params count]; i++) {
        NSString *keyValue = [params objectAtIndex:i];
        NSArray *pair = [keyValue componentsSeparatedByString:@"="];
        if ([pair count] == 2) {
            [dictionary setObject:[[pair objectAtIndex:1] stringByReplacingPercentEscapesUsingEncoding:NSASCIIStringEncoding]
                           forKey:[[pair objectAtIndex:0] stringByReplacingPercentEscapesUsingEncoding:NSASCIIStringEncoding]];
        }
    }
    return dictionary;
}

+ (NSString*)base64Encode:(NSString *)string {
    
    NSData *stringAsData = [string dataUsingEncoding:NSUTF8StringEncoding];
    NSMutableString *result = [NSMutableString string];
    const unsigned char *chars = [stringAsData bytes];
    
    for (int i = 0; i + 2 < [stringAsData length]; i+=3) {
        [result appendFormat:@"%c%c%c%c",
         base64Table[chars[i] >> 2],
         base64Table[((chars[i] & 0x03) << 4)  + (chars[i+1] >> 4)],
         base64Table[((chars[i+1] & 0x0f) << 2)  + (chars[i+2] >> 6)],
         base64Table[chars[i+2] & 0x3f]];
        
    }
    
    int padding = [stringAsData length] % 3;
    
    NSUInteger i = [stringAsData length] - padding;
    
    if (padding == 2) {
        [result appendFormat:@"%c%c%c=",
         base64Table[chars[i] >> 2],
         base64Table[((chars[i] & 0x03) << 4)  + (chars[i+1] >> 4)],
         base64Table[((chars[i+1] & 0x0f) << 2)]];
    } else if (padding == 1) {
        [result appendFormat:@"%c%c==",
         base64Table[chars[i] >> 2],
         base64Table[((chars[i] & 0x03) << 4)  + (chars[i+1] >> 4)]];
    }
    return result;
}

@end
