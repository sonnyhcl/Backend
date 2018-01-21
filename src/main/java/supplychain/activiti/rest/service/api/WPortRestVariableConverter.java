package supplychain.activiti.rest.service.api;

import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.engine.variable.RestVariableConverter;
import supplychain.entity.WPort;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class WPortRestVariableConverter implements RestVariableConverter {

    @Override
    public String getRestTypeName() {
        // TODO Auto-generated method stub
        return "WPort";
    }

    @Override
    public Class<?> getVariableType() {
        // TODO Auto-generated method stub
        return WPort.class;
    }

    @Override
    public Object getVariableValue(RestVariable result) {
        // TODO Auto-generated method stub
        if (result.getValue() != null) {

            if (result.getValue() instanceof LinkedHashMap) {
                @SuppressWarnings("unchecked")
                HashMap<String, Object> mp = (HashMap<String, Object>) result.getValue();
                WPort wp = Map2WPort(mp);
                return wp;
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

    public WPort Map2WPort(HashMap<String, Object> map) {
        // TODO Auto-generated method stub
        String pname = map.get("pname") == null ? null : map.get("pname").toString();
        double carryRate = map.get("carryRate") == null ? null : Double.parseDouble(map.get("carryRate").toString());
        String esTime = map.get("esTime") == null ? null : map.get("esTime").toString();
        double dist = map.get("dist") == null ? null : Double.parseDouble(map.get("dist").toString());
        double supCost = map.get("supCost") == null ? null : Double.parseDouble(map.get("supCost").toString());
        double cost = map.get("cost") == null ? null : Double.parseDouble(map.get("cost").toString());
        String x_coor = map.get("x_coor") == null ? null : map.get("x_coor").toString();
        String y_coor = map.get("y_coor") == null ? null : map.get("y_coor").toString();
        double sortFlag = map.get("sortFlag") == null ? null : Double.parseDouble(map.get("sortFlag").toString());
//		@SuppressWarnings("unchecked")
//		HashMap<String, Object> routeMap = (HashMap<String, Object>) (map.get("route") == null ? null : map.get("route"));
//		JSONObject route = new JSONObject(routeMap);
        WPort wp = new WPort("WPort", pname, carryRate, esTime, dist, supCost, x_coor, y_coor, sortFlag);
        return wp;
    }

}
