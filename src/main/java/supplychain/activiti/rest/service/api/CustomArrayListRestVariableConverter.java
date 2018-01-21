package supplychain.activiti.rest.service.api;

import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.engine.variable.RestVariableConverter;
import supplychain.entity.VPort;
import supplychain.entity.WPort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        if (result.getValue() != null) {
            @SuppressWarnings("unchecked")
            List<HashMap<String, Object>> resList = (List<HashMap<String, Object>>) result.getValue();
            if (resList.get(0).get("name").equals("VPort")) {
                return Map2VPortList(resList);
            } else if (resList.get(0).get("name").equals("WPort")) {
                return Map2WPortList(resList);
            } else {
                System.out.println("ArrayList getVariableValur fail!");
                return null;
            }

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

    public List<VPort> Map2VPortList(List<HashMap<String, Object>> resList) {
        // TODO Auto-generated method stub
        if (resList != null) {
            List<VPort> list = new ArrayList<VPort>();
            VPortRestVariableConverter vpc = new VPortRestVariableConverter();
            for (int i = 0; i < resList.size(); i++) {
                VPort nowVp = vpc.Map2VPort(resList.get(i));
                list.add(nowVp);
            }
            return list;
        }
        return null;
    }

    public List<WPort> Map2WPortList(List<HashMap<String, Object>> resList) {
        // TODO Auto-generated method stub
        if (resList != null) {
            List<WPort> list = new ArrayList<WPort>();
            WPortRestVariableConverter vpc = new WPortRestVariableConverter();
            for (int i = 0; i < resList.size(); i++) {
                WPort nowWp = vpc.Map2WPort(resList.get(i));
                list.add(nowWp);
            }
            return list;
        }
        return null;
    }

}
