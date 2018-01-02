package supplychain.activiti.listener;

import java.io.Serializable;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("anchorStartListener")
public class AnchorStartListener implements ExecutionListener, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4149621500319226872L;
	@Autowired
	private RuntimeService runtimeService;
	@Override
	public void notify(DelegateExecution execution) {
		// TODO Auto-generated method stub
		runtimeService.setVariable(execution.getId(), "State" , "other");
	}

}
