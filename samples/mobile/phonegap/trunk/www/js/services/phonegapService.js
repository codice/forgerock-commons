.factory('PhonegapService', function($http, $localStorage, restEndpoints ){


	var service = {

        getConnection: function() {

        	var connectType = 'Unknown connection';

    		
    		if( angular.isDefined(navigator.connection)){

	    		var networkState = navigator.connection.type;
	    		var states = {};
	    		states[navigator.connection.UNKNOWN]  = 'Unknown connection';
		    	states[navigator.connection.ETHERNET] = 'Ethernet connection';
		    	states[navigator.connection.WIFI]     = 'WiFi connection';
		    	states[navigator.connection.CELL_2G]  = 'Cell 2G connection';
		    	states[navigator.connection.CELL_3G]  = 'Cell 3G connection';
		    	states[navigator.connection.CELL_4G]  = 'Cell 4G connection';
		    	states[navigator.connection.NONE]     = 'No network connection';



		    	connectType = states[networkState];
        	}
            
		  	return connectType;  
        }
       
	 }

    return service;
});