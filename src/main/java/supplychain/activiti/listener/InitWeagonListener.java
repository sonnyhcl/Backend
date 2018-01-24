package supplychain.activiti.listener;

import com.zbq.GlobalVariables;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import supplychain.entity.Location;
import supplychain.entity.WPort;
import supplychain.entity.Weagon;

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
		System.out.println("\033[33;1m 初始化Weagon : \033[0m" + runtimeService);
		 Map<String, Object> vars = new HashMap<String, Object>();
		 String pid = dExe.getProcessInstanceId();
		 Weagon w = (Weagon) runtimeService.getVariable(pid, "W_Info");
		 w.setPid(pid);
		 vars.put("W_Info", w);
		 vars.put("DestPort" , new WPort()); 
		 runtimeService.setVariable(pid, "isArriving", false);
		 runtimeService.setVariable(pid, "W_Info" , w);
		 
		 globalVariables.createOrUpdateVariablesByValue(pid, vars);
	}

}
