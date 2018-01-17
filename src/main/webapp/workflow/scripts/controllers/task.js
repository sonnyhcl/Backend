/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
'use strict';


angular.module('activitiApp')
    .controller('TaskController', ['$rootScope', '$scope', '$translate', '$timeout','$location', '$modal', '$popover', 'appResourceRoot', 'CommentService', 'TaskService', '$routeParams', 'AppDefinitionService',
        function ($rootScope, $scope, $translate, $timeout, $location, $modal, $popover, appResourceRoot, CommentService, TaskService, $routeParams, AppDefinitionService) {

            // Ensure correct main page is set
            $rootScope.setMainPageById('tasks');

            $scope.selectedTask = { id: $routeParams.taskId };

            $scope.deploymentKey = $routeParams.deploymentKey;

            $scope.$on('task-completed', function (event, data) {
                $rootScope.addAlertPromise($translate('TASK.ALERT.COMPLETED', data));
                //alert("TaskCtrl");
                $scope.openTasks();
            });

            $scope.openTasks = function(task) {
                var path='';
                if($rootScope.activeAppDefinition && !ACTIVITI.CONFIG.integrationProfile) {
                    path = "/apps/" + $rootScope.activeAppDefinition.id;
                }
                $location.path(path + "/tasks");
            };
        }]
);

