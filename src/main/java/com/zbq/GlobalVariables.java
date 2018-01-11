package com.zbq;

import static org.hamcrest.CoreMatchers.instanceOf;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.activiti.engine.impl.form.StringFormType;
import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONException;
import org.activiti.engine.impl.util.json.JSONObject;
import org.activiti.rest.service.api.RestResponseFactory;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.engine.variable.RestVariable.RestVariableScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.NumberSerializers.IntLikeSerializer;

@Component
public class GlobalVariables {
	@Autowired
	private RestResponseFactory restResponseFactory;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private Map<String, JSONArray> variableMap;
	

	public Map<String, JSONArray> getVariableMap() {
		return variableMap;
	}

	public void setVariableMap(Map<String, JSONArray> variableMap) {
		this.variableMap = variableMap;
	}

	public GlobalVariables() {
		super();
		
		variableMap = new ConcurrentHashMap<String, JSONArray>();
	}
	
	
	public void createOrUpdateVariablesByValue(String pid , Map<String, Object> vars) throws JsonProcessingException {
		List<RestVariable> restVars = restResponseFactory.createRestVariables(vars, pid, RestResponseFactory.VARIABLE_PROCESS, RestVariableScope.LOCAL);
		JSONArray jsonVars = new JSONArray();
		for(RestVariable var : restVars) {
			jsonVars.put(new JSONObject(objectMapper.writeValueAsString(var)));
		}
		
		variableMap.put(pid, jsonVars);
	}
	
	public void createOrUpdateVariablesByRestVars(String pid , List<RestVariable> restVars) throws JsonProcessingException {
		JSONArray jsonVars = new JSONArray();
		for(RestVariable var : restVars) {
			jsonVars.put(new JSONObject(objectMapper.writeValueAsString(var)));
		}
		
		variableMap.put(pid, jsonVars);
	}
	
	public JSONObject createOrUpdateVariableByNameAndValue(String pid  , String variableName , Object value) throws JsonProcessingException{
		RestVariable restVar = restResponseFactory.createRestVariable(variableName, value, RestVariableScope.LOCAL, pid, RestResponseFactory.VARIABLE_PROCESS, false , null);
		JSONArray jsonVars = variableMap.get(pid);
		
		JSONObject result  = new JSONObject(objectMapper.writeValueAsString(restVar));
		if(jsonVars != null) {
			int i = 0;
			for (i = 0; i < jsonVars.length(); i++) {
				JSONObject var = (JSONObject) jsonVars.get(i);
				if (var.getString("name").equals(variableName)) {
					jsonVars.put(i , result);
					break;
				}
			}
			
			if(i == jsonVars.length()) {
				System.out.println("创建变量 ： "+variableName + " in "+pid +"process instance");
				jsonVars.put(result);
			}
			
		}else {
			jsonVars = new JSONArray();
			jsonVars.put(result);
		}
	
		variableMap.put(pid, jsonVars);
		return result;
	}
	
	public JSONObject createOrUpdateVariableByRestVar(String pid , String variableName , RestVariable restVar) throws  JsonProcessingException {
		JSONArray jsonVars = variableMap.get(pid);
		JSONObject result = new JSONObject(objectMapper.writeValueAsString(restVar));
		if(jsonVars != null) {
			int i = 0;
			for (i = 0; i < jsonVars.length(); i++) {
				//boolean haveThisVar = false;
				JSONObject var = (JSONObject) jsonVars.get(i);
				if (var.getString("name").equals(variableName)) {
					jsonVars.put(i , result);
					break;
				}
			}
			
			if(i == jsonVars.length()) {
				System.out.println("创建变量 ： "+variableName + " in "+pid +"process instance");
				jsonVars.put(result);
			}
			
		}else {
			jsonVars = new JSONArray();
			jsonVars.put(result);
		}
		
		variableMap.put(pid, jsonVars);
		return result;
	}
	
	public JSONObject getVariableByName(String pid  , String variableName) {
		JSONArray jsonVars = variableMap.get(pid);
		for (int i = 0; i < jsonVars.length(); i++) {
			JSONObject var = (JSONObject) jsonVars.get(i);
			if (var.getString("name").equals(variableName)) {
				return var;
			}
		}
		return null;
	}
}
