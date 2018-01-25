package supplychain.activiti.listener;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.HashMap;


@Service("RecvMsgFromLambdaListener")
public class RecvMsgFromLambdaListener implements JavaDelegate, Serializable {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RuntimeService runtimeService;

    @Override
    public void execute(DelegateExecution exec) {
        System.out.println("我就试试行不行");
        HashMap<String, Object> vars = (HashMap<String, Object>) runtimeService.getVariables(exec.getProcessInstanceId());
        System.out.println(vars.toString());
        System.out.println(vars.get("key1"));
        System.out.println(vars.get("key2"));
        System.out.println(vars.get("key3"));
        System.out.println(vars.get("status"));
        System.out.println("Done.");
    }
}