angular.module('activitiApp')
  .controller('TaskDetailController', ['SessionService' ,'$filter','$interval' , '$rootScope', '$scope', '$translate', '$http','$location', '$routeParams', 'appResourceRoot', 'CommentService', 'TaskService', 'FormService', 'RelatedContentService', '$timeout', '$modal', '$popover',
        function (SessionService , $filter, $interval , $rootScope, $scope, $translate, $http, $location, $routeParams, appResourceRoot, CommentService, TaskService, FormService, RelatedContentService, $timeout, $modal, $popover) {

	 $scope.$on('task-completed' , function(event , data){
		 	console.log("TaskDetailCtrl : " + data.taskId + " completed");
		 	 $scope.$broadcast('complete-apply' , data);
	 })
    $scope.model = {
        // Indirect binding between selected task in parent scope to have control over display
        // before actual selected task is switched
	    task: $scope.selectedTask,
	    completeButtonDisabled: false,
	    claimButtonDisabled: false,
        uploadInProgress: false
	};
    $scope.activeTab = 'form';
    /**
     * 
     */
    /**
     * 轮询
     */
   // $scope.lastEventId = 0;
//    $rootScope.eventTimer = $interval(function(){
//		$http.get(ACTIVITI.CONFIG.contextRoot+'/api/revents')
//		.success(function(data){
//			//console.log("test revents" , data);
//			if(data.length>0){
//				data.sort(function sortNumber(a , b){
//					return a.id - b.id;
//				})
//				for(var  i = 0 ; i < data.length ; i++){
//					var event = data[i];
//					if('RW_PLAN' == event.type){
//						 $scope.$broadcast('RW_PLAN' , event.data);
//						console.log("receive RW_PLAN" , event.data);
//					}
//					if('RW_STOP' == event.type){
//						 $scope.$broadcast('RW_STOP' , event.data);
//						 //console.log("receive RW_PLAN" , event.data);
//					}
//				}
//			}
//		})
//	} , 2000);
    /**
     * *******************************************************************
     * custom variables
     * ********************************************************************
     */
//    $scope.isVoyagingTask = false;
//    $scope.isADTask = false;
//    $scope.isApply = false;
//    
//    $scope.nowTask = {};
//    $scope.NowLoc = {};
//    $scope.velocity = {};
   // $scope.durax = 10;  //D/A Task --- /min , 默认1min
//    $scope.pvars = {};
//    $scope.pidxs = {};
//    $scope.dispTime = null;
//    $scope.waitTime =  0;
    /******************************************************************/
    $scope.model.involvementSummary = {
        loading: false
    };

    $scope.model.contentSummary = {
        loading: false
    };

    $scope.model.commentSummary = {
        loading: false
    };

    /**
     * ***************************************************************
     * Custom functions
     * ***************************************************************
     */
    //返回下标，供子controller使用
    $scope.createPidxs = function(pvars){
    	var i = 0;
    	var pidxs = {};
    	var arr = pvars;
    	for(i in arr){
    		pidxs[arr[i]['name']] = i;
    	}
    	return pidxs;
    }
    //dateString to ms
    $scope.dateStr2ms = function(dateStr){
    	return Date.parse(dateStr);
    }
    
    //ms to dateString 
    $scope.ms2dateStr = function(vms){
    	var d = new Date();
	    d.setTime(vms);
		if(d != 'Invalid Date') {
				return $filter('date')(d, "yyyy-MM-dd HH:mm:ss");	
		}else{
			return null;
		}
    }
    
    
//    var voyageTaskTimer = $interval(function(){
//    	if($scope.isVoyagingTask == true || $scope.isApply == true){
//    	   	$http.get(ACTIVITI.CONFIG.contextRoot+'/api/runtime/process-instances/'+$scope.nowTask['processInstanceId']+'/variables')
//        	.success(function(data){
//        		$scope.pvars = data;
//        		$scope.pidxs = $scope.createPidxs($scope.pvars);	
//        		if($scope.pvars[$scope.pidxs['NowLoc']]['value']['Name'] == null && $scope.pvars[$scope.pidxs['PrePort']]['value']['Name'] != undefined &&
//        				$scope.pvars[$scope.pidxs['NextPort']]['value']['Name'] != undefined){
//        			$scope.pvars[$scope.pidxs['NowLoc']]['value']['Name'] =$scope.pvars[$scope.pidxs['PrePort']]['value']['Name']+"-->"+$scope.pvars[$scope.pidxs['NextPort']]['value']['Name'];
//        		}
//        		console.log("Now State : "+$scope.pvars[$scope.pidxs['State']]['value']);
//        		if($scope.pvars[$scope.pidxs['State']]['value'] == 'other'){
//        				$scope.pvars[$scope.pidxs['StartTime']]['value'] = $scope.dispTime;
//        				$http.put(ACTIVITI.CONFIG.contextRoot+'/api/runtime/vessel-procinst/'+$scope.nowTask['processInstanceId']+'/variables' , $scope.pvars)
//            			.success(function(res){
//            				console.log("Voyaging Task 结束， 将StartTime上传")
//            				$scope.isVoyagingTask = false;
//            				$scope.completeTask();
//            			});
//        				
//        		}
//        		//计算展示时间
//        		console.log("StartTime : "+$scope.pvars[$scope.pidxs['StartTime']]['value']);
//        		console.log("timeStamp : "+$scope.pvars[$scope.pidxs['NowLoc']]['value']['timeStamp']/60000 + " min");
//        		var ms = Date.parse($scope.pvars[$scope.pidxs['StartTime']]['value']) + $scope.pvars[$scope.pidxs['NowLoc']]['value']['timeStamp'];
//        		var d = new Date();
//        		d.setTime(ms);
//        		if(d != 'Invalid Date'){
//        			$scope.dispTime = $filter('date')(d, "yyyy-MM-dd HH:mm:ss"); 
//        		}
//        	})
//    	}
//    }, 2000);
    
    //如果是D/A Task
//    $scope.countDown = $scope.durax * 60;
//    var duraTimer = $interval(function(){
//    	if($scope.isADTask == true){
//    		$scope.countDown--;
//    		$scope.waitTime++;
//    		console.log("wait time : "+$scope.waitTime);
//    	}
//	} , 1000); 
//    $scope.setDuration = function(){
//    	$interval.cancel(duraTimer);
//    	console.log("new dura :"+$scope.durax*60)
//    	$scope.countDown = $scope.durax * 60;
//    	duraTimer = $interval(function(){
//    		$scope.countDown--;
//    		$scope.waitTime++;
//    		console.log("wait time : "+$scope.waitTime);
//    	} , 1000);
//    }
//    $scope.$watch('durax' , function(newVal , oldVal){
//    		 console.log("watch dura  : "+newVal+" "+oldVal);
//    })
//    $scope.$watch('countDown' , function(newVal){	
//    	if(newVal == 0){
//			//修改StartTime
//    		$interval.cancel(duraTimer);
//		 	$http.get(ACTIVITI.CONFIG.contextRoot+'/api/runtime/process-instances/'+$scope.nowTask['processInstanceId']+'/variables')
//        	.success(function(data){
//        		$scope.pvars = data;
//        		console.log("variables after countDown :"+data);
//        		$scope.pidxs = $scope.createPidxs($scope.pvars);
//        		var ms = Date.parse($scope.pvars[$scope.pidxs['StartTime']]['value'])+$scope.waitTime*60*1000;
//    			var d = new Date();
//    			d.setTime(ms);
//    			console.log("d :"+d+"ms : "+ms);
//    			if(d == 'Invalid Date'){
//        			d = new Date();
//        		}
//    			
//        		console.log("** StartTime :"+$scope.pvars[$scope.pidxs['StartTime']]['value'])
//        		console.log("$filter('date')" + $filter('date')(d, "yyyy-MM-dd HH:mm:ss"))
//        		$scope.pvars[$scope.pidxs['StartTime']]['value'] = $filter('date')(d, "yyyy-MM-dd HH:mm:ss"); 
//        		console.log("$scope.pvars[$scope.pidxs['StartTime']]['type'] : "+$scope.pvars[$scope.pidxs['StartTime']]['type']);
//        		//$scope.pvars[$scope.pidxs['StartTime']]['type'] = 'date';
//        		console.log(" && StartTime  :"+$scope.pvars[$scope.pidxs['StartTime']]['value'])
//        		$http.put(ACTIVITI.CONFIG.contextRoot+'/api/runtime/vessel-procinst/'+$scope.nowTask['processInstanceId']+'/variables' , $scope.pvars)
//    			.success(function(res){
//    				console.log("New StartTime : "+res[$scope.pidxs['StartTime']]['value']);
//    				$scope.completeTask();
//    				$scope.isADTask = false;
//    				$scope.waitTime = 0;
//    			});
//        	});
//		}
//    });

    /***************************************************************************************************/
    
    $scope.resetModel =  function() {
        // Reset tabs
        $scope.taskTabs = [];
        if ($scope.model.task.formKey != null) {
            $scope.taskTabs.push(
                {
                    'id': 'form',
                    'title': 'TASK.TITLE.FORM'
                }
            );
            $scope.activeTab = 'form';
        } else {
            $scope.activeTab = 'details';
        }

        $scope.taskTabs.push( {
            'id': 'details',
            'title': 'TASK.TITLE.DETAILS'
        });

        // Reset summary model
        $scope.model.involvementSummary = {
            loading: true
        };

        $scope.model.contentSummary = {
            loading: true
        };

        $scope.model.commentSummary = {
            loading: true
        };

        $scope.model.content = undefined;
        $scope.model.comments = undefined;

        $timeout(function() {
            // Force refresh of all auto-height components as the tabs can be hidden or shown
            $rootScope.window.forceRefresh = true;
        }, 100);

        var today = new Date();
        $scope.today = new Date(today.getFullYear(), today.getMonth(), today.getDate() , 0, 0, 0, 0);
    };

    $scope.showPeople = function() {
        $scope.activeTab = 'details';
    };
    $scope.showContent = function() {
        $scope.activeTab = 'details';
    };
    $scope.showComments= function() {
        $scope.activeTab = 'details';
    };
    $scope.toggleForm= function() {
        if($scope.activeTab == 'form') {
            $scope.activeTab = 'details';
        } else {
            $scope.activeTab = 'form';
        }
    };

    // The selected task is set by the parent, eg in tasks.js
    $scope.$watch('selectedTask', function(newValue) {
        if(newValue && newValue.id) {
            $scope.model.taskUpdating = true;
            $scope.model.task = newValue;
            if ($scope.model.task.formKey) {
                $scope.resetModel();
            }
            $scope.getTask(newValue.id);
        } else {
            // Reset whole model to make sure nothing is left behind in case a new task will
            // be selected in the future
            $scope.model = {};
        }
    });

	// Ensure correct main page is set
    $rootScope.setMainPageById('tasks');


    $scope.setTaskAssignee = function(user) {
        var alertData = {
            firstName: user.firstName,
            lastName: user.lastName,
            taskName: $scope.model.task.name
        };

        TaskService.assignTask($scope.model.task.id, user.id).then(function(data) {
            $rootScope.addAlertPromise($translate('TASK.ALERT.ASSIGNED', alertData));
            $scope.model.task = data;
        });
    };

    $scope.setTaskAssigneeByEmail = function(email) {
        TaskService.assignTaskByEmail($scope.model.task.id, email).then(function() {
            $scope.model.task.assignee = {email: email}; // Faking a user (since it will only be an email address)
        });
    };

    $scope.involvePerson = function (user) {
        var alertData = {
            firstName: user.firstName,
            lastName: user.lastName,
            taskName: $scope.model.task.name
        };

        TaskService.involveUserInTask(user.id, $scope.model.task.id).then(function() {
            $rootScope.addAlertPromise($translate('TASK.ALERT.PERSON-INVOLVED',
                alertData));

            if(!$scope.model.task.involvedPeople) {
                $scope.model.task.involvedPeople = [user];
            } else {
                $scope.model.task.involvedPeople.push(user);
            }
        });
    };

    $scope.involvePersonByEmail = function(email) {
        TaskService.involveUserInTaskByEmail(email, $scope.model.task.id).then(function() {
            if(!$scope.model.task.involvedPeople) {
                $scope.model.task.involvedPeople = {email: email};
            } else {
                $scope.model.task.involvedPeople.push({email: email});
            }
        });
    };

    $scope.removeInvolvedUser = function (user) {
        var alertData = {
            firstName: user.firstName,
            lastName: user.lastName,
            taskName: $scope.model.task.name
        };

        TaskService.removeInvolvedUserInTask(user, $scope.model.task.id).then(function() {
            $rootScope.addAlertPromise($translate('TASK.ALERT.PERSON-NO-LONGER-INVOLVED',
                alertData));

            $scope.model.task.involvedPeople.splice($.inArray(user, $scope.model.task.involvedPeople),1);
        });
    };

    $scope.getTask = function(taskId) {
        $scope.model.loading = true;
        $scope.model.formData = undefined;
        $scope.model.hasFormKey = false;
        if ($scope.model.task.formKey) {
            $scope.model.hasFormKey = true;
        }

        $http({method: 'GET', url: ACTIVITI.CONFIG.contextRoot + '/app/rest/tasks/' + taskId}).
            success(function(response, status, headers, config) {

                // Do not replace the model, as it's still used in the task-list
                angular.extend($scope.model.task, response);

                $scope.model.loading = false;
                $scope.noSuchTask = false;
                          
                /*
                 * *****************************************************
                 * Set task symbol
                 * ******************************************************
                 */
//                $scope.nowTask = response;
//                if(response['name'] == 'Voyaging'){
//                	 $scope.isApply = false;
//                	 $scope.isVoyagingTask = true;
//                	 console.log("enter into Voyaging Task");
//                }
//                if(response['name'] == 'Anchoring-Docking'){
//                	 $scope.isVoyagingTask = false;
//                	$scope.isADTask = true;
//                	console.log("enter into anchoring-docking Task");
//                }
//                
//                if(response['name'] == 'Apply for spare parts'){
//                	$scope.isApply = true;
//                	console.log("enter into Applying Task");
//                }
                /**********************************************************************/
                if (!$scope.model.hasFormKey) {
                    $scope.resetModel();
                }

                $scope.loadComments();
                $scope.loadRelatedContent();

                if($scope.model.task.processInstanceId) {
                    $scope.loadProcessInstance();
                } else {
                    $scope.model.processInstance = null;
                }

                $scope.refreshInvolvmentSummary();

                // Loading form already
                if ($scope.model.task.formKey !== null && $scope.model.task.formKey !== undefined) {
                    FormService.getTaskForm($scope.model.task.id).then(function(formData) {
                        $scope.model.formData = formData;
                    });
                } else {
                    $scope.model.formData = undefined;
                }

                $scope.model.taskUpdating = false;
            }).
            error(function(response, status, headers, config) {
                $scope.noSuchTask = true;
            });
    };

    $scope.$watch('model.task.involvedPeople', function(newValue) {
        $scope.refreshInvolvmentSummary();
    }, true);

    $scope.refreshInvolvmentSummary = function() {
        if($scope.model.task) {
            var newValue = $scope.model.task.involvedPeople;
            $scope.model.involvementSummary.loading = false;
            if(newValue && newValue.length > 0) {
                $scope.model.involvementSummary.count = newValue.length;

                if(newValue.length > 8) {
                    $scope.model.involvementSummary.overflow = true;
                    $scope.model.involvementSummary.items = [];

                    for(var i=0; i< 8; i++) {
                        $scope.model.involvementSummary.items.push(newValue[i]);
                    }
                } else {
                    $scope.model.involvementSummary.overflow = false;
                    $scope.model.involvementSummary.items = newValue;
                }

            } else {
                $scope.model.involvementSummary.count = 0;
            }
        }
    };

    $scope.$watch('model.content.data', function(newValue) {
        if($scope.model.task) {
            $scope.model.contentSummary.loading = false;
            if(newValue && newValue.length > 0) {
                $scope.model.contentSummary.count = newValue.length;

                if(newValue.length > 8) {
                    $scope.model.contentSummary.overflow = true;
                    $scope.model.contentSummary.items = [];

                    for(var i=0; i< 8; i++) {
                        $scope.model.contentSummary.items.push(newValue[i]);
                    }
                } else {
                    $scope.model.contentSummary.overflow = false;
                    $scope.model.contentSummary.items = newValue;
                }

            } else {
                $scope.model.contentSummary.count = 0;
            }
        }
    }, true);

    $scope.$watch('model.comments.data', function(newValue) {
       $scope.refreshCommentSummary();
    }, true);

    $scope.refreshCommentSummary = function() {
        if ($scope.model.task) {
            var newValue = $scope.model.comments ? $scope.model.comments.data : undefined;
            $scope.model.commentSummary.loading = false;

            if(newValue) {
                $scope.model.commentSummary.count = newValue.length;
            } else {
                $scope.model.commentSummary.loading = true;
                $scope.model.commentSummary.count = undefined;
            }
        }
    };

    $scope.dragOverContent = function(over) {
        if(over && ! $scope.model.contentSummary.addContent) {
            $scope.model.contentSummary.addContent = true;
        }
    };

    $scope.$watch('model.content.data', function(newValue) {
        if($scope.model.task) {
        }
    }, true);

    $scope.loadComments = function() {
        CommentService.getTaskComments($scope.model.task.id).then(function (data) {
            $scope.model.comments = data;

            $scope.refreshCommentSummary();
        });
    };

    $scope.toggleCreateComment = function() {
        if($scope.model.commentSummary.addComment) {
            $scope.model.commentSummary.newComment = undefined;
        }

        $scope.model.commentSummary.addComment = ! $scope.model.commentSummary.addComment;

        if($scope.model.commentSummary.addComment) {
            $timeout(function() {
                angular.element('.focusable').focus();
            }, 100);

        }
    };

    $scope.toggleCreateContent = function() {
        $scope.model.contentSummary.addContent = ! $scope.model.contentSummary.addContent;
    };

    $scope.onContentUploaded = function(content) {
        if ($scope.model.content && $scope.model.content.data) {
            $scope.model.content.data.push(content);
            RelatedContentService.addUrlToContent(content);
            $scope.model.selectedContent = content;
        }
        $rootScope.addAlertPromise($translate('TASK.ALERT.RELATED-CONTENT-ADDED', content), 'info');
        $scope.toggleCreateContent();
    };

    $scope.onContentDeleted = function(content) {
        if ($scope.model.content && $scope.model.content.data) {
            $scope.model.content.data.forEach(function(value, i, arr){
                if (content === value) {
                    arr.splice(i, 1);
                }
            })
        }
    };

    $scope.selectContent = function (content) {
        if ($scope.model.selectedContent == content) {
            $scope.model.selectedContent = undefined;
        } else {
            $scope.model.selectedContent = content;
        }
    };

    $scope.confirmNewComment = function() {
        $scope.model.commentSummary.loading = true;
        CommentService.createTaskComment($scope.model.task.id, $scope.model.commentSummary.newComment.trim())
            .then(function(comment) {
                $scope.model.commentSummary.newComment = undefined;
                $scope.model.commentSummary.addComment = false;
                $scope.model.commentSummary.loading = false;
                $rootScope.addAlertPromise($translate('TASK.ALERT.COMMENT-ADDED', $scope.model.task));
                $scope.loadComments();
            });
    };

    $scope.$watch('model.task.dueDate', function(newValue, oldValue) {
        if (!$scope.model.taskUpdating && $scope.model.task) {
            // Update task due-date

            if (oldValue === null && newValue === null
                || oldValue === null && newValue === undefined
                || oldValue === undefined && newValue === undefined
                || oldValue === undefined && newValue === null) {
                return;
            }

            // Normalize the date to midnight
            if(newValue && newValue !== undefined && newValue.getHours && newValue.getHours() != 23) {
                newValue.setHours(23);
                newValue.setMinutes(59);
                newValue.setSeconds(59);
                $scope.model.task.dueDate = newValue;
            }

            if (new Date(oldValue).getTime() != new Date(newValue).getTime() || oldValue != null && newValue != null) {
                $scope.model.taskUpdating = true;
                // Explicitly force NULL value when undefined to make sure the null
                // is sent to the service
                var data = {
                    dueDate: newValue ? newValue : null
                };
                TaskService.updateTask($scope.model.task.id, data).then(function(response) {
                    $scope.model.taskUpdating = false;
                });
            }
        }
    });

    $scope.createTaskInline = function() {
        if(!$scope.newTask) {
            $scope.newTask = {
                name: 'New task',
                inline: true
            };
        }
    };

    $scope.createProcess = function() {
        $rootScope.createProcessInstance = true;
        $scope.openProcessInstance();
    };

    $scope.selectProcessDefinition = function (definition) {
        $scope.newProcessInstance.processDefinitionId = definition.id;
        $scope.newProcessInstance.name = definition.name + ' - ' + new moment().format('MMMM Do YYYY');

        $timeout(function () {
            angular.element('#start-process-name').focus();
        }, 20);
    };

    $scope.closeInlineTaskCreation = function($event) {
        $scope.newTask = undefined;
        $event.stopPropagation();
    };

    $scope.completeTask = function() {
        $scope.model.completeButtonDisabled = true;
        TaskService.completeTask($scope.model.task.id);
    };

    $scope.claimTask = function() {
        $scope.model.loading = true;
        $scope.model.claimButtonDisabled = true;
        TaskService.claimTask($scope.model.task.id).then(function(data) {
            // Refetch data on claim success
            $scope.getTask($scope.model.task.id);
        });
    };

    // TODO: move process instance loading to separate service and merge with process.js
    $scope.loadProcessInstance = function() {
        $http({method: 'GET', url: ACTIVITI.CONFIG.contextRoot + '/app/rest/process-instances/' + $scope.model.task.processInstanceId}).
            success(function(response, status, headers, config) {
                $scope.model.processInstance = response;
            }).
            error(function(response, status, headers, config) {
                // Do nothing. User is not allowed to see the process instance
            });
    };

    $scope.openProcessInstance = function(id) {
        $rootScope.root.selectedProcessId = id;
        var path='';
        if($rootScope.activeAppDefinition && !ACTIVITI.CONFIG.integrationProfile) {
            path = "/apps/" + $rootScope.activeAppDefinition.id;
        }
        $location.path(path + "/processes");
    };

    $scope.returnToTaskList = function() {
        var path='';
        if($rootScope.activeAppDefinition && !ACTIVITI.CONFIG.integrationProfile) {
            path = "/apps/" + $rootScope.activeAppDefinition.id;
        }
        $location.path(path + "/tasks");
    };

    // OLD STUFF


    $scope.returnToList = function() {
        $location.path("/tasks");
    };

    $scope.loadRelatedContent = function() {
        $scope.model.content = undefined;
        TaskService.getRelatedContent($scope.model.task.id).then(function (data) {
            $scope.model.content = data;
        });
    };

    $scope.$watch("model.content", function(newValue) {
       if(newValue && newValue.data && newValue.data.length > 0) {
           var needsRefresh = false;
           for(var i=0; i<newValue.data.length; i++) {
               var entry =  newValue.data[i];
               if(!entry.contentAvailable) {
                   needsRefresh = true;
                   break;
               }
           }
       }
    }, true);

    $scope.editComment = function() {
        $scope.model.editingComment = true;
    };

    $scope.stopEditComment = function() {
        $scope.model.editingComment = false;
    };


    $scope.userInvolved = function (user) {
        var alertData = {
            firstName: user.firstName,
            lastName: user.lastName,
            taskName: $scope.model.task.name
        };

        TaskService.involveUserInTask(user.id, $scope.model.task.id).then(function() {
            $rootScope.addAlertPromise($translate('TASK.ALERT.PERSON-INVOLVED',
                alertData));

            if(!$scope.model.task.involvedPeople) {
                $scope.model.task.involvedPeople = [user];
            } else {
                $scope.model.task.involvedPeople.push(user);
            }

        });
    };

    $scope.assigneeSelected = function(user) {
        var alertData = {
            firstName: user.firstName,
            lastName: user.lastName,
            taskName: $scope.model.task.name
        };

        TaskService.assignTask($scope.model.task.id, user.id).then(function() {
            $rootScope.addAlertPromise($translate('TASK.ALERT.ASSIGNED',
                alertData));

        $scope.model.task.assignee = user;
        });
    };


    $scope.revealContent = function(content) {
        $scope.model.activeTab = "content";
        $scope.model.selectedContent = content;
    };

    $scope.hasDetails = function() {

        if ($scope.model.loading == true
            || ($scope.model.involvementSummary === null || $scope.model.involvementSummary === undefined || $scope.model.involvementSummary.loading === true)
            || ($scope.model.contentSummary === null || $scope.model.contentSummary === undefined || $scope.model.contentSummary.loading === true)
            || ($scope.model.commentSummary === null || $scope.model.commentSummary === undefined || $scope.model.commentSummary.loading === true) ) {
            return false;
        }

        if ($scope.model.task !== null && $scope.model.task !== undefined) {

            // Returning true by default, or the screen will flicker until all the data (people/comments/content) have been fetched
            var hasPeople = false;
            var hasContent = false;
            var hasComments = false;

            // Involved people
            if ($scope.model.task.involvedPeople !== null
                && $scope.model.task.involvedPeople !== undefined
                && $scope.model.task.involvedPeople.length > 0) {
                hasPeople = true;
            }

            // Content
            if ($scope.model.content !== null
                && $scope.model.content !== undefined
                && $scope.model.content.data.length > 0) {
                hasContent = true;
            }

            // Comments
            if ($scope.model.comments !== null
                && $scope.model.comments !==undefined
                && $scope.model.comments.data.length > 0) {
                hasComments = true;
            }

            return hasPeople || hasContent || hasComments;

        }
        return false;
    };

        $scope.uploadInProgress = function(state) {
            if (state !== 'undefined') {
                $scope.model.uploadInProgress = state;
            }
        };
 }]);


