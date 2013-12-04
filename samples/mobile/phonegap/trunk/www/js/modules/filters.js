
angular.module('ngFilters', [])

.directive('onlyNumbers', function(){
   return {
     require: 'ngModel',
     link: function(scope, el, at, ctrl) {

        ctrl.$parsers.push(function (input) {

            var newVal = input.replace(/[^0-9]+/g, ''); 

            if (newVal!=input) {
               ctrl.$setViewValue(newVal);
               ctrl.$render();
            }         

            return newVal;         
        });
     }
   };
})

.factory('UtilsService', function(){

    var service = {
        
        parseQueryString: function( string ) {

            var params = {}, queries, temp, i, l;
            if(string){
                queries = string.split("&");
                for ( i = 0, l = queries.length; i < l; i++ ) {
                    temp = queries[i].split('=');
                    params[temp[0]] = temp[1];
                }
            }
            return params;
         },

         objectToLower: function( obj ) {

            var cleaned = {};
            angular.forEach(obj, function(val, key){
                cleaned[key.toLowerCase()] = val;
            });
            return cleaned;
         }

    }
    return service;

});

