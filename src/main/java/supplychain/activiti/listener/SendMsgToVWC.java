package supplychain.activiti.listener;

import java.io.Serializable;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Service;

@Service("sendMsgToVWC")
public class SendMsgToVWC implements ExecutionListener, Serializable {

	@Override
	public void notify(DelegateExecution execution) {
		// TODO ETime Change
		//
	}

}
