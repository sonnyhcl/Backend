package supplychain.activiti.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service("sendMsgToVWC")
public class SendMsgToVWC implements ExecutionListener, Serializable {

    @Override
    public void notify(DelegateExecution execution) {
        // TODO ETime Change
        //
    }

}
