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
     * *******************************************************************
     * custom variables
     * ********************************************************************
     */
      $scope.ZoomInVal= 500; // 1000ms ---> 10ms 时间压缩100倍$scope.ZoomInVal= 1000; // 1000ms ---> 10ms 时间压缩100倍
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
      

    $scope.model.involvementSummary = {
        loading: false
    };

    $scope.model.contentSummary = {
        loading: false
    };

    $scope.model.commentSummary = {
        loading: false
    };


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
.controller('ADTaskController', ['$filter','$interval' , '$rootScope', '$scope', '$translate', '$http','$location', '$routeParams', 'appResourceRoot', 'CommentService', 'TaskService', 'FormService', 'RelatedContentService', '$timeout', '$modal', '$popover',
      function ($filter, $interval , $rootScope, $scope, $translate, $http, $location, $routeParams, appResourceRoot, CommentService, TaskService, FormService, RelatedContentService, $timeout, $modal, $popover) { 
	 
	/******************************************************************************
	 * 区分延误dx和 延期dy
	 */
	
	
	$scope.dx = 0; // /h 
	$scope.dy = $scope.dx; //默认dx == dy
	$scope.countTime = {};
	$scope.curTime = '';
    $scope.estart = null;  //存最初的[T1 , T2] , 该状态下的常量
	$scope.eend = null;
	$scope.newST = null;
	$scope.newED = null;//存修改后的值 ，
	$scope.vstate = "docking"; // vstate : anchring / docking , 默认是docking
	$scope.leftTime = null;
	$scope.pname = null;
	$scope.pidxs = {}
	$scope.oldPortList = {};
	$scope.newPortList = {};
	$scope.pvars = {};  
	
	if($scope.model.task.name == 'Anchoring-Docking'){
   	 	console.log("Enter into Anchoring-Docking'");
   	 	$http.get(ACTIVITI.CONFIG.contextRoot+'/api/zbq/variables/'+$scope.model.task.processInstanceId)
   	 		.success(function(data){
   	 			$scope.pvars = data;
   	 			$scope.pidxs = $scope.createPidxs($scope.pvars);
   	 			var curDate = new Date();
	 			var stMs = $scope.dateStr2ms($scope.pvars[$scope.pidxs['StartTime']].value);
	 			$scope.oldPortList = angular.copy($scope.pvars[$scope.pidxs['TargLocList']]);
	 			$scope.pname = $scope.pvars[$scope.pidxs['PrePort']].value.pname;
	 			console.log($scope.pname ,$scope.pvars);
	 			console.log("OldPortList",$scope.oldPortList);
	 		    $scope.curTime=  $scope.ms2dateStr(stMs + (curDate.getTime() - stMs)*$scope.ZoomInVal);
	 		    $scope.model.completeButtonDisabled = true; //设置自动完成
	 		   
   	 		    $scope.estart = $scope.pvars[$scope.pidxs['PrePort']].value.estart;//设置最初的靠港时间
   	 		    $scope.eend = $scope.pvars[$scope.pidxs['PrePort']].value.eend;//设置最初的离港时间
   	 		    
   	 		    $scope.newED =  $scope.eend;
   	 		    $scope.newST =   $scope.estart;
   	 		    $scope.countTime = $interval(function(){ //如果anchoring 滞留
   	   	 		//计算实际时间， 即非压缩时间
   	 		    	var curMs =$scope.dateStr2ms($scope.curTime)
   	 		    	var st = ($scope.dateStr2ms($scope.newED) - curMs)/(1000*60*60);
   	 		        $scope.leftTime = st.toFixed(2);
   	 		    	var nextMs = curMs+1000*$scope.ZoomInVal; // 此处放大500倍， 相当与实际时间过了8.335分钟
   	 		        $scope.curTime= $scope.ms2dateStr(nextMs); 
//   		 		    console.log(" $scope.curTime : " , $scope.curTime);
   	 		        if($scope.vstate == 'anchoring' && $scope.newST != null){ //出现延误
   	 		        	if($scope.dateStr2ms($scope.newST) > curMs && $scope.dateStr2ms($scope.newST) <= nextMs){
   	 		        		console.log("anchoring 结束 ， $scope.curTime : " , $scope.curTime);
   	 		        	    $scope.vstate = "docking";
   	 		        	}
   	 		        } 
   	 		    	var compMs = $scope.dateStr2ms($scope.newED);
   	 		    	if(compMs > curMs && compMs <= nextMs){
   	 		    		$scope.dx = 0; // /h 
	 		    		$scope.dy = $scope.dx; //默认dx == dy	
   	 		    		$scope.completeTask();
   	 		    		$interval.cancel($scope.countTime);
   	 		    		console.log("docking结束 ， $scope.curTime : " , $scope.curTime);
   	 		    	}
   	 			} ,1000);
   	 		})
     }
	$scope.disableSetDefaultState = false;
	$scope.setDefaultState = function(){
		$scope.vstate = "anchoring";
		$scope.disableSetDefaultState = true;
	}
	$scope.setDxy = function(){ //需要延期靠港
		console.log("$scope.estart" , $scope.estart,"$scope.eend", $scope.eend);
		var adxy = 0;//后续港口后移时间
		var validFlag = false;
		if($scope.vstate == "anchoring"){ // 如果是anchring状态 , 能延误也能延期
			var t_newSt = $scope.ms2dateStr($scope.dateStr2ms($scope.estart)+$scope.dx*60*60*1000); //新的靠港时间
			var t_newEd = $scope.ms2dateStr($scope.dateStr2ms($scope.eend)+$scope.dx*60*60*1000+$scope.dy*60*60*1000); // 新的离港时间
			console.log("t_newSt : " , t_newSt, "t_newEd : " , t_newEd);
			console.log("$scope.estart : " , $scope.estart, "$scope.eend : " , $scope.eend);
			console.log("$scope.dx : " , $scope.dx, "$scope.dy: " , $scope.dy);
			console.log("$scope.newED : " , $scope.newED);
			if($scope.dateStr2ms(t_newEd) > $scope.dateStr2ms(t_newSt)){ //首先保证新的离港时间大于新的靠港时间
				if($scope.dateStr2ms(t_newSt) > $scope.dateStr2ms($scope.curTime)){//新的靠港时间应该大于当前时间
				  if($scope.dateStr2ms(t_newEd) < $scope.dateStr2ms($scope.newED)){
					  console.log("相对于上一次离港时间提前");
				  }else{
					  console.log("相对于上一次离港时间延期");
				  }
				  if(parseInt($scope.dx)+parseInt($scope.dy) < 0 ){
					  adxy = 0;
				  }else{
					  console.log("adxy0 : " , parseInt($scope.dx)+parseInt($scope.dy));
					  adxy = (parseInt($scope.dx)+parseInt($scope.dy))*60*60*1000;
					 
				  }
				  console.log("adxy : " , adxy);
				  $scope.newST = t_newSt;
				  $scope.newED = t_newEd;
				  validFlag = true;
				}else{
					//alert("设置延误时间不合理， 竟然新的靠港时间比当前还早！"+t_newSt +":"+$scope.curTime);
					console.log("$scope.curTime" , $scope.curTime);
				}
			}else{
				alert("无效， 新的离港时间应该晚于靠港时间！"+t_newSt+":"+t_newEd);
			}
		}else{// 如果是docking状态 ， 只考虑延期
			var t_newEd =   $scope.ms2dateStr($scope.dateStr2ms($scope.eend)+$scope.dy*60*60*1000); // 新的离港时间
			if($scope.dateStr2ms(t_newEd) > $scope.dateStr2ms($scope.curTime)){//新的靠港时间应该大于当前时间
				if($scope.dateStr2ms(t_newEd) < $scope.dateStr2ms($scope.newED)){
				  console.log("相对于上一次离港时间提前");			 
				}else{
				  console.log("相对于上一次离港时间延期");
				}
				 adxy = $scope.dy*60*60*1000;
				console.log("t_newEd =" , t_newEd , "$scope.eend" , $scope.eend);
				$scope.newED = t_newEd;
				validFlag = true;
				console.log("$scope.newST: " , $scope.newST, "$scope.newED" , $scope.newED );
			}else{
				alert("设置延期时间不合理， 竟然新的靠港时间比当前还早！");
			}
		}
		if(validFlag == true){
			//更新当前及其后所有有效港口停靠时间段
 			$scope.newPortList = angular.copy($scope.oldPortList);
			for(var i in  $scope.oldPortList.value ){
			        var x = $scope.oldPortList.value[i];
			        //如果是当前港口及其后面港口， 时间按d_timeStamp顺移
			        if(x.state == 'InAD'){
			        	$scope.newPortList.value[i].estart = $scope.newST;
			        	$scope.newPortList.value[i].eend =  $scope.newED;
			        	console.log("InAD , $scope.newED" , $scope.newED , $scope.newPortList.value[i]);
			        	$scope.pvars[$scope.pidxs['PrePort']].value = angular.copy($scope.newPortList.value[i]);
			        }
			        if(x.state == 'AfterAD'){
			        	 
					      var s_ms = Date.parse($scope.oldPortList.value[i].estart)+adxy;
					      var e_ms = Date.parse($scope.oldPortList.value[i].eend)+adxy;
					      $scope.newPortList.value[i].estart = $scope.ms2dateStr(s_ms);
					      $scope.newPortList.value[i].eend = $scope.ms2dateStr(e_ms);
					      console.log("AfterAD : " , $scope.newPortList.value[i] , x);
			        }
			        
			        if(x.pname == $scope.pvars[$scope.pidxs['NextPort']].value.pname){
			        	$scope.pvars[$scope.pidxs['NextPort']].value.estart = $scope.newPortList.value[i].estart;
			        	$scope.pvars[$scope.pidxs['NextPort']].value.eend = $scope.newPortList.value[i].eend;
			        }
			 }
			console.log("$scope.newPortList : " , $scope.newPortList);   
			$scope.sendMsgToVWC();
		}
	  }  
	 $scope.sendMsgToVWC = function(){  
			//通知VWC 
			 $http.get(ACTIVITI.CONFIG.contextRoot+'/api/zbq/variables/'+$scope.model.task.processInstanceId)
			 .success(function(pdata){
				 var pvs = pdata; 
				 var pds = $scope.createPidxs(pvs);
				 console.log("old pvs : " , angular.copy(pvs));
				 //修改新值
				 pvs[pds['TargLocList']] = angular.copy($scope.newPortList);
				 pvs[pds['PrePort']] =  angular.copy($scope.pvars[$scope.pidxs['PrePort']]);
				 pvs[pds['NextPort']] = angular.copy($scope.pvars[$scope.pidxs['NextPort']]);
				 console.log("new pvs : " , pvs);
				 $http.put(ACTIVITI.CONFIG.contextRoot+'/api/zbq/variables/'+$scope.model.task.processInstanceId+'/complete' ,pvs)
					.success(function(data){
						$scope.pvars = data;
		        		$scope.pidxs = $scope.createPidxs($scope.pvars);
		        		console.log("Docking sendMsgVWC ",$scope.pvars[$scope.pidxs['W_pid']]);
		        		if($scope.pvars[$scope.pidxs['W_pid']]!= null){
		        			var tempdx='';
		        			var tempdy='';
		        			if($scope.dx>0){
		        				tempdx="推迟"+$scope.dx+"h进港,";
		        			}else if($scope.dx<0){
		        				tempdx="提前"+(-$scope.dx)+"h进港,";
		        			}
		        			if($scope.dy>0){
		        				tempdy="推迟"+$scope.dy+"h离港";
		        			}else if($scope.dy<0){
		        				tempdy="提前"+(-$scope.dy)+"h离港";
		        			}
		        			var data2VWC = {
		    		    			 'msgType' : "msg_UpdateDest" ,
		    		    			 'V_pid'  : $scope.model.task.processInstanceId ,
		    		    			 'W_pid' : $scope.pvars[$scope.pidxs['W_pid']].value ,
		    		    			 'reason' : tempdx+tempdy
		    		    	};
		      
		    		    	$http.post(ACTIVITI.CONFIG.contextRoot + "/api/coord/messages/Msg_StartVWC", data2VWC)
		    	             .success(function (data) {
		    	                 console.log("Send Message to VMC!", data);
		    	             })
		        		}else{
		        			console.log("Weagon 实例未启动 ， VWC联系无法建立");
		        		}
					});
			 });
		  }
}]);
//angular.module('activitiApp')
//.controller('DockingTaskController', ['$filter','$interval' , '$rootScope', '$scope', '$translate', '$http','$location', '$routeParams', 'appResourceRoot', 'CommentService', 'TaskService', 'FormService', 'RelatedContentService', '$timeout', '$modal', '$popover',
//      function ($filter, $interval , $rootScope, $scope, $translate, $http, $location, $routeParams, appResourceRoot, CommentService, TaskService, FormService, RelatedContentService, $timeout, $modal, $popover) { 
//	 
//	/***************************Anchoring***************************************************
//	 * 区分延误dx和 延期dy
//	 */
//	$scope.dy = 0; 
//	$scope.countTime = {};
//	$scope.curTime = '1970-01-01 00:00:00'
//    $scope.estart = {};
//	$scope.eend = {};
//	$scope.compDockTime = {};
//	
//	$scope.pvars = {};  
//	
//	if($scope.model.task.name == 'Docking'){
//   	 	console.log("Enter into Docking");
//   	    $scope.model.completeButtonDisabled = true;
//   	 	$http.get(ACTIVITI.CONFIG.contextRoot+'/api/zbq/variables/'+$scope.model.task.processInstanceId)
//   	 		.success(function(data){
//   	 			$scope.pvars = data;
//   	 			$scope.pidxs = $scope.createPidxs($scope.pvars);
//   	 		    //默认靠港离港时间 ， 如果anchoring 有延期 ， 此时estart = t1 + dx , 如果没有,就是t1 , 此处忽略提交表单时间
//   	 		    //需要及时提交表单
//   	 			var curDate = new Date();
//   	 			var stMs = $scope.dateStr2ms($scope.pvars[$scope.pidxs['StartTime']].value);
//   	 		    $scope.curTime =  $scope.ms2dateStr(stMs + (curDate.getTime() - stMs)*$scope.ZoomInVal);
//   	 		    console.log("stMs: " ,stMs , "curTime " , $scope.curTime , "StartTime " ,  $scope.pvars[$scope.pidxs['StartTime']].value , "pvars " ,$scope.pvars);
//   	 		    $scope.estart = $scope.pvars[$scope.pidxs['PrePort']].value.estart;
//   	 		   
//   	 		    $scope.eend = $scope.pvars[$scope.pidxs['PrePort']].value.eend;
//   	 		    for(var i in  $scope.pvars[$scope.pidxs['TargLocList']].value){//在TargLocList 中获取该港口最新离港时间
//   	 		    	var x = $scope.pvars[$scope.pidxs['TargLocList']].value[i];
//   	 		    	if(x.pname == $scope.pvars[$scope.pidxs['PrePort']].value.pname){
//   	 		    		$scope.compDockTime = x.eend;
//   	 		    	}
//   	 		    }
//   	 		   //初次估计离港时间
//   	 		 $scope.countTime = $interval(function(){ //如果anchoring 滞留
//    	   	 		//计算实际时间， 即非压缩时间
//    	 		    	var curMs =$scope.dateStr2ms($scope.curTime)
//    	 		    	var nextMs = curMs+1000*$scope.ZoomInVal; // 此处放大1000倍， 相当与实际时间过了16.67分钟
//    	 		        $scope.curTime = $scope.ms2dateStr(nextMs);
//    	 		    	var compMs = $scope.dateStr2ms($scope.compDockTime);
//    	 		    	if(compMs > curMs && compMs <= nextMs){
//    	 		    		$scope.completeTask();
//    	 		    		$interval.cancel($scope.countTime);
//    	 		       }
//    	 			} ,1000);
//   	 		})
//     }
//	$scope.setDy= function(){ //需要延期靠港
//		
//		if($scope.dy != 0){//有延期
//		        console.log("$scope.eend : " , $scope.eend);
//				var cur_ms = $scope.dateStr2ms($scope.eend)+$scope.dy*60*60*1000;
//				var pre_ms = $scope.dateStr2ms($scope.compDockTime);
//				var now_ms = $scope.dateStr2ms($scope.curTime);
//				if(now_ms < cur_ms){
//					if(cur_ms > $scope.dateStr2ms($scope.eend)){
//						console.log("延期离港");
//					}else{
//						console.log("相对于默认了离港时间提前！");
//					}
//					 //计算新的离港时间
//					  $scope.compDockTime = $scope.ms2dateStr(cur_ms);
//					  console.log("出现延期 ： 新[T1 , T2] = " , $scope.estart ,  $scope.compDockTime);
//					//更新当前及其后所有有效港口停靠时间段
//					  for(var i in  $scope.pvars[$scope.pidxs['TargLocList']].value ){
//					        var x = $scope.pvars[$scope.pidxs['TargLocList']].value[i];
//					        //如果是当前港口及其后面港口， 时间按d_timeStamp顺移
//					        if(x.pname == $scope.pvars[$scope.pidxs['PrePort']].value.pname){
//					        	$scope.pvars[$scope.pidxs['TargLocList']].value[i].eend = $scope.compDockTime;
//					        	console.log("PrePort state : " , $scope.pvars[$scope.pidxs['PrePort']].value)
//					        }else{
//					        	 if(x.state == "AfterAD"){
//							          	var s_ms = Date.parse($scope.pvars[$scope.pidxs['TargLocList']].value[i].estart)+ cur_ms - pre_ms;
//							            var e_ms = Date.parse($scope.pvars[$scope.pidxs['TargLocList']].value[i].eend)+ cur_ms - pre_ms;
//							            $scope.pvars[$scope.pidxs['TargLocList']].value[i].estart = $scope.ms2dateStr(s_ms);
//							            $scope.pvars[$scope.pidxs['TargLocList']].value[i].eend = $scope.ms2dateStr(e_ms);
//							       }
//					        }
//					       
//					   }
//					   console.log("TargLoc test : " ,  $scope.pvars[$scope.pidxs['TargLocList']])  ;     
//					  $scope.sendMsgToVWC();
//				}else{
//					alert("设置无效 ， 竟然离港时间早于当前时间！");
//				}
//				
//				 
//		}
//
//	  }  
//	  $scope.sendMsgToVWC = function(){  
//		//通知VWC 
//		 $http.get(ACTIVITI.CONFIG.contextRoot+'/api/zbq/variables/'+$scope.model.task.processInstanceId)
//		 .success(function(pdata){
//			 var pvs = pdata; 
//			 var pds = $scope.createPidxs(pvs);
//			 //修改新值
//			 console.log("pvs : " , pvs);
//			 pvs[pds['TargLocList']] = $scope.pvars[$scope.pidxs['TargLocList']];
//			 console.log("TargLoc test1 : " ,  $scope.pvars[$scope.pidxs['TargLocList']]);   
//			 pvs[pds['PrePort']] =  $scope.pvars[$scope.pidxs['PrePort']];
//			 $http.put(ACTIVITI.CONFIG.contextRoot+'/api/zbq/variables/'+$scope.model.task.processInstanceId+'/complete' ,pvs)
//				.success(function(data){
//					$scope.pvars = data;
//	        		$scope.pidxs = $scope.createPidxs($scope.pvars);
//	        		console.log("Docking sendMsgVWC ",$scope.pvars[$scope.pidxs['W_pid']]);
//	        		if($scope.pvars[$scope.pidxs['W_pid']].value != null){
//	        			var data2VWC = {
//	    		    			 'msgType' : "msg_UpdateDest" ,
//	    		    			 'V_pid'  : $scope.model.task.processInstanceId ,
//	    		    			 'W_pid' : $scope.pvars[$scope.pidxs['W_pid']].value,
//	    		    			 'reason' : "延期"+$scope.dy+"h"
//	    		    			// 'V_TargLocList' : $scope.pvars[$scope.pidxs['TargLocList']]
//	    		    	};
//	    		    	$http.post(ACTIVITI.CONFIG.contextRoot + "/api/coord/messages/Msg_StartVWC", data2VWC)
//	    	             .success(function (data) {
//	    	                 console.log("Send Message to VMC!", data);
//	    	             })
//	        		}else{
//	        			console.log("Weagon 实例未启动 ， VWC联系无法建立");
//	        		}
//				});
//		 });
//	  }
//}]);
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
    	$http.get(varUrl)
    	.success(function(data){
    		$scope.pvars = data;
    		$scope.pidxs = $scope.createPidxs($scope.pvars);
    		//console.log("voya data : " , data);
    		if($scope.pvars[$scope.pidxs['NowLoc']].value.lname == null && $scope.pvars[$scope.pidxs['PrePort']].value.pname != undefined &&
    				$scope.pvars[$scope.pidxs['NextPort']].value.pname != undefined){
    			$scope.pvars[$scope.pidxs['NowLoc']].value.lname =$scope.pvars[$scope.pidxs['PrePort']].value.pname+"-->"+$scope.pvars[$scope.pidxs['NextPort']].value.pname;
    		}
    		var curDate = new Date();
	 		var stMs = $scope.dateStr2ms($scope.pvars[$scope.pidxs['StartTime']].value);
	 		$scope.dispTime =  $scope.ms2dateStr(stMs + (curDate.getTime() - stMs)*$scope.ZoomInVal);
    		if($scope.pvars[$scope.pidxs['State']].value == 'arrival'){	
    				$http.put(varUrl , $scope.pvars)
        			.success(function(res){
        				console.log("Voyaging Task 结束， 将StartTime上传")
        				$scope.completeTask();  //call parent controller function; 交给VW_F 完成
        				$interval.cancel($scope.voyageTaskTimer);
        			});
    		}
    	})
    }, 1000);
}]);


/***
 * Weagon controllers
 */


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


