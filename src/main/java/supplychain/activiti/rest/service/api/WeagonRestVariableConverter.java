package supplychain.activiti.rest.service.api;

import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.engine.variable.RestVariableConverter;
import supplychain.entity.Location;
import supplychain.entity.Weagon;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

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
            if (result.getValue() instanceof LinkedHashMap) {
                @SuppressWarnings("unchecked")
                HashMap<String, Object> mp = (HashMap<String, Object>) result.getValue();
                try {
                    Weagon w = Map2Weagon(mp);
                    return w;
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
//			if (!(result.getValue() instanceof Weagon)) {
//				throw new ActivitiIllegalArgumentException("Converter can only convert Weagon Type");
//			}
        }
        return null;
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

    public Weagon Map2Weagon(HashMap<String, Object> map) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date end = map.get("end") == null ? null : sdf.parse((String) map.get("end"));
        Date start = map.get("start") == null ? null : sdf.parse((String) map.get("start"));
        Date pArri = map.get("pArri") == null ? null : sdf.parse((String) map.get("pArri"));

        String pid = map.get("pid") == null ? null : (String) map.get("pid");//流程实例的id
        String w_id = map.get("w_id") == null ? null : (String) map.get("w_id");
        String w_Name = map.get("w_Name") == null ? null : (String) map.get("w_Name");
        String x_Coor = map.get("x_Coor") == null ? null : map.get("x_Coor").toString();
        String y_Coor = map.get("y_Coor") == null ? null : map.get("y_Coor").toString();
        String w_Velocity = map.get("w_Velocity") == null ? null : (String) map.get("w_Velocity");
        boolean needPlan = map.get("needPlan") == null ? null : (boolean) map.get("needPlan");
        int planRes = map.get("planRes") == null ? null : (Integer) map.get("planRes");
        boolean isArrival = map.get("isArrival") == null ? null : (boolean) map.get("isArrival");
        LocationRestVariableConverter c = new LocationRestVariableConverter();
        @SuppressWarnings("unchecked")
        HashMap<String, Object> lmap = (HashMap<String, Object>) map.get("w_TargLoc");
        Location w_TargLoc = c.Map2Location(lmap);
        Weagon w = new Weagon("Weagon", pid, w_id, w_Name, x_Coor, y_Coor, w_Velocity, start, end, pArri, needPlan, planRes, w_TargLoc, isArrival);
        return w;
    }
}
