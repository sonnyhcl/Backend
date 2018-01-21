package supplychain.activiti.coord;

import com.zbq.GlobalVariables;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;

@Service("VMCoordinator")
public class VMCoordinator implements JavaDelegate, Serializable {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private GlobalVariables globalVariables;

    /**
     *
     */
    private static final long serialVersionUID = 5334846840309131394L;

    @Override
    public void execute(DelegateExecution exec) {
        // TODO Auto-generated method stubVWCoordinator.java

        //TODO : VMC Locgic --- 取出所有变量转发给Manager流程
        HashMap<String, Object> msgData = (HashMap<String, Object>) runtimeService.getVariables(exec.getId());
        String msgType = (String) msgData.get("msgType");
        if (msgType.equals("Msg_StartMana")) {
            //TODO : Start Manager
            msgData.remove("msgType");
            runtimeService.startProcessInstanceByMessage("Msg_StartMana", msgData);
            System.out.println("Manager流程实例已启动");
        }
    }
}
