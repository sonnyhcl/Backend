package supplychain.activiti.listener;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import supplychain.entity.VPort;

@Service("anchStartListener")
public class AnchorStartListener implements ExecutionListener, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4149621500319226872L;
	@Autowired
	private RuntimeService runtimeService;
	@Override
	public void notify(DelegateExecution exec) {
		// TODO Auto-generated method stub
		String pid = exec.getProcessInstanceId();
		runtimeService.setVariable(pid , "State" , "other");
		//runtimeService.setVariable(pid, "ADRealStartTime", new Date());
		HashMap<String, Object> vars = (HashMap<String, Object>) runtimeService
				.getVariables(pid);
		VPort preport = (VPort) vars.get("PrePort");
		@SuppressWarnings("unchecked")
		List<VPort> targLocList = (List<VPort>) vars.get("TargLocList");
		for (int i = 0; i < targLocList.size(); i++) {
			VPort now = targLocList.get(i);
			if (now.getPname().equals(preport.getPname())) {
				runtimeService.setVariable(pid, "State", "InAD");
				System.out.println(preport.getPname()+" 到达，更新TargLocList完毕!");
			}
		}
		vars.put("TargLocList", targLocList);
	}

}
