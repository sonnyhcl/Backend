package supplychain.activiti.service;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service("sendMsgToWVCService")
public class SendMsgToWVCService implements JavaDelegate, Serializable {

    @Override
    public void execute(DelegateExecution execution) {
        // TODO Auto-generated method stub
        System.out.println("------------Send message to WVC  from weagon -------------------");
    }

}
