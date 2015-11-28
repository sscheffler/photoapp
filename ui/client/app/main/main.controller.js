'use strict';

angular.module('testApp')
  .controller('MainCtrl', function ($scope, $http) {
    $scope.awesomeThings = [];
    $scope.couchdb={
            url: 'http://localhost:5984',
            database: 'android'
        };
        $scope.url = $scope.couchdb.url;
        $scope.database = $scope.couchdb.database;
        $scope.image='';

    /*$http.get('/api/things').success(function(awesomeThings) {
      $scope.awesomeThings = awesomeThings;
    });*/

  var getData = function(){
            var url=$scope.couchdb.url+'/'+$scope.couchdb.database+'/_all_docs?limit=20&descending=true&include_docs=true';
            $http.get('api/photos')
                .then(function successCallback(response) {
                    $scope.data=response.data.rows;
                    console.log($scope.data);

                    var rows = response.data.rows;
                    rows.sort(function(a,b){
                        //console.log(a.doc.time + " - "+ b.doc.time);
                        return b.doc.time - a.doc.time;
                    });

                    return rows;
                }, function errorCallback(response) {
                    console.log('error');
                    console.log(response);
                    return '';
                });
        };
        getData();
        $scope.refresh = function(){
            console.log("refresh main");
            getData();
        }; 

        $scope.setImage = function(id){

        	//$scope.image='app/photo';
        	$http.get('api/photo/'+ id)
        		.then(function successCallback(response) {
                    console.log(response.data.url);
                    $scope.image = response.data.url;
                    //$scope.data=response.data.rows;
                    console.log(response.data);
                }, function errorCallback(response) {
                    console.log('error');
                });		






          //$scope.image=$scope.couchdb.url+'/' + $scope.couchdb.database+'/'+id+'/picture';
        };

        $scope.saveCredentials = function(url, database)
         {
             $scope.couchdb.url=url;
             $scope.couchdb.database=database;
         };

        $scope.resetCredentials = function()
         {
             $scope.url=$scope.couchdb.url;
             $scope.database=$scope.couchdb.database;
         };

  });
