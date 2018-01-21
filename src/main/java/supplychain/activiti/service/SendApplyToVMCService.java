package supplychain.activiti.service;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import supplychain.entity.Location;
import supplychain.entity.Weagon;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Service("sendApplyToVMCService")
public class SendApplyToVMCService implements TaskListener, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1855827506708169338L;
    @Autowired
    RuntimeService runtimeService;

    @Override
    public void notify(DelegateTask dt) {
        // TODO Auto-generated method stub
        System.out.println("---------------Send Application to VMC------------------");
        System.out.println("Apply_Time : " + runtimeService.getVariable(dt.getExecutionId(), "appy_time"));
        Map<String, Object> vars = new HashMap<String, Object>();
        Weagon W_Info = new Weagon();
        Location tloc = new Location("南京", "118.800095", "32.146214");
        W_Info.setW_TargLoc(tloc);
        W_Info.setW_Name("杭州");
        W_Info.setX_Coor("120.1958370209");
        W_Info.setY_Coor("30.2695940578");
        W_Info.setPlanRes(1);
        W_Info.setNeedPlan(true);
        vars.put("W_Info", W_Info);
        runtimeService.startProcessInstanceByMessage("Msg_StartWeagon", vars);
    }

}
