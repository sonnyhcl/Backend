package supplychain.activiti.listener;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import supplychain.entity.WPort;
import supplychain.entity.Weagon;
import supplychain.global.GlobalVariables;

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
        System.out.println("初始化Wagon:" + runtimeService);
        Map<String, Object> vars = new HashMap<String, Object>();
        String pid = dExe.getProcessInstanceId();

        @SuppressWarnings("unchecked")
        HashMap<String, Object> W_Info = (HashMap<String, Object>)runtimeService.getVariable(pid, "W_Info");
        System.out.println(runtimeService.getVariable(pid, "W_Info").toString());

        Weagon w = new Weagon();
        w.setW_Name(W_Info.get("W_Name").toString());
        w.setIsArrival((boolean)W_Info.get("isArrival"));
        w.setX_Coor(W_Info.get("X_Coor").toString());
        w.setY_Coor(W_Info.get("Y_Coor").toString());
        w.setPid(pid);

        vars.put("W_Info", w);
        vars.put("DestPort", new WPort());
        vars.put("W_TargLocList", runtimeService.getVariable(pid, "W_TargLocList"));
        globalVariables.createOrUpdateVariablesByValue(pid, vars);

        runtimeService.setVariable(pid, "isArriving", false);
        runtimeService.setVariable(pid, "W_Info", w);
        System.out.println("init Wagon Listener done....\n");
    }

}
