'use strict';

var _ = require('lodash');
var http = require('http');
var fs = require('fs');

// Get list of photos
exports.index = function(req, res) {
	var id = req.params.id;

	var url1 = 'http://192.168.43.81:5984/android/'+id+'/picture';
	var url2 = 'http://192.168.178.28:5984/android/'+id+'/picture';
  res.json({url: url1});
};