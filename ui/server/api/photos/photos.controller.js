'use strict';

var _ = require('lodash');
var http = require('http');

exports.index = function(req, res) {
var couchdb = {
            host: 'http://localhost:5984/android/_all_docs?include_docs=true',
            path: '/android/_all_docs?limit=20&descending=true&include_docs=true'
        };
var url='http://localhost:5984/android/_all_docs?limit=40&include_docs=true';


        http.get(url, function(result){
        	var bodyarr = [];

  			result.on('data', function(chunk){
    	  		bodyarr.push(chunk);
		  	});
		  	result.on('end', function(){
		    	console.log(bodyarr.join('').toString())
		    	res.json(JSON.parse(bodyarr.join('').toString()))
		  	});
        }).on('error', function(e) {
  			console.log("Got error: " + e.message);
  			
});
};		
