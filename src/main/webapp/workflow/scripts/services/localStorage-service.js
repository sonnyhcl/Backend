'use strict';

activitiApp.service('SessionService' , function(localStorageService){
	this.createlastEventId = function(){
		localStorageService.set('lastEventId' , 0);
	}
	this.getLastEventId = function(){
		return localStorageService.get('lastEventId');
	}
	this.setLastEventId = function(val){
		localStorageService.set('lastEventId' , val);
	}
})
