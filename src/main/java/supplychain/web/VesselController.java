package supplychain.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zbq.ACTFEvent;
import com.zbq.GlobalEventQueue;
import com.zbq.GlobalVariables;
import com.zbq.VWFEvent;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.activiti.engine.runtime.Execution;
import org.activiti.rest.service.api.RestResponseFactory;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import supplychain.activiti.rest.service.api.CustomActivitiTaskActionService;
import supplychain.activiti.rest.service.api.CustomBaseExcutionVariableResource;
import supplychain.activiti.rest.service.api.CustomBaseVariableCollectionResource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

@RestController
public class VesselController extends AbstractController {

    @Autowired
    protected CustomActivitiTaskActionService taskActionService;

    @Autowired
    private GlobalEventQueue globalEventQueue;

    @Autowired
    private GlobalVariables globalVariables;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    protected RestResponseFactory restResponseFactory;
    @Autowired
    private CustomBaseExcutionVariableResource baseExcutionVariableResource;
    @Autowired
    private CustomBaseVariableCollectionResource baseVariableCollectionResource;

    // @CrossOrigin // 单独测试起了作用
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

    @RequestMapping(value = "/sevents", method = RequestMethod.GET)
    public ResponseEntity<String> queryEvents() throws InterruptedException {
        // System.out.println("lastId : "+lastId);
        JSONArray result = new JSONArray();
        LinkedBlockingQueue<VWFEvent> q = globalEventQueue.getSendQueue();
        while (!q.isEmpty()) {
            VWFEvent e = q.take();
            // System.out.println("send lastId ：" + lastId +" --- e.getId() : "+e.getId());
            // if(e.getId()>=lastId) {
            result.put(e.getJson());
            System.out.println(e.getJson());
            // }
        }

        return new ResponseEntity<String>(result.toString(), HttpStatus.OK);
    }

//	@RequestMapping(value = "/revents", method = RequestMethod.POST, produces = "application/json")
//	public ResponseEntity<String> createEvent(@RequestBody HashMap<String, Object> map) throws InterruptedException {
//		System.out.println("Events from frontEnd");
//		System.out.println("Type : " + map.get("type"));
//		ACTFEvent e = new ACTFEvent(translateType((String) map.get("type")));
//		System.out.println(map.get("id").toString());
//		e.setId(Long.parseLong(map.get("id").toString()));
//		HashMap<String, Object> dataMap = new HashMap<String, Object>();
//		dataMap.put("data", map.get("data"));
//		JSONObject data = new JSONObject(dataMap);
//		e.setData(data.getJSONObject("data"));
//		LinkedBlockingQueue<ACTFEvent> q = globalEventQueue.getRecQueue();
//		// create receive event
//		q.put(e);
//		JSONArray result = new JSONArray();
//		result.put(e.getJson());
//		return new ResponseEntity<String>(result.toString(), HttpStatus.OK);
//	}

	@RequestMapping(value = "/revents", method = RequestMethod.GET)
	public ResponseEntity<String> queryREvents() throws InterruptedException {
		// System.out.println("GET sevents");
		JSONArray result = new JSONArray();
		LinkedBlockingQueue<ACTFEvent> q = globalEventQueue.getRecQueue();
		while (!q.isEmpty()) {// 取空队列里面的所有事件
			ACTFEvent e = q.take();
			result.put(e.getJson());
		}
		return new ResponseEntity<String>(result.toString(), HttpStatus.OK);
	}

	// @RequestMapping(value = "/process-instances", method = RequestMethod.POST)
	// public ProcessInstanceRepresentation startNewProcessInstance(@RequestBody
	// CreateProcessInstanceRepresentation startRequest) {
	// return super.startNewProcessInstance(startRequest);
	// }

	/**
	 * Custom GET/PUT Variables
	 */
	@RequestMapping(value = "/zbq/variables/{pid}", method = RequestMethod.GET)
	public ResponseEntity<String> queryVariables(@PathVariable String pid) {
		JSONArray result = new JSONArray();

		if (globalVariables.getVariableMap().containsKey(pid)) {
			result = globalVariables.getVariableMap().get(pid);
		}
		return new ResponseEntity<String>(result.toString(), HttpStatus.OK);
	}

	@RequestMapping(value = "/zbq/variables/{pid}/{variableName}", method = RequestMethod.GET)
	public ResponseEntity<String> queryVariablesByName(@PathVariable String pid, @PathVariable String variableName) {
		JSONObject result = globalVariables.getVariableByName(pid, variableName);
		return new ResponseEntity<String>(result.toString(), HttpStatus.OK);
	}
	
