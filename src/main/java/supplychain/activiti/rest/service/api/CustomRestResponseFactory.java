package supplychain.activiti.rest.service.api;


import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.rest.service.api.RestResponseFactory;
import org.activiti.rest.service.api.RestUrlBuilder;
import org.activiti.rest.service.api.RestUrls;
import org.activiti.rest.service.api.engine.variable.*;
import org.activiti.rest.service.api.engine.variable.RestVariable.RestVariableScope;

import java.util.*;
import java.util.Map.Entry;

public class CustomRestResponseFactory extends RestResponseFactory {
    /**
     * Called once when the converters need to be initialized. Override of custom
     * conversion needs to be done between java and rest.
     */
    protected void initializeVariableConverters() {
        variableConverters.add(new LocationRestVariableConverter());
        variableConverters.add(new WeagonRestVariableConverter());
        variableConverters.add(new VPortRestVariableConverter());
        variableConverters.add(new WPortRestVariableConverter());
        variableConverters.add(new StringRestVariableConverter());
        variableConverters.add(new IntegerRestVariableConverter());
        variableConverters.add(new LongRestVariableConverter());
        variableConverters.add(new ShortRestVariableConverter());
        variableConverters.add(new DoubleRestVariableConverter());
        variableConverters.add(new BooleanRestVariableConverter());
        variableConverters.add(new CustomDateRestVariableConverter());
        variableConverters.add(new CustomArrayListRestVariableConverter());
    }

    public Object getVariableValue(RestVariable restVariable) {
        Object value = null;

        if (restVariable.getType() != null) {
            // Try locating a converter if the type has been specified
            RestVariableConverter converter = null;
            for (RestVariableConverter conv : variableConverters) {
//				System.out.println("getClass :" + value.getClass());
//				System.out.println("getVariableType ： "+conv.getVariableType());
                if (conv.getRestTypeName().equals(restVariable.getType())) {
                    converter = conv;
                    break;
                }
            }
            if (converter == null) {
                throw new ActivitiIllegalArgumentException("Variable '" + restVariable.getName()
                        + "' has unsupported type: '" + restVariable.getType() + "'.");
            }
            value = converter.getVariableValue(restVariable);

        } else {
            // Revert to type determined by REST-to-Java mapping when no
            // explicit type has been provided
            value = restVariable.getValue();
        }
        return value;
    }

    public List<RestVariable> createRestVariables(Map<String, Object> variables, String id, int variableType,
                                                  RestVariableScope scope) {
        RestUrlBuilder urlBuilder = createUrlBuilder();
        List<RestVariable> result = new ArrayList<RestVariable>();

        for (Entry<String, Object> pair : variables.entrySet()) {
            result.add(createRestVariable(pair.getKey(), pair.getValue(), scope, id, variableType, false, urlBuilder));
        }

        return result;
    }

    public RestVariable createRestVariable(String name, Object value, RestVariableScope scope, String id, int variableType,
                                           boolean includeBinaryValue, RestUrlBuilder urlBuilder) {

        RestVariableConverter converter = null;
        RestVariable restVar = new RestVariable();
        restVar.setVariableScope(scope);
        restVar.setName(name);

        if (value != null) {
            // Try converting the value
            for (RestVariableConverter c : variableConverters) {
//				System.out.println("getClass :" + value.getClass());
//				System.out.println("getVariableType ： "+c.getVariableType());
                if (c.getVariableType().isAssignableFrom(value.getClass())) {
                    converter = c;
                    break;
                }

                if (value instanceof LinkedHashMap) {
                    @SuppressWarnings("unchecked")
                    HashMap<String, Object> mp = (HashMap<String, Object>) value;
                    if (c.getRestTypeName().equals(mp.get("name"))) {
                        converter = c;
                        break;
                    }
                }

                if (value instanceof ArrayList && c.getRestTypeName().equals("ArrayList")) {
                    converter = c;
                    break;
                }
//				// 单独判断
//				if (("Loc".equals(name))|| ("NowLoc".equals(name) || "V_TargLoc".equals(name) || "NextPort".equals(name) ||
// "PrePort".equals(name) )
//						&& c.getRestTypeName().equals("Location")) {
//					converter = c;
//					break;
//				}
                if (("apply_time".equals(name) || "StartTime".equals(name)) && c.getRestTypeName().equals("date")) {
                    converter = c;
                    break;
                }
//				if("W_Info".equals(name) && c.getRestTypeName().equals("Weagon")) {
//					converter = c;
//					break;
//				}
            }

            if (converter != null) {
                converter.convertVariableValue(value, restVar);
                restVar.setType(converter.getRestTypeName());
            } else {
                // Revert to default conversion, which is the
                // serializable/byte-array form
                if (value instanceof Byte[] || value instanceof byte[]) {
                    restVar.setType(BYTE_ARRAY_VARIABLE_TYPE);
                } else {
                    restVar.setType(SERIALIZABLE_VARIABLE_TYPE);
                }

                if (includeBinaryValue) {
                    restVar.setValue(value);
                }

                if (variableType == VARIABLE_TASK) {
                    restVar.setValueUrl(urlBuilder.buildUrl(RestUrls.URL_TASK_VARIABLE_DATA, id, name));
                } else if (variableType == VARIABLE_EXECUTION) {
                    restVar.setValueUrl(urlBuilder.buildUrl(RestUrls.URL_EXECUTION_VARIABLE_DATA, id, name));
                } else if (variableType == VARIABLE_PROCESS) {
                    restVar.setValueUrl(urlBuilder.buildUrl(RestUrls.URL_PROCESS_INSTANCE_VARIABLE_DATA, id, name));
                } else if (variableType == VARIABLE_HISTORY_TASK) {
                    restVar.setValueUrl(
                            urlBuilder.buildUrl(RestUrls.URL_HISTORIC_TASK_INSTANCE_VARIABLE_DATA, id, name));
                } else if (variableType == VARIABLE_HISTORY_PROCESS) {
                    restVar.setValueUrl(
                            urlBuilder.buildUrl(RestUrls.URL_HISTORIC_PROCESS_INSTANCE_VARIABLE_DATA, id, name));
                } else if (variableType == VARIABLE_HISTORY_VARINSTANCE) {
                    restVar.setValueUrl(urlBuilder.buildUrl(RestUrls.URL_HISTORIC_VARIABLE_INSTANCE_DATA, id));
                } else if (variableType == VARIABLE_HISTORY_DETAIL) {
                    restVar.setValueUrl(urlBuilder.buildUrl(RestUrls.URL_HISTORIC_DETAIL_VARIABLE_DATA, id));
                }
            }
        }
        return restVar;
    }

}
