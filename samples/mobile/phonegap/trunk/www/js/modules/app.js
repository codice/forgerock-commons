'use strict';

angular.module('openAm', [
  'ngRoute',
  'ngAnimate',
  'openAm.services',
  'openAm.directives',
  'openAm.controllers',
  'ngStorage',
  'localization',
  'ngFilters'
])

.config(['$routeProvider', function($routeProvider) {

  	$routeProvider

	.when('/login', 
	{
		templateUrl: 'partials/login.html',
		controller:  'LoginCtrl',
		authLevelRequired: 0
	})
	.when('/profile', 
	{
		templateUrl: 'partials/profile.html',
		controller:  'ProfileCtrl',
		authLevelRequired: 1
	})
	.when('/serverSettings', 
	{
		templateUrl: 'partials/serverSettings.html',
		controller: 'ServerSettingsCtrl',
		authLevelRequired: 0
	})
	.when('/openam/console', 
	{
		templateUrl: 'partials/admin.html',
		controller: 'AdminCtrl',
		authLevelRequired: 2
	})
	.otherwise({redirectTo: '/login' });

}])

.run([ '$rootScope', '$location', 'AppService', 'AuthService', '$localStorage', function ( $rootScope, $location, AppService, AuthService, $localStorage) {

	//remove references to any previous sessions or headers
	AppService.resetHeaders();
	AppService.resetSession();
	AuthService.setAuthLevel(0);

    $rootScope.$on("$routeChangeStart", function ( event, next) {

		if(angular.isUndefined($localStorage.servers) || $localStorage.servers.length<1){

	       AppService.resetStorage();
	       $location.path('/serverSettings');

	    }else if(angular.isDefined(next.$$route) && next.$$route.authLevelRequired > 0){

	    	if( AuthService.getAuthLevel() <  next.$$route.authLevelRequired ) {
	    		$location.path('/login');
		    }
		}

    });

}]);