angular.module('activitiApp')
    .controller('CreateTaskController', ['$rootScope', '$scope', '$translate', '$http', '$location', 'TaskService',
        function ($rootScope, $scope, $translate, $http, $location, TaskService) {

            $scope.createTask = function() {
                TaskService.createTask($scope.newTask).then(function(createdTask) {
                    $scope.resetModel();
                    $rootScope.addAlertPromise($translate('TASK.ALERT.CREATED', createdTask));
                });
            };

            $scope.resetModel = function() {
                $scope.newTask = {
                    name: '',
                    description: ''
                };
            };

            $scope.resetModel();

        }
]);

angular.module('activitiApp')
    .controller('ContentDetailsController', ['$rootScope', '$scope', '$translate', '$modal', 'appResourceRoot', 'RelatedContentService',
        function ($rootScope, $scope, $translate, $modal, appResourceRoot, RelatedContentService) {

            $scope.model = {
                selectedContent: $scope.content,
                selectedTask : $scope.task
            };

            // Map simple-type to readable content type name
            var translateKey;
            if($scope.content) {
                translateKey = "CONTENT.SIMPLE-TYPE." + $scope.content.simpleType.toUpperCase();
            } else {
                translateKey = "CONTENT.SIMPLE-TYPE.CONTENT";
            }

            $translate(translateKey).then(function(message) {
                $scope.model.contentType = message;
            });

            $scope.getPdfViewerUrl = function(content) {
                var urlEncoded = encodeURIComponent(content.pdfUrl);

                return appResourceRoot + 'views/templates/viewer.html?file=' + urlEncoded;
            };

            $scope.deleteContent = function(content, task) {
                var modalInstance = _internalCreateModal({
                    template: appResourceRoot + 'views/modal/delete-content.html',
                    show: true
                }, $modal, $scope);

                modalInstance.$scope.popup = {
                    content: content,
                    loading: false
                };

                modalInstance.$scope.ok = function() {
                    RelatedContentService.deleteContent(content.id, task && task.id).then(function() {
                        $scope.$emit('content-deleted', {content: content});
                        $scope.model.selectedContent = null;
                        $scope.model.selectedTask = null;
                    });
                };
            };
        }
]);
/**
 * 改写
 */
