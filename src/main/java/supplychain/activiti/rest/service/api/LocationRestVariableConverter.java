package supplychain.activiti.rest.service.api;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.engine.variable.RestVariableConverter;

import supplychain.entity.Location;

public class LocationRestVariableConverter implements RestVariableConverter {

	@Override
	public String getRestTypeName() {
		// TODO Auto-generated method stub
		return "Location";
	}

	@Override
	public Class<?> getVariableType() {
		// TODO Auto-generated method stub
		return Location.class;
	}

	@Override
	public Object getVariableValue(RestVariable result) {
		// TODO Auto-generated method stub
		if (result.getValue() != null) {
//			if (!(result.getValue() instanceof Location)) {
//				throw new ActivitiIllegalArgumentException("Converter can only convert Location Type");
//			}
			return (Object) result.getValue();
		}
		return null;
	}

	@Override
	public void convertVariableValue(Object variableValue, RestVariable result) {
		// TODO Auto-generated method stub
		if (variableValue != null) {
//			if (!(variableValue instanceof Location)) {
//				throw new ActivitiIllegalArgumentException("Converter can only convert Location Type");
//			}
			result.setValue(variableValue);
		} else {
			result.setValue(null);
		}
	}
}
