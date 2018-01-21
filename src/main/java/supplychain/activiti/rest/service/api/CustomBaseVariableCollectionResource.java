package supplychain.activiti.rest.service.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zbq.GlobalVariables;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.runtime.Execution;
import org.activiti.rest.exception.ActivitiConflictException;
import org.activiti.rest.service.api.RestResponseFactory;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.engine.variable.RestVariable.RestVariableScope;
import org.activiti.rest.service.api.runtime.process.BaseVariableCollectionResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CustomBaseVariableCollectionResource extends BaseVariableCollectionResource {
    @Autowired
    private GlobalVariables globalVariables;

    /*
     * Custom
     */
    public Object createOrUpdateExecutionVariable2(String processInstanceId, String body) {

        Execution execution = getProcessInstanceFromRequest(processInstanceId);
        return createExecutionVariable2(execution, true, RestResponseFactory.VARIABLE_PROCESS, body);
    }

    public Object createExecutionVariable2(Execution execution, boolean override, int variableType, String body) {

        Object result = null;
        List<RestVariable> inputVariables = new ArrayList<RestVariable>();
        List<RestVariable> resultVariables = new ArrayList<RestVariable>();
        result = resultVariables;

        try {

            @SuppressWarnings("unchecked")
            List<Object> variableObjects = (List<Object>) objectMapper.readValue(body, List.class);

            for (Object restObject : variableObjects) {
                RestVariable restVariable = objectMapper.convertValue(restObject, RestVariable.class);
                inputVariables.add(restVariable);
            }
        } catch (Exception e) {
            throw new ActivitiIllegalArgumentException("Failed to serialize to a RestVariable instance", e);
        }

        if (inputVariables == null || inputVariables.size() == 0) {
            throw new ActivitiIllegalArgumentException("Request didn't contain a list of variables to create.");
        }

        RestVariableScope sharedScope = null;
        RestVariableScope varScope = null;
        Map<String, Object> variablesToSet = new HashMap<String, Object>();

        for (RestVariable var : inputVariables) {
            // Validate if scopes match
            varScope = var.getVariableScope();
            if (var.getName() == null) {
                throw new ActivitiIllegalArgumentException("Variable name is required");
            }

            if (varScope == null) {
                varScope = RestVariableScope.LOCAL;
            }
            if (sharedScope == null) {
                sharedScope = varScope;
            }
            if (varScope != sharedScope) {
                throw new ActivitiIllegalArgumentException(
                        "Only allowed to update multiple variables in the same scope.");
            }

            if (!override && hasVariableOnScope(execution, var.getName(), varScope)) {
                throw new ActivitiConflictException("Variable '" + var.getName() + "' is already present on execution '"
                        + execution.getId() + "'.");
            }

            Object actualVariableValue = restResponseFactory.getVariableValue(var);
            variablesToSet.put(var.getName(), actualVariableValue);
            resultVariables.add(restResponseFactory.createRestVariable(var.getName(), actualVariableValue, varScope,
                    execution.getId(), variableType, false));
        }

        if (!variablesToSet.isEmpty()) {
            if (sharedScope == RestVariableScope.LOCAL) {
                runtimeService.setVariablesLocal(execution.getId(), variablesToSet);
            } else {
                if (execution.getParentId() != null) {
                    // Explicitly set on parent, setting non-local variables
                    // on execution itself will override local-variables if
                    // exists
                    runtimeService.setVariables(execution.getParentId(), variablesToSet);
                } else {
                    // Standalone task, no global variables possible
                    throw new ActivitiIllegalArgumentException("Cannot set global variables on execution '"
                            + execution.getId() + "', task is not part of process.");
                }
            }
        }

        return result;
    }

    /**
     * 创建或者更新多个变量到全局变量缓存 ， request中是RestVatiable
     *
     * @param execution
     * @param override
     * @param variableType
     * @param request
     * @param response
     * @return
     * @throws JsonProcessingException
     */
    public List<RestVariable> createExecutionVariableToCacheOrEngine(String processInstanceId, boolean override, int variableType,
                                                                     HttpServletRequest request, HttpServletResponse response,
                                                                     boolean isComplete) throws JsonProcessingException {
        List<RestVariable> inputVariables = new ArrayList<RestVariable>();
        List<RestVariable> resultVariables = new ArrayList<RestVariable>();
        try {
            @SuppressWarnings("unchecked")
            List<Object> variableObjects = (List<Object>) objectMapper.readValue(request.getInputStream(), List.class);
            for (Object restObject : variableObjects) {
                RestVariable restVariable = objectMapper.convertValue(restObject, RestVariable.class);
                inputVariables.add(restVariable);
            }
        } catch (Exception e) {
            throw new ActivitiIllegalArgumentException("Failed to serialize to a RestVariable instance", e);
        }

        if (inputVariables == null || inputVariables.size() == 0) {
            throw new ActivitiIllegalArgumentException("Request didn't contain a list of variables to create.");
        }
        if (!inputVariables.isEmpty()) {
            globalVariables.createOrUpdateVariablesByRestVars(processInstanceId, inputVariables);
            if (isComplete == true) {
                RestVariableScope sharedScope = null;
                RestVariableScope varScope = null;
                Map<String, Object> variablesToSet = new HashMap<String, Object>();
                Execution execution = getProcessInstanceFromRequest(processInstanceId);
                for (RestVariable var : inputVariables) {
                    // Validate if scopes match
                    varScope = var.getVariableScope();
                    if (var.getName() == null) {
                        throw new ActivitiIllegalArgumentException("Variable name is required");
                    }

                    if (varScope == null) {
                        varScope = RestVariableScope.LOCAL;
                    }
                    if (sharedScope == null) {
                        sharedScope = varScope;
                    }
                    if (varScope != sharedScope) {
                        throw new ActivitiIllegalArgumentException(
                                "Only allowed to update multiple variables in the same scope.");
                    }

                    if (!override && hasVariableOnScope(execution, var.getName(), varScope)) {
                        throw new ActivitiConflictException("Variable '" + var.getName()
                                + "' is already present on execution '" + execution.getId() + "'.");
                    }

                    Object actualVariableValue = restResponseFactory.getVariableValue(var);
                    variablesToSet.put(var.getName(), actualVariableValue);
                    resultVariables.add(restResponseFactory.createRestVariable(var.getName(), actualVariableValue, varScope,
                            execution.getId(), variableType, false));
                }
                if (sharedScope == RestVariableScope.LOCAL) {
                    runtimeService.setVariablesLocal(execution.getId(), variablesToSet);
                } else {
                    if (execution.getParentId() != null) {
                        // Explicitly set on parent, setting non-local variables
                        // on execution itself will override local-variables if
                        // exists
                        runtimeService.setVariables(execution.getParentId(), variablesToSet);
                    } else {
                        // Standalone task, no global variables possible
                        throw new ActivitiIllegalArgumentException("Cannot set global variables on execution '"
                                + execution.getId() + "', task is not part of process.");
                    }
                }
            }
        }

        response.setStatus(HttpStatus.CREATED.value());
        return inputVariables;
    }
/********************************************************************************************************************************/
    /**
     * 重载其他函数
     */
    @Override
    public Object createExecutionVariable(Execution execution, boolean override, int variableType,
                                          HttpServletRequest request, HttpServletResponse response) {

        Object result = null;
        if (request instanceof MultipartHttpServletRequest) {
            result = setBinaryVariable((MultipartHttpServletRequest) request, execution, variableType, true);
        } else {

            List<RestVariable> inputVariables = new ArrayList<RestVariable>();
            List<RestVariable> resultVariables = new ArrayList<RestVariable>();
            result = resultVariables;

            try {
                @SuppressWarnings("unchecked")
                List<Object> variableObjects = (List<Object>) objectMapper.readValue(request.getInputStream(),
                        List.class);
            } catch (Exception e) {
                throw new ActivitiIllegalArgumentException("Failed to serialize to a RestVariable instance", e);
            }

            if (inputVariables == null || inputVariables.size() == 0) {
                throw new ActivitiIllegalArgumentException("Request didn't contain a list of variables to create.");
            }

            RestVariableScope sharedScope = null;
            RestVariableScope varScope = null;
            Map<String, Object> variablesToSet = new HashMap<String, Object>();

            for (RestVariable var : inputVariables) {
                // Validate if scopes match
                varScope = var.getVariableScope();
                if (var.getName() == null) {
                    throw new ActivitiIllegalArgumentException("Variable name is required");
                }

                if (varScope == null) {
                    varScope = RestVariableScope.LOCAL;
                }
                if (sharedScope == null) {
                    sharedScope = varScope;
                }
                if (varScope != sharedScope) {
                    throw new ActivitiIllegalArgumentException(
                            "Only allowed to update multiple variables in the same scope.");
                }

                if (!override && hasVariableOnScope(execution, var.getName(), varScope)) {
                    throw new ActivitiConflictException("Variable '" + var.getName()
                            + "' is already present on execution '" + execution.getId() + "'.");
                }

                Object actualVariableValue = restResponseFactory.getVariableValue(var);
                variablesToSet.put(var.getName(), actualVariableValue);
                resultVariables.add(restResponseFactory.createRestVariable(var.getName(), actualVariableValue, varScope,
                        execution.getId(), variableType, false));
            }

            if (!variablesToSet.isEmpty()) {
                if (sharedScope == RestVariableScope.LOCAL) {
                    runtimeService.setVariablesLocal(execution.getId(), variablesToSet);
                } else {
                    if (execution.getParentId() != null) {
                        // Explicitly set on parent, setting non-local variables
                        // on execution itself will override local-variables if
                        // exists
                        runtimeService.setVariables(execution.getParentId(), variablesToSet);
                    } else {
                        // Standalone task, no global variables possible
                        throw new ActivitiIllegalArgumentException("Cannot set global variables on execution '"
                                + execution.getId() + "', task is not part of process.");
                    }
                }
            }
        }
        response.setStatus(HttpStatus.CREATED.value());
        return result;
    }
}
