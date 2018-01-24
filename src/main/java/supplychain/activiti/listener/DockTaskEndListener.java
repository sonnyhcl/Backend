package supplychain.activiti.listener;

import com.zbq.EventType;
import com.zbq.GlobalEventQueue;
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
	 @Autowired
	 private GlobalEventQueue globalEventQueue;

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
        boolean isMissing = false;
        int lastValidId = targLocList.size()-1;
        if(vars.get("lastValidId") != null) {
        	lastValidId = (int) vars.get("lastValidId");
        	System.out.println("dock end lastId: "+lastValidId);
        }
        String dpName = null;
        if(vars.get("dpName") != null) {
        	dpName = (String) vars.get("dpName");
        }
        boolean isMeet = false;
        for (int i = 0; i < targLocList.size(); i++) {
            VPort now = targLocList.get(i);
            if (now.getPname().equals(preport.getPname())) {
                now.setState("BeforeAD");
				targLocList.set(i, now);
                System.out.println(preport.getPname() + " 到达，更新TargLocList完毕!");
                if(preport.getPname().equals(dpName)) {
                	runtimeService.setVariable(pid, "isMeet", true);
                	isMeet = true;
                }
                if(lastValidId == i){
                	isMissing = true;
                }
            }
            
            System.out.println(now.toString());
        }
        runtimeService.setVariable(pid, "TargLocList", targLocList);
        runtimeService.setVariable(pid, "PrePort",preport);
        globalVariables.createOrUpdateVariableByNameAndValue(pid, "TargLocList", targLocList);
        globalVariables.createOrUpdateVariableByNameAndValue(pid, "PrePort", preport);
  
       
        if(isMissing == true && isMeet == false) { //在没有交货的时候，发现过了所有有效港口才需要发送Missing 消息
        	System.out.println("----------------Missing----------");
        	VWFEvent e = new VWFEvent(EventType.W_RUN);
			e.getData().put("createAt", (new Date()).toString());
    		runtimeService.setVariable(pid,"isMissing", true);
			e.getData().put("State", "Missing");
			globalEventQueue.sendMsg(e);
        }
        if(isMeet == true) {
        	System.out.println("----------------Meeting----------");
        	VWFEvent e = new VWFEvent(EventType.W_RUN);
			e.getData().put("createAt", (new Date()).toString());
			e.getData().put("State", "Meeting");
			globalEventQueue.sendMsg(e);
        }
    }

}
