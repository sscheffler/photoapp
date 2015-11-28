'use strict';

describe('Controller: CouchdbCtrl', function () {

  // load the controller's module
  beforeEach(module('testApp'));

  var CouchdbCtrl, scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    CouchdbCtrl = $controller('CouchdbCtrl', {
      $scope: scope
    });
  }));

  it('should ...', function () {
    expect(1).toEqual(1);
  });
});
