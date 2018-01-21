package supplychain.activiti.listener;

import com.zbq.GlobalVariables;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Date;


@Service("voyaTaskStartListener")
public class VoyaTaskStartListener implements ExecutionListener, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 4149621500319226872L;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private GlobalVariables globalVariables;

    @Override
    public void notify(DelegateExecution execution) {
        // TODO Auto-generated method stub
        runtimeService.setVariable(execution.getId(), "State", "voyaging");

        //修改某个流程实例中的变量
        String pid = execution.getProcessInstanceId();
        globalVariables.createOrUpdateVariableByNameAndValue(pid, "State", "voyaging");

        System.out.println("进入Voyaging: " + new Date());
    }

}
