'use strict';

/* Directives */

angular.module('openAm.directives', [])
  

.directive('navigationBar', [ '$location', 'AuthService', function( $location, AuthService) {
  return {
    restrict: 'E',
    templateUrl: 'partials/navigationBar.html',
    link: function($scope, element, attrs) {
  
        $scope.logout = function(){
            AuthService.logout(
                function(){
                    $location.path('/login');
                }
            );
        }

        $scope.$on("$routeChangeSuccess", function () {
            $scope.authLevel =  AuthService.getAuthLevel();  
        });

        // hides the navigation upon menu selection
        $("#main-nav li:not(.dropdown) a").click(function(event) {
            $("#main-nav").removeClass("in").addClass("collapse");
        });

    }

  };

}])

.directive('confirmModal', [ '$location', 'AppService', 'AuthService', function ( $location, AppService, AuthService) {
    return {
        restrict: 'E',
        templateUrl: 'partials/confirmModal.html',
        link: function (scope, elm, attrs, ctrl) {
          
            scope.save = function(){
               
                AppService.resetStorage();
                AppService.resetHeaders();
                AppService.resetSession();
                AuthService.setAuthLevel(-1);
                $location.path('/settings');
                
            }

        }
    }
}])

.directive('languageModal', [ '$localStorage', '$location', 'localize',  function ( $localStorage, $location, localize) {
    return {
        restrict: 'E',
        templateUrl: 'partials/languageModal.html',
        link: function ($scope, elm, attrs, ctrl) {
          
            $scope.locales = 
            [
                {
                    language: 'English',
                    locale: 'en'
                },
                {
                    language: 'Español',
                    locale: 'es-419'
                }
            ];

            $scope.changeLanguage = function(index){

                localize.setLanguage($scope.locales[index].locale);    
                 $scope.selectedIndex = index;
            }

        }
    }
}])

.directive('profile', [ '$localStorage', '$sessionStorage', '$location', 'UserService', 'localize', 'UtilsService', '$window', function ( $localStorage, $sessionStorage, $location, UserService, localize, UtilsService, window) {
    return {
        restrict: 'A',
        link: function ($scope, elm, attrs) {
          
            $scope.wideView = false;
            $scope.ready = false;
            $scope.toggle = true;  
            $scope.user = $scope.master = {};
            $scope.wideView = window.innerWidth> 990 ? true : false;

     
            var data = {};
            data.username = $localStorage.username;
            data.realm = $localStorage.servers[$localStorage.serverIndex].realm ;

            UserService.getUser(

                data, 

                function(obj){

                    $sessionStorage.user = UtilsService.objectToLower(obj);
                    $scope.ready = true;
                    $scope.user = $sessionStorage.user;
                    $scope.master = angular.copy($sessionStorage.user);

                },
                function(e){
                    console.log('profile error');
                });

            $scope.resetProfile = function() {
                $scope.user = angular.copy($scope.master);
                $scope.profileForm.$setPristine();
            };


            $scope.isUnchanged = function(user) {
                return angular.equals(user, $scope.master);
            };
            
            $scope.updateProfile = function() {

                $scope.message = localize.getLocalizedString('processing');

                UserService.updateProfile({

                    givenname : $scope.user.givenname,
                    sn : $scope.user.sn,
                    mail : $scope.user.mail,
                    telephonenumber : $scope.user.telephonenumber,
                    postaladdress : $scope.user.postaladdress

                }, { withCredentials: true} ,

                function(success){

                    $scope.master = UtilsService.objectToLower(success);
                    $scope.user = angular.copy($scope.master);
                    $scope.message = localize.getLocalizedString('userProfileUpdateSuccessful');
                    $scope.profileForm.$setPristine();

                },
                function(error){
                    $scope.message = localize.getLocalizedString('error');
                })

            };

        }
    }
}])











  
