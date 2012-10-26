/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011-2012 ForgeRock AS. All rights reserved.
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://forgerock.org/license/CDDLv1.0.html
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at http://forgerock.org/license/CDDLv1.0.html
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 */

/*global define, $, _ */

/**
 * @author mbilski
 */
define("config/validators/UserValidators", [
], function(constants, eventManager) {
    var obj = {
            "registrationEmail": {
                "name": "Present and valid email",
                "dependencies": [
                    "org/forgerock/commons/ui/common/util/ValidatorsUtils"
                ],
                "validator": function(el, input, callback, utils) {
                    var v = $(input).val();
                    
                    if(v === "") {
                        callback($.t("common.form.validation.required"));
                        return;
                    }
                    
                    if(!utils.emailPattern.test(v)) {
                        callback($.t("common.form.validation.emailNotValid"));
                        return;
                    }
                    
                    callback();
                }
            },
            "userName": {
                "name": "Valid and unique username",
                "dependencies": [
                    "org/forgerock/commons/ui/common/util/ValidatorsUtils",
                    "org/forgerock/commons/ui/user/delegates/UserDelegate"
                ],
                "validator": function(el, input, callback, utils, userDelegate) {
                    var v = $(input).val();
                    
                    if(v === "") {
                        callback($.t("common.form.validation.required"));
                        return;
                    }
                    
                    userDelegate.checkUserNameAvailability(v, function(available) {
                        if(!available) {
                            callback($.t("common.form.validation.usernameExists"));
                        } else {
                            callback();
                        }
                    });              
                }
            },
            "name": {
                "name": "Only alphabetic characters",
                "dependencies": [
                    "org/forgerock/commons/ui/common/util/ValidatorsUtils"
                ],
                "validator": function(el, input, callback, utils) {
                    var v = $(input).val();
                    
                    if(v === "") {
                        callback($.t("common.form.validation.required"));
                        return;
                    }
                    
                    if(!utils.namePattern.test(v)) {
                        callback($.t("common.form.validation.onlyAlphabeticCharacters"));
                        return;
                    }

                    callback();  
                }
            },
            "phone": {
                "name": "Only numbers etc",
                "dependencies": [
                    "org/forgerock/commons/ui/common/util/ValidatorsUtils"
                ],
                "validator": function(el, input, callback, utils) {
                    var v = $(input).val();
                    
                    if(v === "") {
                        callback($.t("common.form.validation.required"));
                        return;
                    }
                    
                    if(!utils.phonePattern.test(v)) {
                        callback($.t("common.form.validation.onlyNumbersAndSpecialCharacters"));
                        return;
                    }

                    callback(); 
                }
            },
            "password": {
                "name": "Password validator",
                "dependencies": [
                    "org/forgerock/commons/ui/common/util/ValidatorsUtils"
                ],
                "validator": function(el, input, callback, utils) {
                    var v = $(input).val(), reg, errors = [];
                    
                    if(el.find("input[name=oldPassword]").length !== 0) {
                        if(el.find("input[name=oldPassword]").val() === v) {
                            errors.push($.t("common.form.validation.cannotMatchOldPassword"));
                        }
                        
                        if(v === "" && $(el).find("input[name=passwordConfirm]").val() === "") {
                            $(el).find("input[name=passwordConfirm]").trigger("keyup");
                            callback("disabled");
                            utils.hideBox(el);
                            return;
                        }  else {
                            utils.showBox(el);
                        }
                    }

                    if(v.length < 8) {
                        errors.push($.t("common.form.validation.atLeast8Characters"));
                    }
                    
                    reg = /[(A-Z)]+/;
                    if(!reg.test(v)) {
                        errors.push($.t("common.form.validation.atLeastOneCapitalLetter"));
                    }
                    
                    reg = /[(0-9)]+/;
                    if( !reg.test(v) ) {
                        errors.push($.t("common.form.validation.atLeastOneNumber"));
                    }
                    
                    if( v === "" || v === $(el).find("input[name=userName]").val() ) {
                        errors.push($.t("common.form.validation.cannotMatchLogin"));
                    }
                    
                    if(errors.length === 0) {
                        callback(); 
                    } else {
                        callback(errors);
                    }
                    
                    $(el).find("input[name=passwordConfirm]").trigger("keyup");
                }
            },
            "passwordConfirm": {
                "name": "Password confirmation",
                "dependencies": [
                    "org/forgerock/commons/ui/common/util/ValidatorsUtils"
                ],
                "validator": function(el, input, callback, utils) {
                    var v = $(input).val();
                    
                    if(el.find("input[name=oldPassword]").length !== 0) {
                        if(v === "" && $(el).find("input[name=password]").val() === "") {
                            utils.hideValidation($(el).find("input[name=password]"), el);
                            callback("disabled");
                            utils.hideBox(el);
                            return;
                        } else {
                            utils.showBox(el);
                        }
                    }

                    if( v === "" || v !== $(el).find("input[name=password]").val() ) {
                        callback([$.t("common.form.validation.confirmationMatchesPassword")]);
                        return;
                    }

                    callback(); 
                }
            },
            "passPhrase": {
                "name": "Min 4 characters",
                "dependencies": [
                ],
                "validator": function(el, input, callback) {
                    var v = $(input).val();
                    if($(el).find("input[name=oldPassPhrase]").length !== 0) {
                        if($(el).find("input[name=oldPassPhrase]").val() === v) {
                            callback("disabled");
                            return;
                        }
                    }
                    
                    if(v.length < 4) {
                        callback($.t("common.form.validation.minimum4Characters"));
                        return;
                    }

                    callback();  
                }
            },
            "siteImage": {
                "name": "Site image not same as old",
                "dependencies": [
                ],
                "validator": function(el, input, callback) {
                    var v = $(input).val();
                    if(el.find("input[name=oldSiteImage]").length !== 0) {
                        if(el.find("input[name=oldSiteImage]").val() === v) {
                            callback("disabled");
                            return;
                        }
                    }
                    
                    callback();  
                }
            },
            "termsOfUse": {
                "name": "Acceptance required",
                "dependencies": [
                ],
                "validator": function(el, input, callback) {              
                    if(!$(input).is(':checked')) {
                        callback($.t("common.form.validation.acceptanceRequiredForRegistration"));
                        return;
                    }

                    callback();  
                }
            },
            "profileEmail": {
                "name": "Correct and unique email",
                "dependencies": [
                    "org/forgerock/commons/ui/common/util/ValidatorsUtils",
                    "org/forgerock/commons/ui/user/delegates/UserDelegate",
                    "org/forgerock/commons/ui/common/main/Configuration"
                ],
                "validator": function(el, input, callback, utils, userDelegate, conf) {
                    var v = $(input).val();
                    
                    if(conf.loggedUser.email === v) {
                        callback();
                        return;
                    }
                    
                    if(v === "") {
                        callback($.t("common.form.validation.required"));
                        return;
                    }
                    
                    if(!utils.emailPattern.test(v)) {
                        callback($.t("common.form.validation.emailNotValid"));
                        return;
                    }
                    
                    userDelegate.checkUserNameAvailability(v, function(available) {
                        if(!available) {
                            callback($.t("common.form.validation.emailAddressAlreadyExists"));
                        } else {
                            callback();
                        }
                    });              
                }
            },
            "oldPassword": {
                "name": "Required field",
                "dependencies": [
                    "org/forgerock/commons/ui/common/main/Configuration",
                    "org/forgerock/commons/ui/user/delegates/UserDelegate"
                ],
                "validator": function(el, input, callback, conf, userDelegate) {
                    var v = $(input).val();
                    
                    if(v === "") {
                        callback($.t("common.form.validation.incorrectPassword"));
                        return;
                    }
                    
                    userDelegate.checkCredentials(conf.loggedUser.userName, v, function(result) {
                        if(result.result) {
                            callback();
                            $(input).attr('data-validation-status', 'ok');
                            $("input[name='Continue']").click();
                        } else {
                            callback($.t("common.form.validation.incorrectPassword"));
                        }
                    });
                }
            },
            "resetPasswordCorrectLogin": {
                "name": "Reset Password Correct Login",
                "dependencies": [
                    "org/forgerock/commons/ui/common/util/ValidatorsUtils",
                    "org/forgerock/commons/ui/user/delegates/UserDelegate"
                ],
                "validator": function(el, input, callback, utils, userDelegate) {
                    var v = $(input).val();
                    
                    if(v === "") {
                        callback($.t("common.form.validation.required"));
                        $(input).attr('data-validation-status', 'error');
                        $("input[name='Update']").click();
                        return;
                    }
                   
                    userDelegate.checkUserNameAvailability(v, function(available) {
                        if(!available) {
                            callback();
                            $(input).attr('data-validation-status', 'ok');
                        } else {
                            callback("No such user");
                            $(input).attr('data-validation-status', 'error');
                        }
                        
                        $("input[name=fgtnSecurityAnswer]").trigger("keyup");
                        $("input[name=password]").trigger("keyup");
                        $("input[name=passwordConfirm]").trigger("keyup");
                        
                        $("input[name='Update']").click();
                    });  
                }
            },
            "securityAnswer": {
                "name": "Check if security answer is correct",
                "dependencies": [
                    "org/forgerock/commons/ui/common/util/ValidatorsUtils",
                    "org/forgerock/commons/ui/user/delegates/UserDelegate"
                ],
                "validator": function(el, input, callback, utils, userDelegate) {
                    var v = $(input).val(), userName;
                    if(v === "") {
                        callback($.t("common.form.validation.required"));
                        return;
                    }
                    userName = $(el).find("input[name='resetUsername']").val();
                    userDelegate.getBySecurityAnswer(userName, v, 
                            function(result) {
                        callback();
                    },      function() {
                        callback("x");
                    });
                }
            },
            "newSecurityAnswer": {
                "name": "",
                "dependencies": [
                ],
                "validator": function(el, input, callback) {
                    var v = $(input).val();
                    
                    if(el.find("input[name=oldSecurityQuestion]").val() !== el.find("select[name=securityQuestion]").val()) {
                        if(v === "") {
                            callback($.t("common.form.validation.required"));
                        } else {
                            callback();
                        }
                        
                        return;
                    }
                    
                    if(v === "") {
                        callback("disabled");
                    } else {
                        callback();
                    }
                }
            }
    };
    
    return obj;
});
