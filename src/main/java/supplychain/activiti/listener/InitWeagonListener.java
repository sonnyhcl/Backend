package supplychain.activiti.listener;

import com.zbq.GlobalVariables;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import supplychain.entity.Location;
import supplychain.entity.WPort;
import supplychain.entity.Weagon;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Service("initWeagonListener")
public class InitWeagonListener implements ExecutionListener, Serializable {

    private static final long serialVersionUID = -51948726954754158L;
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private GlobalVariables globalVariables;

    @Override
    public void notify(DelegateExecution dExe) {
        // TODO Auto-generated method stub
        System.out.println("\033[33;1m 初始化Weagon : \033[0m" + runtimeService);
        Map<String, Object> vars = new HashMap<String, Object>();
        String pid = dExe.getProcessInstanceId();
        Weagon w = (Weagon) runtimeService.getVariable(pid, "W_Info");
        w.setPid(pid);
        w.setW_TargLoc(new Location("南京", "118.800095", "32.146214"));
        vars.put("W_Info", w);
        vars.put("DestPort", new WPort());
        runtimeService.setVariable(pid, "W_Info", w);

        globalVariables.createOrUpdateVariablesByValue(pid, vars);

        //Send message to VWC to connect to vessel
//		HashMap<String, Object> connVMData = new HashMap<String , Object>();
//		connVMData.put("W_pid" , pid);
//		String vpid = (String) runtimeService.getVariable(pid, "V_pid");
//		connVMData.put("V_pid" , vpid);
//		connVMData.put("msgType" , "msg_CreateVWConn");
        //runtimeService.startProcessInstanceByMessage("Msg_StartVWC" ,connVMData);
//		System.out.println("Send  Msg_StartVWC message to VWC to connect to vessel");
    }
}
