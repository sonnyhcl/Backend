package supplychain.web;

import org.activiti.app.model.runtime.CreateProcessInstanceRepresentation;
import org.activiti.app.model.runtime.ProcessInstanceRepresentation;
import org.activiti.app.rest.runtime.AbstractProcessInstancesResource;
import org.activiti.app.service.api.UserCache.CachedUser;
import org.activiti.app.service.exception.BadRequestException;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.form.model.FormDefinition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ProcessInstancesResource extends AbstractProcessInstancesResource {

    @RequestMapping(value = "/rest/process-instances", method = RequestMethod.POST)
    public ProcessInstanceRepresentation startNewProcessInstance(@RequestBody CreateProcessInstanceRepresentation startRequest) {
        return MyStartNewProcessInstance(startRequest);
    }

    public ProcessInstanceRepresentation MyStartNewProcessInstance(CreateProcessInstanceRepresentation startRequest) {
        if (StringUtils.isEmpty(startRequest.getProcessDefinitionId())) {
            throw new BadRequestException("Process definition id is required");
        }

        FormDefinition formDefinition = null;
        Map<String, Object> variables = null;

        ProcessDefinition processDefinition = permissionService.getProcessDefinitionById(startRequest.getProcessDefinitionId());

        if (startRequest.getValues() != null || startRequest.getOutcome() != null) {
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
            Process process = bpmnModel.getProcessById(processDefinition.getKey());
            FlowElement startElement = process.getInitialFlowElement();
            if (startElement instanceof StartEvent) {
                StartEvent startEvent = (StartEvent) startElement;
                if (StringUtils.isNotEmpty(startEvent.getFormKey())) {
                    formDefinition = formRepositoryService.getFormDefinitionByKey(startEvent.getFormKey());
                    if (formDefinition != null) {
                        variables = formService.getVariablesFromFormSubmission(formDefinition, startRequest.getValues(),
                                startRequest.getOutcome());
                    }
                }
            }
        }

        if (variables == null) {
            variables = startRequest.getValues();
        }

        ProcessInstance processInstance = activitiService.startProcessInstance(startRequest.getProcessDefinitionId(),
                variables, startRequest.getName());

        // Mark any content created as part of the form-submission connected to the process instance
            /*if (formSubmission != null) {
		      if (formSubmission.hasContent()) {
		        ObjectNode contentNode = objectMapper.createObjectNode();
		        submittedFormValuesJson.put("content", contentNode);
		        for (Entry<String, List<RelatedContent>> entry : formSubmission.getVariableContent().entrySet()) {
		          ArrayNode contentArray = objectMapper.createArrayNode();
		          for (RelatedContent content : entry.getValue()) {
		            relatedContentService.setContentField(content.getId(), entry.getKey(), processInstance.getId(), null);
		            contentArray.add(content.getId());
		          }
		          contentNode.put(entry.getKey(), contentArray);
		        }
		      }*/

        HistoricProcessInstance historicProcess = historyService.createHistoricProcessInstanceQuery().processInstanceId
                (processInstance.getId()).singleResult();

        if (formDefinition != null) {
            formService.storeSubmittedForm(variables, formDefinition, null, historicProcess.getId());
        }

        User user = null;
        if (historicProcess.getStartUserId() != null) {
            CachedUser cachedUser = userCache.getUser(historicProcess.getStartUserId());
            if (cachedUser != null && cachedUser.getUser() != null) {
                user = cachedUser.getUser();
            }
        }
        return new ProcessInstanceRepresentation(historicProcess, processDefinition, ((ProcessDefinitionEntity) processDefinition).isGraphicalNotationDefined(), user);

    }
}

