package supplychain.activiti.listener;

import com.zbq.EventType;
import com.zbq.GlobalVariables;
import com.zbq.VWFEvent;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import supplychain.entity.VPort;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service("dockingTaskEndListener")
public class DockTaskEndListener implements ExecutionListener, Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 4885656684805353238L;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private GlobalVariables globalVariables;

    @Override
    public void notify(DelegateExecution exec) {
        // TODO Auto-generated method stub

        // 把港口列表中已经到达的港口的状态State == "InAD"
        String pid = exec.getProcessInstanceId();
        HashMap<String, Object> vars = (HashMap<String, Object>) runtimeService
                .getVariables(pid);
        VPort preport = (VPort) vars.get("PrePort");
      
        @SuppressWarnings("unchecked")
        List<VPort> targLocList = (List<VPort>) vars.get("TargLocList");
        boolean isMissing = true;
        for (int i = 0; i < targLocList.size(); i++) {
            VPort now = targLocList.get(i);
            if (now.getPname().equals(preport.getPname())) {
                now.setState("BeforeAD");
				targLocList.set(i, now);
                System.out.println(preport.getPname() + " 到达，更新TargLocList完毕!");
            }
            
            if(now.getState().equals("AfterAD")) {
            	if(now.getIsMeetWeightCond() == true) {
            		isMissing = false;
            	}
            }
        }
        runtimeService.setVariable(pid, "TargLocList", targLocList);
        runtimeService.setVariable(pid, "PrePort",preport);
        globalVariables.createOrUpdateVariableByNameAndValue(pid, "TargLocList", targLocList);
        globalVariables.createOrUpdateVariableByNameAndValue(pid, "PrePort", preport);
        if(isMissing == true) {
        	VWFEvent e = new VWFEvent(EventType.W_RUN);
			e.getData().put("createAt", (new Date()).toString());
    		runtimeService.setVariable(pid,"isMissing", true);
			e.getData().put("State", "Missing");
        }
    }

}
