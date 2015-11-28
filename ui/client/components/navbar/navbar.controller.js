'use strict';

angular.module('testApp')
  .controller('NavbarCtrl', function ($scope, $location, $http) {
    $scope.menu = [{
      'title': 'Home',
      'link': '/'
    }];

    $scope.isCollapsed = true;

    $scope.isActive = function(route) {
      return route === $location.path();
    };
        $scope.refresh = function(){
            console.log("refresh");
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
  });
