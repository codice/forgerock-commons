//
//  HttpHelper.m
//  OpenAMOAuth2SampleApp
//
//  Created by Phill on 13/11/2013.
//  Copyright (c) 2013 ForgeRock. All rights reserved.
//

#import "HttpHelper.h"

@implementation HttpHelper

+ (NSString*)urlEncodeDictionary:(NSDictionary*)dictionary {
    
    NSMutableString *url = [[NSMutableString alloc] init];
    BOOL firstParam = true;
    
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

@end
