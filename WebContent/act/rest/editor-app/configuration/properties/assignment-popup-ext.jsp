<%@ page contentType="text/html;charset=UTF-8" %>
<div class="modal" ng-controller="KisBpmAssignmentPopupCtrl">
    <div class="modal-dialog" style="width:1350px;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true" ng-click="close()">&times;</button>
                <h2 translate>PROPERTY.ASSIGNMENT.TITLE</h2>
            </div>
            <div class="modal-body">
            	
            	<div class="row row-no-gutter">
            	  <div class="col-xs-2">
            		<div class="form-group">
            			<label for="assigneeField">{{'PROPERTY.ASSIGNMENT.ASSIGNEE' | translate}}</label>
            			<input type="text" id="assigneeField" class="form-control" ng-model="assignment.assignee" placeholder="{{'PROPERTY.ASSIGNMENT.ASSIGNEE_PLACEHOLDER' | translate}}" />
            		</div>
                    <div class="form-group">
                    	<label for="userField">{{'PROPERTY.ASSIGNMENT.CANDIDATE_USERS' | translate}}</label>
                        <div ng-repeat="candidateUser in assignment.candidateUsers">
            	            <input id="userField" class="form-control" type="text" ng-model="candidateUser.value" />
            	            <i class="glyphicon glyphicon-minus clickable-property" ng-click="removeCandidateUserValue($index)"></i>
            	            <i ng-if="$index == (assignment.candidateUsers.length - 1)" class="glyphicon glyphicon-plus clickable-property" ng-click="addCandidateUserValue($index)"></i>
                        </div>
                   	</div>
            
                    <div class="form-group">
                    	<label for="groupField">{{'PROPERTY.ASSIGNMENT.CANDIDATE_GROUPS' | translate}}</label>
                        <div ng-repeat="candidateGroup in assignment.candidateGroups">
            	          	<input id="groupField" class="form-control" type="text" ng-model="candidateGroup.value" />
            	          	<i class="glyphicon glyphicon-minus clickable-property" ng-click="removeCandidateGroupValue($index)"></i>
            	          	<i ng-if="$index == (assignment.candidateGroups.length - 1)" class="glyphicon glyphicon-plus clickable-property" ng-click="addCandidateGroupValue($index)"></i>
                        </div>
                    </div>
                  </div>
                  <div class="col-xs-10">
                  	<div style="overflow:auto; width:1100px; height:600px; border:solid; border-width:0px; border-color:#999; " >
                    	<iframe id="taskSetting" frameborder="no" border="0" style=" margin:0px 0 0 0px; width:1066px;height:850px;border-style: none;border-width:0px;border-color:#CCC; "
                                src="" scrolling="no">
                    	</iframe>
                    </div>
                  </div>
                </div>
            
            </div>
            <div class="modal-footer">
                <button ng-click="close()" class="btn btn-primary" translate>ACTION.CANCEL</button>
                <button ng-click="save()" class="btn btn-primary" translate>ACTION.SAVE</button>
            </div>
        </div>
    </div>
    
</div>
<script>
	var frame = document.getElementById("taskSetting");
	var element = angular.element(frame);
	var controller = element.controller();
	var scope = element.scope();
	if (scope.processName == "" || scope.taskId == "" || scope.taskName == "") {
		alert("请输全流程名称、任务ID和任务名称！");
	} else {
		var src = "<%=request.getContextPath() %>/a/oa/setting/taskSetting/form?procDefKey=" + scope.processName + "&userTaskId=" + scope.taskId + "&userTaskName=" + scope.taskName;
		frame.setAttribute("src",src);
	}
</script>