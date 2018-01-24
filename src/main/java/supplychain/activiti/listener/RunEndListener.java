package supplychain.activiti.listener;


import com.zbq.GlobalEventQueue;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;

@Service("runEndListener")
public class RunEndListener implements TaskListener, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8770010597954761639L;

	@Autowired
	private RuntimeService runtimeService ;
	@Autowired
	private GlobalEventQueue globalEventQueue;
	@Override
	public void notify(DelegateTask exec) {
		// TODO Auto-generated method stub
		String pid = exec.getProcessInstanceId();
		HashMap<String, Object> vars = (HashMap<String, Object>) runtimeService
                .getVariables(pid);
		System.out.println(pid);
		boolean isArriving = false;
	    if(vars.get("isArriving") != null) {
	    	isArriving = (Boolean) vars.get("isArriving");
	    	runtimeService.setVariable(pid, "isArriving", isArriving);
	     }
	      
	}

}

