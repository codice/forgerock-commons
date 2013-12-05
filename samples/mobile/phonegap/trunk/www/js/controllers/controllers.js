'use strict';

/* Controllers */
angular.module('openAm.controllers', [])

.controller('LoginCtrl', function($scope, $location, $localStorage, $sessionStorage, $routeParams ,$http, $log, AuthService, AdminService, localize ) {

    $scope.showPassword = false;
    $scope.ready = false;
    $scope.messageClass = 'alert-danger';
    var authCallbackObj = {};

    if( angular.isDefined($localStorage.servers) && angular.isDefined($localStorage.serverIndex) && $localStorage.servers.length>0 ){
        
        if($localStorage.servers[$localStorage.serverIndex].online){
            $scope.messageClass = 'alert-info';
        }
        $scope.message = $localStorage.servers[$localStorage.serverIndex].name;
    }

    if(!$localStorage.rememberMe){
        $localStorage.username = '';
    }

    $scope.rememberMe = $localStorage.rememberMe;
    $scope.username =   $localStorage.username;

    if(angular.isDefined($localStorage.servers) && $localStorage.servers.length>0){
        $scope.ready = true;
        AuthService.getRequirements(
            authCallbackObj, 
            function(data){
                authCallbackObj = data;
            }

        );
    }

    $scope.login = function() {
       
        authCallbackObj.callbacks[0].input[0].value = $scope.username;
        authCallbackObj.callbacks[1].input[0].value  = $scope.password;

        $scope.message = '.';

        AuthService.login( 

            authCallbackObj, 

            function(success) {

                if(!$scope.rememberMe){
                    $localStorage.rememberMe = false;
                    $localStorage.username = '';
                }

                $localStorage.rememberMe = $scope.rememberMe;
                $localStorage.username = authCallbackObj.callbacks[0].input[0].value;

                AuthService.setAuthLevel(1);

                if($routeParams.goto){
                    location.href = $routeParams.goto;
                }else if(angular.isDefined(success.successUrl)){
                    if(success.successUrl === '/openam/console'){
                        //PhoneGap returns 200 so need to check if data object contains result
                        AdminService.getSessions('/', 
                            function(result){
                                if(angular.isDefined(result.result)){
                                    AuthService.setAuthLevel(2);
                                    $location.path(success.successUrl)
                                }else{
                                    $location.path('profile');
                                }
                            },
                            function(){
                                $location.path('profile');
                            }
                        )
                    }else{
                        $location.path(success.successUrl);
                    }
                }

            },

            function(error) 
            {
                $log.error('AuthService.login: Error');
                $scope.message = localize.getLocalizedString('authenticationFailed');
                $scope.messageClass = 'alert-danger';
                delete authCallbackObj['authId'];
               
            }

        );

    };

    
})


.controller('ProfileCtrl', function( $scope ) {
    
})


.controller('ServerSettingsCtrl', function($scope, $location, $localStorage, AuthService, $log, UtilsService) {

    $scope.servers = $localStorage.servers;
    $scope.selectedIndex = $localStorage.serverIndex;
    $scope.stage = 'url';
    $scope.url = $scope.name = $scope.realm = '';`

    $scope.addUrl = function(){
        $scope.stage = 'name';
    };

    $scope.finish = function(){

        var server = {}, temp;
        temp = $scope.url.split('?');

        server.openam = temp[0];
        server.params = temp[1];

        temp = UtilsService.parseQueryString(temp[1]);

        server.realm = angular.isDefined(temp.realm) ? temp.realm : '';
        server.url =  $scope.url;
        server.name = $scope.name.length<1 ? server.url : $scope.name;
        server.online = false;

        $scope.servers.splice(0,0, server );
        $scope.selectServer(0,server);
        $scope.url = $scope.name = $scope.realm = '';
        $scope.stage = 'url';

        $localStorage.servers = $scope.servers;
        $localStorage.serverIndex = $scope.selectedIndex = 0;
    };


   
    $scope.editServer = function(index){
        
        var server   = $localStorage.servers[index];
      
        $scope.stage = 'url';
        $scope.url   = server.url;
        $scope.name  = server.name;
        $scope.realm = server.realm;
        
        $scope.deleteServer(index,server);

    };


    $scope.selectServer = function(index){

         $scope.selectedIndex = $localStorage.serverIndex = index;

         AuthService.getRequirements(
            {}, 
            function(data,status,headers){
                $scope.servers[index].status = status;
                if(angular.isDefined(data.authId) && $scope.servers[index].status == 200){
                    $scope.servers[index].online = true;
                }else{
                    $scope.servers[index].online = false;
                }
            }, 
            function(data,status,headers){
                $scope.servers[index].status = status;
                $scope.servers[index].online = false;
            }

        );  

    };

    $scope.deleteServer = function(index){

        $scope.servers.splice(index,1);
        $localStorage.servers = $scope.servers;

        if(index == $localStorage.serverIndex){
            $localStorage.serverIndex = $scope.selectedIndex = 0;
        }
    };

    $scope.login = function(index){
       $scope.selectedIndex = $localStorage.serverIndex = index;
       $location.path('/login');
    };
 
})

.controller('AdminCtrl', function($scope, localize, AdminService, $log, $localStorage, UserService) {

     $scope.selectRealm = function(index){

        $scope.realmIndex = index;

        AdminService.getIdentities(

            $scope.realms[$scope.realmIndex],

            function(data){
                $scope.identities = data.result;    
            }, 
            function(){
                $scope.identities = {};
            }

        );

        AdminService.getSessions(

            $scope.realms[$scope.realmIndex],

            function(data){
                $scope.sessions = data.result; 
            }, 
            function(){
                $scope.sessions = {};
            }

        )

    };

    $scope.addRealm = function(newRealm){
        $scope.realms.push(newRealm)
        $localStorage.servers[$localStorage.serverIndex].console.realms = $scope.realms;
    };

    $scope.getUser = function(user){

        $scope.identity = {};
        var data = {};
        data.username = user;
        data.realm = $scope.realms[$scope.realmIndex];

        UserService.getUser(
            data,
            function(success){
                $log.log('UserService.getUser success', success);
                $scope.identity = success;
            },
            function(error){
                $log.error('UserService.getUser error', error);
                $scope.identity = identity;
            }
        )
    }


    if( angular.isDefined($localStorage.servers[$localStorage.serverIndex].console) ){
        $scope.realmIndex =   $localStorage.servers[$localStorage.serverIndex].console.realmIndex;
        $scope.realms =       $localStorage.servers[$localStorage.serverIndex].console.realms;
    }else{
        $localStorage.servers[$localStorage.serverIndex].console = {};
        $localStorage.servers[$localStorage.serverIndex].console.realms = $scope.realms = ["/"];
        $localStorage.servers[$localStorage.serverIndex].console.realmIndex = $scope.realmIndex = 0;
    }

    $scope.selectRealm($scope.realmIndex);

});



























