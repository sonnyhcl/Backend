package supplychain.activiti.coord;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbq.GlobalVariables;

import supplychain.entity.Weagon;

import java.io.Serializable;
import java.util.HashMap;

@Service("SWCoordinator")
public class SWCoordinator implements JavaDelegate, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 5334846840309131394L;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
	 private GlobalVariables globalVariables;

    @Override
    public void execute(DelegateExecution exec) {
        // TODO Auto-generated method stubVWCoordinator.java
        HashMap<String, Object> msgData = (HashMap<String, Object>) runtimeService.getVariables(exec.getProcessInstanceId());
        String msgType = (String) msgData.get("msgType");
        if (msgType.equals("Msg_StartWeagon")) {
            Weagon w = new Weagon();
            w.setW_Name("weagon_1");
            w.setX_Coor(globalVariables.getSupLoc().getX_coor());
            w.setY_Coor(globalVariables.getSupLoc().getY_coor());
            //	w.setX_Coor("120.1551500000");
            //	w.setY_Coor("30.2741500000");
            w.setIsArrival(false);
            msgData.put("W_Info", w);
            msgData.remove("msgType");
            msgData.remove("M_pid");
            runtimeService.startProcessInstanceByMessage("Msg_StartWeagon", msgData);
            System.out.println("Weagon流程实例已启动");
        }

    }

}
