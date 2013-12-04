'use strict';

angular.module('openAm.services', [])

.value('restEndpoints', 
{	
   
	logout: '/json/sessions?_action=logout',
	authenticate: '/json/authenticate',
	getcookienamefortoken: '/identity/json/getcookienamefortoken',
	forgotpassword: '/json/users?_action=forgotPassword',
	getAllSessions: '/sessions?_queryId=all',
	getAllIdentities: '/users?_queryID=*'
})

.factory('AppService', function($http, $localStorage, $sessionStorage ){


	var service = {

		resetStorage: function( ) {
           	$localStorage.$reset();
           	$localStorage.servers = [];
       		$localStorage.serverIndex = 0;
        },

        resetSession: function( ) {
            $sessionStorage.$reset();
        },

        resetHeaders: function( ) {
          
            //remove any headers from previous sessions
            $http.defaults.headers.common = {};

            //Apply default headers for all REST calls
            $http.defaults.headers.common['Content-Type'] = 'application/json';
            $http.defaults.headers.common['Cache-Control'] = 'no-cache';
  
        }
        
    }

    return service;
})

.factory('AuthService', function ($rootScope, $log, $routeParams, $location, $http, restEndpoints, $localStorage, $sessionStorage, AppService ) {

	/*  
	 *  Auth  0: Not Authenticated
	 *  Auth  1: Authenticated User
	 *  Auth  2: Authenticated Administrator
	 */

    var service = {

    	setAuthLevel: function( val ) {
            $sessionStorage.authLevel = val;
        },

        getAuthLevel: function() {
            return $sessionStorage.authLevel;
        },

        login: function( authCallbackObj,success,error ) {
          
            service.getRequirements( 

            	authCallbackObj, 

            	function(e){

            		 service.getTokenName(function(tokenName){
				  		
				  		// phonegap incorrectly returns 200 so need to double check here
					  	if( angular.isDefined($routeParams.goto) || angular.isDefined(e.successUrl) ){
					  		$http.defaults.headers.common[tokenName] = e.tokenId;
		                    success(e);
		                }else {
		                    error(e);
		                }

		            })
            	}, 
            	error
            );
  
        },

        getTokenName: function(success){
            if( angular.isUndefined( $sessionStorage.tokenName ) ) {
                $http.get( $localStorage.servers[$localStorage.serverIndex].openam + restEndpoints.getcookienamefortoken, { withCredentials: true })
                .success( 
                	function(name){
                		if(success){
                			service.setAuthLevel(0);
                			$sessionStorage.tokenName = name.string;
	                        success($sessionStorage.tokenName);
	                    }
                	});
            } else {
                if(success){
                    success( $sessionStorage.tokenName );
                }
            }
        },
 
        getRequirements: function(authCallbackObj,success, error) {
        	
        
        	var query = angular.isDefined($localStorage.servers[$localStorage.serverIndex].params) ? '?'+$localStorage.servers[$localStorage.serverIndex].params : '';
   
        	$http.post(
        		$localStorage.servers[$localStorage.serverIndex].openam + restEndpoints.authenticate + query , 
        		authCallbackObj,
        		{ 
                	withCredentials: true,
        		})
	        .success(success)
	        .error(error);
	 
        },


        logout: function(success) {
            $http.post( $localStorage.servers[$localStorage.serverIndex].openam + restEndpoints.logout, {} ,{ withCredentials: true })
            .success(function(e){
            	AppService.resetSession();
            	service.setAuthLevel(0);
                success(e);
            })
			.error(function(e){
                AppService.resetSession();
                service.setAuthLevel(0);
                success(e);
            })
        }

    }

    return service;
})


.factory('UserService', function($http, $localStorage){

	var service = {

        getUser: function(data, success,error) {

            var url = $localStorage.servers[$localStorage.serverIndex].openam + '/json/' + data.realm + '/users/' + data.username;
    		$http.get( url ,{ withCredentials: true })
            .success(success) 	
            .error(error);
         
        },

    

        updateProfile: function(user,putConfig,success,error){

            var url = $localStorage.servers[$localStorage.serverIndex].openam + '/json/' + $localStorage.servers[$localStorage.serverIndex].realm + '/users/' + $localStorage.username;
            $http.put( url, user, { withCredentials: true } )
            .success(success)
            .error(error);
        }
       
	 }

    return service;
})


.factory('AdminService', function($http, $localStorage, restEndpoints ){


	var service = {

        getIdentities: function(realm, success, error) {
            var url = $localStorage.servers[$localStorage.serverIndex].openam + '/json/' + realm + restEndpoints.getAllIdentities;
    		$http.get( url ,{ withCredentials: true })
            .success(success) 	
            .error(error);
        },

        getSessions: function( realm, success, error) {

            var url = $localStorage.servers[$localStorage.serverIndex].openam + '/json/' + realm + restEndpoints.getAllSessions;
    		$http.get( url ,{ withCredentials: true })
            .success(success)
            .error(error);
        }
       
	 }

    return service;
});











