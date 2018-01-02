package supplychain.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.runtime.Execution;
import org.activiti.rest.exception.ActivitiConflictException;
import org.activiti.rest.service.api.RestResponseFactory;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.engine.variable.RestVariable.RestVariableScope;
import org.activiti.rest.service.api.runtime.process.BaseExecutionVariableResource;
import org.activiti.rest.service.api.runtime.process.BaseVariableCollectionResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import supplychain.activiti.rest.service.api.CustomRestResponseFactory;
import supplychain.entity.Location;
import supplychain.entity.VesselVariablesResponse;

/**
 * @author bqzhu
 */
@RestController
@Api(tags = { "Vessel Process Instances" }, description = "Manage vessel Process Instances", authorizations = {
		@Authorization(value = "basicAuth") })
public class VesselProcessInstanceVariableDataResource extends BaseVariableCollectionResource {

	@ApiOperation(value = "List of variables for a vessel process instance", tags = {
			"Vessel Process Instances" }, notes = "In case the variable is a binary variable or serializable, the valueUrl points to an URL to fetch the raw value. If it’s a plain variable, the value is present in the response. Note that only local scoped variables are returned, as there is no global scope for process-instance variables.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Indicates the process instance was found and variables are returned."),
			@ApiResponse(code = 400, message = "Indicates the requested process instance was not found.") })
	@RequestMapping(value = "/runtime/vessel-procinst/{processInstanceId}/variables", method = RequestMethod.GET, produces = "application/json")
	public List<RestVariable> getVariables(
			@ApiParam(name = "processInstanceId", value = "The id of the process instance to the variables for.") @PathVariable String processInstanceId,
			@RequestParam(value = "scope", required = false) String scope, HttpServletRequest request) {

		Execution execution = getProcessInstanceFromRequest(processInstanceId);
		return processVariables(execution, scope, RestResponseFactory.VARIABLE_PROCESS);
	}

	@ApiOperation(value = "Update a single or binary variable or multiple variables on a process instance", tags = {
			"Process Instances" }, nickname = "createOrUpdateProcessVariable", notes = "## Update multiples variables\n\n"
					+ " ```JSON\n" + "[\n" + "   {\n" + "      \"name\":\"intProcVar\"\n"
					+ "      \"type\":\"integer\"\n" + "      \"value\":123\n" + "   },\n" + "\n" + "   ...\n" + "] ```"
					+ "\n\n\n"
					+ " Any number of variables can be passed into the request body array. More information about the variable format can be found in the REST variables section. Note that scope is ignored, only local variables can be set in a process instance."
					+ "\n\n\n" + "## Update a single variable\n\n" + "```JSON\n {\n" + "    \"name\":\"intProcVar\"\n"
					+ "    \"type\":\"integer\"\n" + "    \"value\":123\n" + " } ```" + "\n\n\n"
					+ "##  Update an existing binary variable\n\n" + "\n\n\n"
					+ "The request should be of type multipart/form-data. There should be a single file-part included with the binary value of the variable. On top of that, the following additional form-fields can be present:\n"
					+ "\n" + "name: Required name of the variable.\n" + "\n"
					+ "type: Type of variable that is created. If omitted, binary is assumed and the binary data in the request will be stored as an array of bytes.")
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Indicates the process instance was found and variable is created."),
			@ApiResponse(code = 400, message = "Indicates the request body is incomplete or contains illegal values. The status description contains additional information about the error."),
			@ApiResponse(code = 404, message = "Indicates the requested process instance was not found."),
			@ApiResponse(code = 415, message = "Indicates the serializable data contains an object for which no class is present in the JVM running the Activiti engine and therefore cannot be deserialized.")

	})
	@RequestMapping(value = "/runtime/vessel-procinst/{processInstanceId}/variables", method = RequestMethod.PUT, produces = "application/json")
	public Object createOrUpdateExecutionVariable(
			@ApiParam(name = "processInstanceId", value = "The id of the process instance to create the new variable for.") @PathVariable String processInstanceId,
			HttpServletRequest request, HttpServletResponse response) {

		Execution execution = getProcessInstanceFromRequest(processInstanceId);
		return createExecutionVariable(execution, true, RestResponseFactory.VARIABLE_PROCESS, request, response);
	}
		

	/*************************************************************************************************************************************************
	 * 重载其他函数
	 **************************************************************************************************************************************************/
	@Override
	protected Object createExecutionVariable(Execution execution, boolean override, int variableType, HttpServletRequest request, HttpServletResponse response) {

	    Object result = null;
	    if (request instanceof MultipartHttpServletRequest) {
	      result = setBinaryVariable((MultipartHttpServletRequest) request, execution, variableType, true);
	    } else {

	      List<RestVariable> inputVariables = new ArrayList<RestVariable>();
	      List<RestVariable> resultVariables = new ArrayList<RestVariable>();
	      result = resultVariables;

	      try {
	        @SuppressWarnings("unchecked")
	        List<Object> variableObjects = (List<Object>) objectMapper.readValue(request.getInputStream(), List.class);
	        for (Object restObject : variableObjects) {
	          RestVariable restVariable = objectMapper.convertValue(restObject, RestVariable.class);
	          inputVariables.add(restVariable);
	        }
	      } catch (Exception e) {
	        throw new ActivitiIllegalArgumentException("Failed to serialize to a RestVariable instance", e);
	      }

	      if (inputVariables == null || inputVariables.size() == 0) {
	        throw new ActivitiIllegalArgumentException("Request didn't contain a list of variables to create.");
	      }

	      RestVariableScope sharedScope = null;
	      RestVariableScope varScope = null;
	      Map<String, Object> variablesToSet = new HashMap<String, Object>();

	      for (RestVariable var : inputVariables) {
	        // Validate if scopes match
	        varScope = var.getVariableScope();
	        if (var.getName() == null) {
	          throw new ActivitiIllegalArgumentException("Variable name is required");
	        }

	        if (varScope == null) {
	          varScope = RestVariableScope.LOCAL;
	        }
	        if (sharedScope == null) {
	          sharedScope = varScope;
	        }
	        if (varScope != sharedScope) {
	          throw new ActivitiIllegalArgumentException("Only allowed to update multiple variables in the same scope.");
	        }

	        if (!override && hasVariableOnScope(execution, var.getName(), varScope)) {
	          throw new ActivitiConflictException("Variable '" + var.getName() + "' is already present on execution '" + execution.getId() + "'.");
	        }

	        Object actualVariableValue = restResponseFactory.getVariableValue(var);
	        variablesToSet.put(var.getName(), actualVariableValue);
	        resultVariables.add(restResponseFactory.createRestVariable(var.getName(), actualVariableValue, varScope, execution.getId(), variableType, false));
	      }

	      if (!variablesToSet.isEmpty()) {
	        if (sharedScope == RestVariableScope.LOCAL) {
	          runtimeService.setVariablesLocal(execution.getId(), variablesToSet);
	        } else {
	          if (execution.getParentId() != null) {
	            // Explicitly set on parent, setting non-local variables
	            // on execution itself will override local-variables if
	            // exists
	            runtimeService.setVariables(execution.getParentId(), variablesToSet);
	          } else {
	            // Standalone task, no global variables possible
	            throw new ActivitiIllegalArgumentException("Cannot set global variables on execution '" + execution.getId() + "', task is not part of process.");
	          }
	        }
	      }
	    }
	    response.setStatus(HttpStatus.CREATED.value());
	    return result;
	  }

}
