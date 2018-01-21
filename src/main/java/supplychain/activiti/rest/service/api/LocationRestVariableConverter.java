package supplychain.activiti.rest.service.api;

import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.engine.variable.RestVariableConverter;
import supplychain.entity.Location;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;

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
            if (result.getValue() instanceof LinkedHashMap) {
                @SuppressWarnings("unchecked")
                HashMap<String, Object> mp = (HashMap<String, Object>) result.getValue();
                try {
                    Location L = Map2Location(mp);
                    return L;
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
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

    public Location Map2Location(HashMap<String, Object> map) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String lname = map.get("lname") == null ? null : (String) map.get("lname");
        String x_coor = map.get("x_coor") == null ? null : map.get("x_coor").toString();
        String y_coor = map.get("y_coor") == null ? null : map.get("y_coor").toString();
        String velocity = map.get("velocity") == null ? null : (String) map.get("velocity");
        String realTime = map.get("realTime") == null ? null : map.get("realTime").toString();
        Location L = new Location("Location", lname, x_coor, y_coor, velocity, realTime);
        return L;
    }
}
