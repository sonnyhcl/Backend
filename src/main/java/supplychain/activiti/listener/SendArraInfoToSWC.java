package supplychain.activiti.listener;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;

@Service("sendArraInfoToSWC")
public class SendArraInfoToSWC implements ExecutionListener, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -7591079159186056195L;

    @Autowired
    private RuntimeService runtimeService;

    @Override
    public void notify(DelegateExecution exec) {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub
        // TODO : 将当前的Arranging数据发送给SWC
        String spid = exec.getProcessInstanceId();
        HashMap<String, Object> vars = (HashMap<String, Object>) runtimeService
                .getVariables(spid);
        vars.put("S_pid", spid);
        vars.put("msgType", "Msg_StartWeagon");
        // 启动Supplier Process
        runtimeService.startProcessInstanceByMessage("Msg_StartSWC", vars);
        System.out.println("SWC实例已启动");
    }

}
