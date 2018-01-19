package com.zbq;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONException;
import org.activiti.engine.impl.util.json.JSONObject;
import org.activiti.rest.service.api.RestResponseFactory;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.engine.variable.RestVariable.RestVariableScope;
import org.mvel2.util.Varargs;
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
	private Map<String , Double> carRateMp;
	private final int zoomInRate = 500;

	public Map<String, JSONArray> getVariableMap() {
		return variableMap;
	}

	public void setVariableMap(Map<String, JSONArray> variableMap) {
		this.variableMap = variableMap;

	}

	public GlobalVariables() {
		super();
		this.portsInfo = new ConcurrentHashMap<String, VPort>();
		this.portsInfo.put("黄石", new VPort("黄石", "AfterAD" , 470.0, 2.1,"115.13506", "30.21027", true , 0 ));
		this.portsInfo.put("武穴", new VPort("武穴","AfterAD" , 475.0, 2.1,"115.5674", "29.83692" , true ,1));
		this.portsInfo.put("九江", new VPort("九江", "AfterAD" ,480.0,  2.1,"115.95308", "29.72574",true , 2));
		this.portsInfo.put("安庆", new VPort("安庆", "AfterAD" ,485.0,   2.1,"117.03104", "30.49944", true , 3));
		this.portsInfo.put("池州", new VPort("池州", "AfterAD" ,490.0,   2.1, "117.52272", "30.7332",  true , 4));
		this.portsInfo.put("铜陵", new VPort("铜陵", "AfterAD" ,495.0,   2.1, "117.7291", "30.95259", true , 5));
		this.portsInfo.put("芜湖", new VPort("芜湖", "AfterAD" ,500.0,   2.1, "118.34633", "31.33907", true ,6));
		this.portsInfo.put("马鞍山", new VPort("马鞍山","AfterAD" , 505.0,   2.1,  "118.45491", "31.729427", true ,7));
		this.portsInfo.put("南京", new VPort("南京","AfterAD" , 510.0,  2.1,  "118.800095", "32.146214",  true ,8));
		this.portsInfo.put("仪征", new VPort("仪征", "AfterAD" ,515.0,  2.1,  "119.17036", "32.24261", true ,9));
		this.portsInfo.put("镇江", new VPort("镇江","AfterAD" , 520.0,   2.1, "119.37172", "32.21107", true ,10));
		this.portsInfo.put("泰州", new VPort("泰州", "AfterAD" ,525.0,   2.1,  "119.83584", "32.29552", true ,11));
		this.portsInfo.put("常州", new VPort("常州","AfterAD" , 470.0,   2.1,   "120.006065", "31.976147", true , 12));
		this.portsInfo.put("江阴", new VPort("江阴", "AfterAD" ,513.0,   2.1,  "120.250824", "31.93615", true ,13));
		variableMap = new ConcurrentHashMap<String, JSONArray>();
		
		this.carRateMp = new ConcurrentHashMap<String, Double>();
		this.carRateMp.put("黄石", 2.34);
		this.carRateMp.put("武穴", 2.34);
		this.carRateMp.put("九江",2.34);
		this.carRateMp.put("安庆", 2.34);
		this.carRateMp.put("池州", 2.34);
		this.carRateMp.put("铜陵", 2.34);
		this.carRateMp.put("芜湖", 2.34);
		this.carRateMp.put("马鞍山", 2.34);
		this.carRateMp.put("南京", 2.34);
		this.carRateMp.put("仪征", 2.34);
		this.carRateMp.put("镇江", 2.34);
		this.carRateMp.put("泰州", 2.34);
		this.carRateMp.put("常州",2.34);
		this.carRateMp.put("江阴", 2.34);
	}

	public Map<String, VPort> getPortsInfo() {
		return portsInfo;
	}

	public void setPortsInfo(Map<String, VPort> portsInfo) {
		this.portsInfo = portsInfo;
	}

	public void createOrUpdateVariablesByValue(String pid, Map<String, Object> vars) {
		List<RestVariable> restVars = restResponseFactory.createRestVariables(vars, pid,
				RestResponseFactory.VARIABLE_PROCESS, RestVariableScope.LOCAL);
		JSONArray jsonVars = new JSONArray();
		for (RestVariable var : restVars) {
			try {
				jsonVars.put(new JSONObject(objectMapper.writeValueAsString(var)));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

//		JSONArray jvs = variableMap.get(pid);
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

	public JSONObject createOrUpdateVariableByNameAndValue(String pid, String variableName, Object value) {
		RestVariable restVar = restResponseFactory.createRestVariable(variableName, value, RestVariableScope.LOCAL, pid,
				RestResponseFactory.VARIABLE_PROCESS, false, null);
		JSONArray jsonVars = variableMap.get(pid);

		JSONObject result = null;
		try {
			result = new JSONObject(objectMapper.writeValueAsString(restVar));
		} catch (JSONException | JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	public Map<String, Double> getCarRateMp() {
		return carRateMp;
	}

	public void setCarRateMp(Map<String, Double> carRateMp) {
		this.carRateMp = carRateMp;
	}

	public  int getZoominrate() {
		return zoomInRate;
	}
	
	
}