angular.module('activitiApp')
.controller('AnchoringTaskController', ['$filter','$interval' , '$rootScope', '$scope', '$translate', '$http','$location', '$routeParams', 'appResourceRoot', 'CommentService', 'TaskService', 'FormService', 'RelatedContentService', '$timeout', '$modal', '$popover',
      function ($filter, $interval , $rootScope, $scope, $translate, $http, $location, $routeParams, appResourceRoot, CommentService, TaskService, FormService, RelatedContentService, $timeout, $modal, $popover) { 
	 
	/******************************************************************************
	 * 区分延误dx和 延期dy
	 */
	
	
	$scope.dx = 0; // /h 
	$scope.dy = $scope.dx; //默认dx == dy
	$scope.ZoomInVal= 1000; // 1000ms ---> 10ms 时间压缩100倍
	$scope.countTime = {};
	$scope.curTime = '1970-01-01';
    $scope.estart = {};  //存最初的[T1 , T2] , 该状态下的常量
	$scope.eend = {};
	$scope.eend_v ={};//存修改后的值 ，
	$scope.compAnchTime = {};
	
	$scope.pvars = {};  
	
	if($scope.model.task.name == 'Anchoring'){
   	 	console.log("Enter into Anchoring'");
   	 	$http.get(ACTIVITI.CONFIG.contextRoot+'/api/zbq/variables/'+$scope.model.task.processInstanceId)
   	 		.success(function(data){
   	 			$scope.pvars = data;
   	 			$scope.pIdxs = $scope.createPidxs($scope.pvars);
   	 	
   	 		    $scope.curTime = $scope.pvars[$scope.pIdxs['PrePort']].value.estart;//默认靠港离港时间
   	 		    $scope.estart = $scope.pvars[$scope.pIdxs['PrePort']].value.estart;
   	 		    $scope.compAnchTime = $scope.pvars[$scope.pIdxs['PrePort']].value.estart;
   	 		    $scope.eend = $scope.pvars[$scope.pIdxs['PrePort']].value.eend;
   	 		    $scope.countTime = $interval(function(){ //如果anchoring 滞留
   	   	 		//计算实际时间， 即非压缩时间
   	 		    	var curMs =$scope.dateStr2ms($scope.curTime)
   	 		    	var nextMs = curMs+1000*$scope.ZoomInVal; // 此处放大1000倍， 相当与实际时间过了16.67分钟
   	 		        $scope.curTime= $scope.ms2dateStr(nextMs);
   	 		    	if($scope.dx != 0){ //如果需要延期，anchoring状态滞留dx , 否则将这段时间当作docking时间的一部分
   	 		    		var compMs = $scope.dateStr2ms($scope.compAnchTime);
   	 		    		if(compMs > curMs && compMs <= nextMs){
   	 		    			$scope.completeTask();
   	 		    			$interval.cancel($scope.countTime);
   	 		    			console.log("延期结束， anchoring 结束 ， $scope.curTime : " , $scope.curTime);
   	 		    		}
   	 		    	}
   	 			} ,1000);
   	 		})
     }
	
	$scope.setDxy = function(){ //需要延期靠港
		
		if($scope.dx != 0){//有延误或者延期
				if($scope.dx <= $scope.dy){
					$scope.compAnchTime = $scope.ms2dateStr($scope.dateStr2ms($scope.estart)+$scope.dx * 60 * 60 * 1000)
					$scope.pvars[$scope.pIdxs['PrePort']].value.estart = $scope.compAnchTime; // 不需要在docking状态使用，可以更新					  
					$scope.eend_v = $scope.ms2dateStr($scope.dateStr2ms($scope.eend)+$scope.dy * 60 * 60 * 1000)
					//$scope.pvars[$scope.pIdxs['PrePort']].value.eend = $scope.eend; //延迟修改默认到达时间T2 , 有可能在doking期间修改			  
				}else{
					alert("延误时间小于延期时间，无效！")
				}
		}
		//计算新的靠港时间，离港时间
		var d = new Date();
		console.log("出现延误或延期 ： 新[T1 , T2] = " , $scope.estart , $scope.eend);
		//更新当前及其后所有有效港口停靠时间段
		 for(var i in  $scope.pvars[$scope.pIdxs['TargLocList']].value ){
		        var x = $scope.pvars[$scope.pIdxs['TargLocList']].value[i];
		        //如果是当前港口及其后面港口， 时间按d_timeStamp顺移
		        if(x.pname == $scope.pvars[$scope.pIdxs['PrePort']].value.pname){
		        	$scope.pvars[$scope.pIdxs['TargLocList']].value[i].estart = $scope.compAnchTime ; 
		        	$scope.pvars[$scope.pIdxs['TargLocList']].value[i].eend = $scope.eend_v;
		        }
		        if(x.State == "AfterAD"){
		          	var s_ms = $scope.dateStr2ms($scope.pvars[$scope.pIdxs['TargLocList']].value[i].estart)+$scope.dy * 60 * 60 * 1000;
		            var e_ms = $scope.dateStr2ms($scope.pvars[$scope.pIdxs['TargLocList']].value[i].eend)+ $scope.dy * 60 * 60 * 1000;
		            $scope.pvars[$scope.pIdxs['TargLocList']].value[i].estart = $scope.ms2dateStr(s_ms);
		            $scope.pvars[$scope.pIdxs['TargLocList']].value[i].eend =  $scope.ms2dateStr(e_ms);
		       }
		   }
		          
		  $scope.sendMsgToVWC();
	  }  
	  $scope.sendMsgToVWC = function(){  
		//通知VWC 
		  $http.put(ACTIVITI.CONFIG.contextRoot+'/api/zbq/variables/'+$scope.model.task.processInstanceId+'/complete' ,$scope.pvars)
			.success(function(data){
				$scope.pvars = data;
        		$scope.pidxs = $scope.createPidxs($scope.pvars);
        		if($scope.pvars[$scope.pIdxs['W_pid']] != undefined){
        			var data2VWC = {
    		    			 'msgType' : "msg_ADTimeChange" ,
    		    			 'V_pid'  : $scope.model.task.processInstanceId ,
    		    			 'W_pid' : $scope.pvars[$scope.pIdxs['W_pid']],
    		    			 'V_TargLocList' : $scope.pvars[$scope.pIdxs['TargLocList']]
    		    	};
    		    	$http.post(activityBasepath + "/coord/message/Msg_StartVWC", data2VWC)
    	             .success(function (data) {
    	                 console.log("Send Message to VMC!", data);
    	             })
        		}else{
        			console.log("Weagon 实例未启动 ， VWC联系无法建立");
        		}
			})	
	  }
}]);
angular.module('activitiApp')
.controller('DockingTaskController', ['$filter','$interval' , '$rootScope', '$scope', '$translate', '$http','$location', '$routeParams', 'appResourceRoot', 'CommentService', 'TaskService', 'FormService', 'RelatedContentService', '$timeout', '$modal', '$popover',
      function ($filter, $interval , $rootScope, $scope, $translate, $http, $location, $routeParams, appResourceRoot, CommentService, TaskService, FormService, RelatedContentService, $timeout, $modal, $popover) { 
	 
	/***************************Anchoring***************************************************
	 * 区分延误dx和 延期dy
	 */
	$scope.dy = 0; 
	$scope.ZoomInVal= 1000; // 1000ms ---> 10ms 时间压缩100倍
	$scope.countTime = {};
	$scope.curTime = '1970-01-01'
    $scope.estart = {};
	$scope.eend = {};
	$scope.compDockTime = {};
	
	$scope.pvars = {};  
	
	if($scope.model.task.name == 'Docking'){
   	 	console.log("Enter into DockingTaskCtrl");
   	 	$http.get(ACTIVITI.CONFIG.contextRoot+'/api/zbq/variables/'+$scope.model.task.processInstanceId)
   	 		.success(function(data){
   	 			$scope.pvars = data;
   	 			$scope.pIdxs = $scope.createPidxs($scope.pvars);
   	 		    //默认靠港离港时间 ， 如果anchoring 有延期 ， 此时estart = t1 + dx , 如果没有,就是t1 , 此处忽略提交表单时间
   	 		    //需要及时提交表单
   	 		    $scope.curTime = $scope.pvars[$scope.pIdxs['PrePort']].value.estart;
   	 		    $scope.estart = $scope.pvars[$scope.pIdxs['PrePort']].value.estart;
   	 		    for(var i in  $scope.pvars[$scope.pIdxs['TargLocList']].value ){//在TargLocList 中获取该港口最新离港时间
   	 		    	var x = $scope.pvars[$scope.pIdxs['TargLocList']].value[i];
   	 		    	if(x.pname == $scope.pvars[$scope.pIdxs['PrePort']].value.pname){
   	 		    		$scope.compDockTime = x.eend ;
   	 		    		console.log("x.pname " , x.pname  , $scope.compDockTime ,x.eend ,$scope.pvars[$scope.pIdxs['TargLocList']].value[i])
   	 		    	}
   	 		    }
   	 		    $scope.eend = $scope.pvars[$scope.pIdxs['PrePort']].value.eend; //初次估计离港时间
   	 		    
   	 		 $scope.countTime = $interval(function(){ //如果anchoring 滞留
    	   	 		//计算实际时间， 即非压缩时间
    	 		    	var curMs =$scope.dateStr2ms($scope.curTime)
    	 		    	var nextMs = curMs+1000*$scope.ZoomInVal; // 此处放大1000倍， 相当与实际时间过了16.67分钟
    	 		        $scope.curTime= $scope.ms2dateStr(nextMs);
    	 		    	var compMs = $scope.dateStr2ms($scope.compDockTime);
    	 		    	console.log($scope.curTime ,$scope.compDockTime);
    	 		    	if(compMs > curMs && compMs <= nextMs){
    	 		    		$scope.completeTask();
    	 		    		$interval.cancel($scope.countTime);
    	 		    		console.log("$scope.curTime : " , $scope.curTime);
    	 		       }
    	 			} ,1000);
   	 		})
     }
	$scope.setDy= function(){ //需要延期靠港
		
		if($scope.dy != 0){//有延期
				var cur_ms = $scope.dateStr2ms($scope.eend)+$scope.dy;
				var pre_ms = $scope.dateStr2ms($scope.compDockTime);
				if(cur_ms > pre_ms){
					console.log("延期离港");
				}else{
					alert("相对于上次估计时间来说，提前离港！");
				}
				  //计算新的离港时间
				  $scope.compDockTime = $scope.ms2dateStr(cur_ms);
				  console.log("出现延误或延期 ： 新[T1 , T2] = " , $scope.estart ,  $scope.compDockTime);
				//更新当前及其后所有有效港口停靠时间段
				  for(var i in  $scope.pvars[$scope.pIdxs['TargLocList']].value ){
				        var x = $scope.pvars[$scope.pIdxs['TargLocList']].value[i];
				        //如果是当前港口及其后面港口， 时间按d_timeStamp顺移
				        if(x.pname == $scope.pvars[$scope.pIdxs['PrePort']].value.pname){
				        	$scope.pvars[$scope.pIdxs['TargLocList']].value.eend = $scope.compDockTime;
				        }
				        if(x.State == "AfterAD"){
				          	var s_ms = Date.parse($scope.pvars[$scope.pIdxs['TargLocList']].value[i].estart)+ cur_ms - pre_ms;
				            var e_ms = Date.parse($scope.pvars[$scope.pIdxs['TargLocList']].value[i].eend)+ cur_ms - pre_ms;
				            $scope.pvars[$scope.pIdxs['TargLocList']].value[i].estart = $scope.ms2dateStr(s_ms);
				            $scope.pvars[$scope.pIdxs['TargLocList']].value[i].eend = $scope.ms2dateStr(e_ms);
				          }
				   }
				          
				  $scope.sendMsgToVWC();
		}else{
			alter("暂不允许负值！")
		}

	  }  
	  $scope.sendMsgToVWC = function(){  
		//通知VWC 
		  $http.put(ACTIVITI.CONFIG.contextRoot+'/api/zbq/variables/'+$scope.model.task.processInstanceId+'/complete' ,$scope.pvars)
			.success(function(data){
				$scope.pvars = data;
        		$scope.pidxs = $scope.createPidxs($scope.pvars);
        		if($scope.pvars[$scope.pIdxs['W_pid']] != undefined){
        			var data2VWC = {
    		    			 'msgType' : "msg_UpdateDest" ,
    		    			 'V_pid'  : $scope.model.task.processInstanceId ,
    		    			 'W_pid' : $scope.pvars[$scope.pIdxs['W_pid']],
    		    			 'V_TargLocList' : $scope.pvars[$scope.pIdxs['TargLocList']]
    		    	};
    		    	$http.post(activityBasepath + "/coord/message/Msg_StartVWC", data2VWC)
    	             .success(function (data) {
    	                 console.log("Send Message to VMC!", data);
    	             })
        		}else{
        			console.log("Weagon 实例未启动 ， VWC联系无法建立");
        		}
			})	
	  }
}]);
angular.module('activitiApp')
.controller('VoyaTaskController', ['$filter','$interval' , '$rootScope', '$scope', '$translate', '$http','$location', '$routeParams', 'appResourceRoot', 'CommentService', 'TaskService', 'FormService', 'RelatedContentService', '$timeout', '$modal', '$popover',
      function ($filter, $interval , $rootScope, $scope, $translate, $http, $location, $routeParams, appResourceRoot, CommentService, TaskService, FormService, RelatedContentService, $timeout, $modal, $popover) {

    $scope.pvars = {};
    $scope.pidxs = {};
    $scope.dispTime = null;
        
    $scope.voyageTaskTimer = $interval(function(){
    	console.log("Task Name : " + $scope.model.task.name);
    	if($scope.model.task.name != 'Voyaging'){
     	 	return;
        }
    	var varUrl = ACTIVITI.CONFIG.contextRoot+'/api/zbq/variables/'+$scope.model.task.processInstanceId;
		
//    	console.log("Voya voyaging")
    	$http.get(varUrl)
    	.success(function(data){
    		$scope.pvars = data;
    		$scope.pidxs = $scope.createPidxs($scope.pvars);
//    		console.log($scope.pvars[$scope.pidxs['NowLoc']].value.lname , $scope.pvars[$scope.pidxs['PrePort']].value)
    		if($scope.pvars[$scope.pidxs['NowLoc']].value.lname == null && $scope.pvars[$scope.pidxs['PrePort']].value.pname != undefined &&
    				$scope.pvars[$scope.pidxs['NextPort']].value.pname != undefined){
    			$scope.pvars[$scope.pidxs['NowLoc']].value.lname =$scope.pvars[$scope.pidxs['PrePort']].value.pname+"-->"+$scope.pvars[$scope.pidxs['NextPort']].value.pname;
    		}
//    		console.log("Now State : "+$scope.pvars[$scope.pidxs['State']].value);
    		//检查前台传来的状态，看该段区间是否航行完成
//    		console.log("State : " + $scope.pvars[$scope.pidxs['State']].value);
    		if($scope.pvars[$scope.pidxs['State']].value == 'arrival'){
    				$scope.pvars[$scope.pidxs['StartTime']].value = $scope.dispTime;
    				
    				$http.put(varUrl , $scope.pvars)
        			.success(function(res){
        				console.log("Voyaging Task 结束， 将StartTime上传")
        				$scope.completeTask();  //call parent controller function; 交给VW_F 完成
        				$interval.cancel($scope.voyageTaskTimer);
        			});;
    				
    		}
    		//计算展示时间
//    		console.log("StartTime : "+$scope.pvars[$scope.pidxs['StartTime']].value);
//    		console.log("timeStamp : "+$scope.pvars[$scope.pidxs['NowLoc']].value.timeStamp/60000 + " min");
    		var ms = Date.parse($scope.pvars[$scope.pidxs['StartTime']].value) + $scope.pvars[$scope.pidxs['NowLoc']].value.timeStamp;
    		var d = new Date();
    		d.setTime(ms);
    		if(d != 'Invalid Date'){
    			$scope.dispTime = $filter('date')(d, "yyyy-MM-dd HH:mm:ss"); 
    		}
    	})
    }, 1000);
   
}]);

