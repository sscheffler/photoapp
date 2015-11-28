'use strict';

angular.module('testApp')
  .config(function ($stateProvider) {
console.log("testfall");
    $stateProvider
      .state('couchdb', {
        url: '/couchdb',
        templateUrl: 'app/couchdb/couchdb.html',
        controller: 'CouchdbCtrl'
      });
  });
