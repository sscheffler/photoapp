angular.module('app', [])
    .controller('AppController', function($scope, $http){
        $scope.couchdb={
            url: 'http://localhost:5984',
            database: 'android'
        };
        $scope.url = $scope.couchdb.url;
        $scope.database = $scope.couchdb.database;
        $scope.image='';

        var getData = function(){
            var url=$scope.couchdb.url+'/'+$scope.couchdb.database+'/_all_docs?limit=20&descending=true&include_docs=true';
            $http.get(url)
                .then(function successCallback(response) {
                    console.log('success');
                    $scope.data=response.data.rows;
                    console.log($scope.data);

                    var rows = response.data.rows;
                    rows.sort(function(a,b){
                        console.log(a.doc.time + " - "+ b.doc.time);
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
            console.log("refresh");
            getData();
        };

        $scope.setImage = function(id){
          $scope.image=$scope.couchdb.url+'/' + $scope.couchdb.database+'/'+id+'/picture';
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