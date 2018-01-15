package supplychain.activiti.rest.service.api;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.activiti.app.model.common.ResultListDataRepresentation;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.engine.variable.RestVariableConverter;

import supplychain.entity.VPort;
import supplychain.entity.Weagon;

public class CustomArrayListRestVariableConverter implements RestVariableConverter {

	@Override
	public String getRestTypeName() {
		// TODO Auto-generated method stub
		return "ArrayList";
	}

	@Override
	public Class<?> getVariableType() {
		// TODO Auto-generated method stub
		return ArrayList.class;
	}

	@Override
	public Object getVariableValue(RestVariable result) {
		// TODO Auto-generated method stub
		if(result.getValue() != null) {
			@SuppressWarnings("unchecked")
			List<HashMap<String, Object>> resList = (List<HashMap<String, Object>>) result.getValue();
			return Map2VPortList(resList);
		}
		return null;
	}

	@SuppressWarnings("unused")
	@Override
	public void convertVariableValue(Object variableValue, RestVariable result) {
		// TODO Auto-generated method stub
		System.out.println("result" + variableValue.getClass());
		
		if (variableValue != null) {
			result.setValue(variableValue);
		} else {
			result.setValue(null);
		}
	}

	public List<VPort> Map2VPortList(List<HashMap<String, Object>> resList){
		// TODO Auto-generated method stub
		if (resList != null) {
			List<VPort> list = new ArrayList<VPort>();
			VPortRestVariableConverter vpc =  new VPortRestVariableConverter();
			for(int i = 0 ; i < resList.size();i++) {
				VPort nowVp = vpc.Map2VPort(resList.get(i));
				list.add(nowVp);
			}
			return list;
		}
		return null;
	}

}
