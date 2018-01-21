package supplychain.web;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.activiti.engine.task.Task;
import org.activiti.rest.service.api.runtime.task.TaskBaseResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class TaskController extends TaskBaseResource {
    @ApiOperation(value = "Tasks actions", tags = {"Tasks"},
            notes = "## Complete a task - Request Body\n\n"
                    + " ```JSON\n" + "{\n" + "  \"action\" : \"complete\",\n" + "  \"variables\" : []\n" + "} ```"
                    + "Completes the task. Optional variable array can be passed in using the variables property. More " +
                    "information about the variable format can be found in the REST variables section. Note that the " +
                    "variable-scope that is supplied is ignored and the variables are set on the parent-scope unless a variable" +
                    " exists in a local scope, which is overridden in this case. This is the same behavior as the TaskService" +
                    ".completeTask(taskId, variables) invocation.\n"
                    + "\n"
                    + "Note that also a transientVariables property is accepted as part of this json, that follows the same " +
                    "structure as the variables property."
                    + "\n\n\n"
                    + "## Claim a task - Request Body \n\n"
                    + " ```JSON\n" + "{\n" + "  \"action\" : \"claim\",\n" + "  \"assignee\" : \"userWhoClaims\"\n" + "} ```"
                    + "\n\n\n"
                    + "Claims the task by the given assignee. If the assignee is null, the task is assigned to no-one, " +
                    "claimable again."
                    + "\n\n\n"
                    + "## Delegate a task - Request Body \n\n"
                    + " ```JSON\n" + "{\n" + "  \"action\" : \"delegate\",\n" + "  \"assignee\" : \"userToDelegateTo\"\n" + "} " +
                    "```"
                    + "\n\n\n"
                    + "Delegates the task to the given assignee. The assignee is required."
                    + "\n\n\n"
                    + "## Suspend a process instance\n\n"
                    + " ```JSON\n" + "{\n" + "  \"action\" : \"resolve\"\n" + "} ```"
                    + "\n\n\n"
                    + "Resolves the task delegation. The task is assigned back to the task owner (if any)."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Indicates the action was executed."),
            @ApiResponse(code = 400, message = "When the body contains an invalid value or when the assignee is missing when " +
                    "the action requires it."),
            @ApiResponse(code = 404, message = "Indicates the requested task was not found."),
            @ApiResponse(code = 409, message = "Indicates the action cannot be performed due to a conflict. Either the task was" +
                    " updates simultaneously or the task was claimed by another user, in case of the claim action.")
    })
    @RequestMapping(value = "/zbq/tasks/{taskName}", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void executeTaskAction(@ApiParam(name = "taskId") @PathVariable String taskName) {
        String taskId = taskService.createTaskQuery().taskName(taskName).singleResult().getId();
        Task task = getTaskFromRequest(taskId);
        completeTask(task);
    }

    protected void completeTask(Task task) {
        taskService.complete(task.getId());
    }
}