angular.module('activitiApp')
.controller('ApplyTaskController', ['$filter','$interval' , '$rootScope', '$scope', '$translate', '$http','$location', '$routeParams', 'appResourceRoot', 'CommentService', 'TaskService', 'FormService', 'RelatedContentService', '$timeout', '$modal', '$popover',
      function ($filter, $interval , $rootScope, $scope, $translate, $http, $location, $routeParams, appResourceRoot, CommentService, TaskService, FormService, RelatedContentService, $timeout, $modal, $popover) {
	
	if($scope.model.task['name'] == 'Apply for spare parts'){
	 	console.log("Enter into VoyaTaskCtrl");
	 }
    $scope.pvars = {};
    $scope.pidxs = {};
    $scope.dispTime = "1970-01-01 00:00:00";
    
    //一直读取船的信息，直到船任务人工完成
    $scope.ApplyTimer = $interval(function(){
    	if($scope.model.task['name'] == 'Apply for spare parts'){
    		//var varUrl = ACTIVITI.CONFIG.contextRoot+'/api/runtime/process-instances/'+$scope.model.task.processInstanceId+'/variables';
    		var varUrl = ACTIVITI.CONFIG.contextRoot+'/api/zbq/variables/'+$scope.model.task.processInstanceId;
    		
    		
          	$http.get(varUrl)
        	.success(function(data){
        		$scope.pvars = data;
        		$scope.pidxs = $scope.createPidxs($scope.pvars);
        		//console.log("Apply voyaging" , $scope.pvars[$scope.pidxs['NowLoc']].value.x_coor , $scope.pvars[$scope.pidxs['NowLoc']].value.y_coor);
        		if($scope.pvars[$scope.pidxs['NowLoc']].value.lname == null && $scope.pvars[$scope.pidxs['PrePort']]['value']['lname'] != undefined &&
        				$scope.pvars[$scope.pidxs['NextPort']]['value']['lname'] != undefined){
        			$scope.pvars[$scope.pidxs['NowLoc']]['value']['lname'] =$scope.pvars[$scope.pidxs['PrePort']]['value']['lname']+"-->"+$scope.pvars[$scope.pidxs['NextPort']]['value']['Name'];
        		}
//        		console.log("Now State : "+$scope.pvars[$scope.pidxs['State']].value);
        		//当前任务是Applying 时 ，如果遇到达下一港口消息，（这里默认在第个区间就会提出申请，不会出现这种情况）
//        		if($scope.pvars[$scope.pidxs['State']].value == 'arrival'){
//        			//******should do nothing
//        			$scope.pvars[$scope.pidxs['StartTime']].value = $scope.dispTime;
//        			$scope.pvars[$scope.pidxs['State']].value = 'voyaging';
//    				$http.put(varUrl , $scope.pvars)
//        			.success(function(res){
//        				console.log("过站了...")
////        				console.log("Voyaging Task 结束， 将StartTime上传")
////        				$scope.completeTask();  //call parent controller function;
//        			});
//        		}
        		//计算展示时间
//        		console.log("StartTime : "+$scope.pvars[$scope.pidxs['StartTime']].value);
//        		console.log("timeStamp : "+$scope.pvars[$scope.pidxs['NowLoc']].value.timeStamp /60000 + " min");
        		var ms = Date.parse($scope.pvars[$scope.pidxs['StartTime']].value) + $scope.pvars[$scope.pidxs['NowLoc']].value.timeStamp;
        		var d = new Date();
        		d.setTime(ms);
        		if(d != 'Invalid Date'){
        			$scope.dispTime = $filter('date')(d, "yyyy-MM-dd HH:mm:ss"); 
        		}
        	})
    	}

    }, 1000);
    $scope.$on('complete-apply' , function(event , data){
    	console.log(data + ""+data.taskId+" --> cancel  $scope.ApplyTimer");
    	console.log(data.taskId == $scope.model.task.id);
    	if(data.taskId == $scope.model.task.id){
    		$interval.cancel($scope.ApplyTimer);
    	}
    
    })
}]);

