package supplychain.activiti.listener;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service("flowIntoVoyaListener")
public class FlowIntoVoyaListener implements ExecutionListener, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6366572000725423648L;
    @Autowired
    private RuntimeService runtimeService;

    @Override
    public void notify(DelegateExecution execution) {
        // TODO Auto-generated method stub
        //runtimeService.setVariable(execution.getId(), "StartTime" , new Date());
    }

}
