package supplychain.activiti.coord;

import java.io.Serializable;
import java.util.HashMap;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("MSCoordinator")
public class MSCoordinator implements JavaDelegate, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5334846840309131394L;
	@Autowired
	private RuntimeService runtimeService;
	
	@Override
	public void execute(DelegateExecution exec) {
		// TODO Auto-generated method stubVWCoordinator.java
		HashMap<String , Object>  mp = (HashMap<String, Object>) runtimeService.getVariables(exec.getId());
		// 消息启动Supplier流程
		runtimeService.startProcessInstanceByMessage("Msg_StartSupplier" , mp);
		System.out.println("Supplier流程实例已启动");
	}

}