/***
 * Weagon controllers
 */
angular.module('activitiApp')
.controller('PlanPathController', ['$filter','$interval' , '$rootScope', '$scope', '$translate', '$http','$location', '$routeParams', 'appResourceRoot', 'CommentService', 'TaskService', 'FormService', 'RelatedContentService', '$timeout', '$modal', '$popover',
      function ($filter, $interval , $rootScope, $scope, $translate, $http, $location, $routeParams, appResourceRoot, CommentService, TaskService, FormService, RelatedContentService, $timeout, $modal, $popover) {
	 $scope.$on('RW_PLAN' , function(event , data){
		 //TODO some work when receive 'RW_PLAN'
		console.log(" PlanPathController receice RW_PLAN " , data)
		console.log("发消息给coordinator");
		//给coordinator 
		//自动完成任务
		$scope.completeTask();
	 });
}]);

angular.module('activitiApp')
.controller('RunController', ['$filter','$interval' , '$rootScope', '$scope', '$translate', '$http','$location', '$routeParams', 'appResourceRoot', 'CommentService', 'TaskService', 'FormService', 'RelatedContentService', '$timeout', '$modal', '$popover',
      function ($filter, $interval , $rootScope, $scope, $translate, $http, $location, $routeParams, appResourceRoot, CommentService, TaskService, FormService, RelatedContentService, $timeout, $modal, $popover) {
	
	$scope.w = {};
	$scope.runTimer = $interval(function(){
		if($scope.model.task.name == 'Running'){
			$http.get(ACTIVITI.CONFIG.contextRoot+'/api/runtime/process-instances/'+$scope.model.task.processInstanceId+'/variables/W_Info')
			.success(function(data){
				$scope.w = data;
        		$scope.pidxs = $scope.createPidxs($scope.w);
				
			})
		 }	
	} , 1000)
}]);


