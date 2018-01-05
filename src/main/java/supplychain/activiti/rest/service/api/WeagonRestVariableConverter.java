package supplychain.activiti.rest.service.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.engine.variable.RestVariableConverter;

import supplychain.entity.Location;
import supplychain.entity.Weagon;

public class WeagonRestVariableConverter implements RestVariableConverter {

	@Override
	public String getRestTypeName() {
		// TODO Auto-generated method stub
		return "Weagon";
	}

	@Override
	public Class<?> getVariableType() {
		// TODO Auto-generated method stub
		return Weagon.class;
	}

	@Override
	public Object getVariableValue(RestVariable result) {
		// TODO Auto-generated method stub
		if (result.getValue() != null) {
		//	System.out.println(result.getValue().getClass());
//			if (!(result.getValue() instanceof Weagon)) {
//				throw new ActivitiIllegalArgumentException("Converter can only convert Weagon Type");
//			}
//			Weagon w = new Weagon();
//			HashMap<String, Object> map = new HashMap<String , Object>();
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			Date end = null;
//			Date start = null;
//			if(map.get("end") != null) {
//				end = sdf.parse((String) map.get("end"));
//			}
//			if(map.get("start") != null) {
//				end = sdf.parse((String) map.get("start"));
//			}
//			w.setEnd(end);
//			w.setStart(start);
//			w.setName(name);
//			w.setNeedPlan(needPlan);
//			w.setpArri(pArri);
//			w.setPid(pid);
//			w.setPlanRes(planRes);
//			w.setW_TargLoc(w_TargetLocation);
//			w.setW_Velocity(w_Velocity);
//			w.setW_id(w_id);
//			w.setX_Coor(x_Coor);
//			w.setY_Coor(y_Coor);
		//	return (Weagon) result.getValue();
		}

		return (Object) result.getValue();
	}

	@Override
	public void convertVariableValue(Object variableValue, RestVariable result) {
		// TODO Auto-generated method stub
		if (variableValue != null) {
//			if (!(variableValue instanceof Weagon)) {
//				throw new ActivitiIllegalArgumentException("Converter can only convert Weagon Type");
//			}
			result.setValue(variableValue);
		} else {
			result.setValue(null);
		}
	}
}
