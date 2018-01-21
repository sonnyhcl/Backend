package supplychain.activiti.rest.service.api;

import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.engine.variable.RestVariableConverter;
import supplychain.entity.VPort;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class VPortRestVariableConverter implements RestVariableConverter {

    @Override
    public String getRestTypeName() {
        // TODO Auto-generated method stub
        return "VPort";
    }

    @Override
    public Class<?> getVariableType() {
        // TODO Auto-generated method stub
        return VPort.class;
    }

    @Override
    public Object getVariableValue(RestVariable result) {
        // TODO Auto-generated method stub
        if (result.getValue() != null) {

            if (result.getValue() instanceof LinkedHashMap) {
                @SuppressWarnings("unchecked")
                HashMap<String, Object> mp = (HashMap<String, Object>) result.getValue();
                VPort vp = Map2VPort(mp);
                return vp;
            }
        }
        return null;
    }

    @Override
    public void convertVariableValue(Object variableValue, RestVariable result) {
        // TODO Auto-generated method stub
        if (variableValue != null) {
            result.setValue(variableValue);
        } else {
            result.setValue(null);
        }
    }

    public VPort Map2VPort(HashMap<String, Object> map) {
        // TODO Auto-generated method stub
        String pname = map.get("pname") == null ? null : map.get("pname").toString();
        System.out.println(map.get("quayRate"));
        double quayRate = map.get("quayRate") == null ? null : Double.parseDouble(map.get("quayRate").toString());
        double weight = map.get("weight") == null ? null : Double.parseDouble(map.get("weight").toString());
        boolean isCraneStart = map.get("isCraneStart") == null ? null : (boolean) map.get("isCraneStart");
        String eStart = map.get("estart") == null ? null : map.get("estart").toString();
        String eEnd = map.get("eend") == null ? null : map.get("eend").toString();
        boolean isMeetWeightCond = map.get("isMeetWeightCond") == null ? null : (boolean) map.get("isMeetWeightCond");
        String state = map.get("state") == null ? null : map.get("state").toString();
        double cost = map.get("cost") == null ? null : Double.parseDouble(map.get("cost").toString());
        String x_coor = map.get("x_coor") == null ? null : map.get("x_coor").toString();
        String y_coor = map.get("y_coor") == null ? null : map.get("y_coor").toString();
        double sortFlag = map.get("sortFlag") == null ? null : Double.parseDouble(map.get("sortFlag").toString());
        VPort vp = new VPort("VPort", pname, quayRate, weight, isCraneStart, eStart, eEnd, isMeetWeightCond, state, cost, x_coor, y_coor, sortFlag);
        return vp;
    }

}
