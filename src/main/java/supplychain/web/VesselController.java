package supplychain.web;

import org.activiti.app.service.editor.ActivitiTaskActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import supplychain.activiti.rest.service.api.CustomActivitiTaskActionService;


@RestController
public class VesselController extends AbstractController{

	@Autowired
	protected CustomActivitiTaskActionService taskActionService;
	//@CrossOrigin  // 单独测试起了作用
	@RequestMapping(value = "/test")
	public String test() {
		return "test ok";
	}
	@RequestMapping(value = "/tasks/{taskId}/action/complete", method = RequestMethod.PUT)
	@ResponseStatus(value = HttpStatus.OK)
	public void completeTask(@PathVariable String taskId) {
		System.out.println("*******************completeTask**************************");
	    taskActionService.completeTask(taskId);
	}
}
