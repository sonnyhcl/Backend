package supplychain.activiti.listener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import supplychain.entity.Weagon;

@Service("initWeagonListener")
public class InitWeagonListener implements ExecutionListener, Serializable {

	private static final long serialVersionUID = -51948726954754158L;
	@Autowired
	private RuntimeService runtimeService;
	@Override
	public void notify(DelegateExecution dExe) {
		// TODO Auto-generated method stub
		System.out.println("\033[33;1m 初始化Weagon : \033[0m" + runtimeService);
		 Map<String, Object> vars = new HashMap<String, Object>();
		 Weagon W_Info = new Weagon();
		 W_Info.setPlanRes(-1);
		 vars.put("W_Info", W_Info);
		 runtimeService.setVariables(dExe.getId(), vars);
	}
}
