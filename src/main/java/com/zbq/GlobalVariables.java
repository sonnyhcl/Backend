package com.zbq;

import static org.hamcrest.CoreMatchers.instanceOf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.activiti.rest.service.api.RestResponseFactory;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.engine.variable.RestVariable.RestVariableScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import supplychain.entity.VPort;

@Component
public class GlobalVariables {
	@Autowired
	private RestResponseFactory restResponseFactory;

	@Autowired
	private ObjectMapper objectMapper;

	private Map<String, VPort> portsInfo;
	private Map<String, JSONArray> variableMap;

	public Map<String, JSONArray> getVariableMap() {
		return variableMap;
	}

	public void setVariableMap(Map<String, JSONArray> variableMap) {
		this.variableMap = variableMap;

	}

	public GlobalVariables() {
		super();
		portsInfo = new ConcurrentHashMap<String, VPort>();
		this.portsInfo.put("黄石", new VPort("黄石", 470.0,3.70, true));
		this.portsInfo.put("武穴", new VPort("武穴", 475.0,6.78, true));
		this.portsInfo.put("九江", new VPort("九江", 480.0, 7.34, true));
		this.portsInfo.put("安庆", new VPort("安庆", 485.0,  9.36, true));
		this.portsInfo.put("池州", new VPort("池州", 490.0,  3.45, true));
		this.portsInfo.put("铜陵", new VPort("铜陵", 495.0,  10.34, true));
		this.portsInfo.put("芜湖", new VPort("芜湖", 500.0,  5.67, true));
		this.portsInfo.put("马鞍山", new VPort("马鞍山", 505.0,  5.34, true));
		this.portsInfo.put("南京", new VPort("南京", 510.0, 2.98, true));
		this.portsInfo.put("仪征", new VPort("仪征", 515.0, 3.54, true));
		this.portsInfo.put("镇江", new VPort("镇江", 520.0,  6.35, true));
		this.portsInfo.put("泰州", new VPort("泰州", 525.0,  1.34, true));
		this.portsInfo.put("常州", new VPort("常州", 470.0,  5.63, true));
		this.portsInfo.put("江阴", new VPort("江阴", 513.0,  3.46, true));
		variableMap = new ConcurrentHashMap<String, JSONArray>();
	}

	public Map<String, VPort> getPortsInfo() {
		return portsInfo;
	}

	public void setPortsInfo(Map<String, VPort> portsInfo) {
		this.portsInfo = portsInfo;
	}

	public void createOrUpdateVariablesByValue(String pid, Map<String, Object> vars) throws JsonProcessingException {
		List<RestVariable> restVars = restResponseFactory.createRestVariables(vars, pid,
				RestResponseFactory.VARIABLE_PROCESS, RestVariableScope.LOCAL);
		JSONArray jsonVars = new JSONArray();
		for (RestVariable var : restVars) {
			jsonVars.put(new JSONObject(objectMapper.writeValueAsString(var)));
		}

		variableMap.put(pid, jsonVars);
	}

	public void createOrUpdateVariablesByRestVars(String pid, List<RestVariable> restVars)
			throws JsonProcessingException {
		JSONArray jsonVars = new JSONArray();
		for (RestVariable var : restVars) {
			jsonVars.put(new JSONObject(objectMapper.writeValueAsString(var)));
		}

		variableMap.put(pid, jsonVars);
	}

	public JSONObject createOrUpdateVariableByNameAndValue(String pid, String variableName, Object value)
			throws JsonProcessingException {
		RestVariable restVar = restResponseFactory.createRestVariable(variableName, value, RestVariableScope.LOCAL, pid,
				RestResponseFactory.VARIABLE_PROCESS, false, null);
		JSONArray jsonVars = variableMap.get(pid);

		JSONObject result = new JSONObject(objectMapper.writeValueAsString(restVar));
		if (jsonVars != null) {
			int i = 0;
			for (i = 0; i < jsonVars.length(); i++) {
				JSONObject var = (JSONObject) jsonVars.get(i);
				if (var.getString("name").equals(variableName)) {
					jsonVars.put(i, result);
					break;
				}
			}

			if (i == jsonVars.length()) {
				System.out.println("创建变量 ： " + variableName + " in " + pid + "process instance");
				jsonVars.put(result);
			}

		} else {
			jsonVars = new JSONArray();
			jsonVars.put(result);
		}

		variableMap.put(pid, jsonVars);
		return result;
	}

	public JSONObject createOrUpdateVariableByRestVar(String pid, String variableName, RestVariable restVar)
			throws JsonProcessingException {
		JSONArray jsonVars = variableMap.get(pid);
		JSONObject result = new JSONObject(objectMapper.writeValueAsString(restVar));
		if (jsonVars != null) {
			int i = 0;
			for (i = 0; i < jsonVars.length(); i++) {
				// boolean haveThisVar = false;
				JSONObject var = (JSONObject) jsonVars.get(i);
				if (var.getString("name").equals(variableName)) {
					jsonVars.put(i, result);
					break;
				}
			}

			if (i == jsonVars.length()) {
				System.out.println("创建变量 ： " + variableName + " in " + pid + "process instance");
				jsonVars.put(result);
			}

		} else {
			jsonVars = new JSONArray();
			jsonVars.put(result);
		}

		variableMap.put(pid, jsonVars);
		return result;
	}

	public JSONObject getVariableByName(String pid, String variableName) {
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
