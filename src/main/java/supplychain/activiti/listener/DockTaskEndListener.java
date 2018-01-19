package supplychain.activiti.listener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbq.GlobalVariables;

import supplychain.entity.Location;
import supplychain.entity.VPort;

@Service("dockingTaskEndListener")
public class DockTaskEndListener implements ExecutionListener, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4885656684805353238L;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private GlobalVariables globalVariables;

	@Override
	public void notify(DelegateExecution exec) {
		// TODO Auto-generated method stub

		// 把港口列表中已经到达的港口的状态State == "InAD"
		String pid = exec.getProcessInstanceId();
		HashMap<String, Object> vars = (HashMap<String, Object>) runtimeService
				.getVariables(pid);
		VPort nextport = (VPort) vars.get("NextPort");
		@SuppressWarnings("unchecked")
		List<VPort> targLocList = (List<VPort>) vars.get("TargLocList");
		for (int i = 0; i < targLocList.size(); i++) {
			VPort now = targLocList.get(i);
			if (now.getPname().equals(nextport.getPname())) {
				runtimeService.setVariable(pid, "State", "BeforeAD");
				System.out.println(nextport.getPname()+" 到达，更新TargLocList完毕!");
			}
		}
		runtimeService.setVariable(pid ,"TargLocList", targLocList);
		runtimeService.setVariable(pid, "NextPort", nextport);
		globalVariables.createOrUpdateVariableByNameAndValue(pid, "TargLocList", targLocList);
		globalVariables.createOrUpdateVariableByNameAndValue(pid, "NextPort", nextport);
	}

}