	/**
	 * PUT to global variables cache
	 * 传入RestObject
	 * @param processInstanceId
	 * @param request
	 * @return
	 * @throws JsonProcessingException 
	 */
	@RequestMapping(value = "/zbq/variables/{processInstanceId}", method = RequestMethod.PUT)
	public ResponseEntity<List<RestVariable>> updateVariablesToCache(@PathVariable String processInstanceId, HttpServletRequest request , HttpServletResponse response) throws JsonProcessingException {
		
		List<RestVariable> result = null;
		result = baseVariableCollectionResource.createExecutionVariableToCacheOrEngine(processInstanceId, true, RestResponseFactory.VARIABLE_PROCESS, request, response  ,false);
		return new ResponseEntity<List<RestVariable>>(result ,HttpStatus.OK);
	}

	/**
	 * PUT to global variables cache and process engine
	 * @param pid
	 * @param body
	 * @return
	 * @throws JsonProcessingException 
	 */
	@RequestMapping(value = "/zbq/variables/{processInstanceId}/complete", method = RequestMethod.PUT)
	public ResponseEntity<List<RestVariable>> updateVariablesCacheAndEngine(@PathVariable String processInstanceId, HttpServletRequest request , HttpServletResponse response) throws JsonProcessingException {

		// System.out.println("/zbq/variables/ update :"+ body);

//		JSONArray vars = new JSONArray(body);
//		globalVariables.getVariableMap().put(pid, vars);
//		baseVariableCollectionResource.createOrUpdateExecutionVariable2(pid, body);
        List<RestVariable> result = null;
        //PUT to cache if isComplete == true  , then  PUT to Engine
        HttpServletRequest requestCopy = request;
        result = baseVariableCollectionResource.createExecutionVariableToCacheOrEngine(processInstanceId, true,
				RestResponseFactory.VARIABLE_PROCESS, request, response, true);
        return new ResponseEntity<List<RestVariable>>(result, HttpStatus.OK);
    }

    /**
     * PUT a RestVariable only to cache
     *
     * @param processInstanceId
     * @param variableName
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/zbq/variables/{processInstanceId}/{variableName}", method = RequestMethod.PUT)
    public ResponseEntity<RestVariable> updateVariablesByNameToCache(@PathVariable String processInstanceId, @PathVariable
			String variableName,
                                                                     HttpServletRequest request) throws JsonProcessingException {
        Execution execution = baseExcutionVariableResource.getProcessInstanceFromRequest(processInstanceId);
        RestVariable restVariable = null;
        try {
            restVariable = objectMapper.readValue(request.getInputStream(), RestVariable.class);
        } catch (Exception e) {
            throw new ActivitiIllegalArgumentException(
                    "request body could not be transformed to a RestVariable instance.");
        }

        if (restVariable == null) {
            throw new ActivitiException("Invalid body was supplied");
        }
        if (!restVariable.getName().equals(variableName)) {
            throw new ActivitiIllegalArgumentException(
                    "Variable name in the body should be equal to the name used in the requested URL.");
        }
        globalVariables.createOrUpdateVariableByRestVar(processInstanceId, variableName, restVariable);
        return new ResponseEntity<RestVariable>(restVariable, HttpStatus.OK);
    }

    /**
     * PUT a RestVariable to cache and Engine
     *
     * @param processInstanceId
     * @param variableName
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/zbq/variables/{processInstanceId}/{variableName}/complete", method = RequestMethod.PUT)
    public ResponseEntity<RestVariable> updateVariableByNameToCacheAndEngine(@PathVariable String processInstanceId,
																			 @PathVariable String variableName,
                                                                             HttpServletRequest request) throws
			JsonProcessingException {

        // System.out.println("/zbq/variables/ update :"+ body);

        // 上传到流程引擎
        Execution execution = baseExcutionVariableResource.getProcessInstanceFromRequest(processInstanceId);

        RestVariable result = null;

        RestVariable restVariable = null;
        try {
            restVariable = objectMapper.readValue(request.getInputStream(), RestVariable.class);
        } catch (Exception e) {
            throw new ActivitiIllegalArgumentException(
                    "request body could not be transformed to a RestVariable instance.");
        }

        if (restVariable == null) {
            throw new ActivitiException("Invalid body was supplied");
        }
        if (!restVariable.getName().equals(variableName)) {
            throw new ActivitiIllegalArgumentException(
                    "Variable name in the body should be equal to the name used in the requested URL.");
        }

        //PUT to engine
        result = baseExcutionVariableResource.setSimpleVariable(restVariable, execution, false);
        //PUT to cache
        globalVariables.createOrUpdateVariableByRestVar(processInstanceId, variableName, restVariable);

        return new ResponseEntity<RestVariable>(result, HttpStatus.OK);
    }

}
