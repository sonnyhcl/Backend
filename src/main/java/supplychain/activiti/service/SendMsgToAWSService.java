package supplychain.activiti.service;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service("sendMsgToAWSService")
public class SendMsgToAWSService implements JavaDelegate, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -4786358918252550905L;

    @Override
    public void execute(DelegateExecution execution) {
        // TODO Auto-generated method stub
        //写入数据到AWS
        System.out.println("-----------------Send message to AWS--------------------");
    }

}
