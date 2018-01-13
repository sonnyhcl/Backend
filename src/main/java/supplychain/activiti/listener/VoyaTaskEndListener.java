package supplychain.activiti.listener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import supplychain.entity.Location;
import supplychain.entity.VPort;

@Service("voyaTaskEndListener")
public class VoyaTaskEndListener implements ExecutionListener, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4885656684805353238L;
	@Autowired
	private RuntimeService runtimeService;

	@Override
	public void notify(DelegateExecution exec) {
		// TODO Auto-generated method stub

		// 把港口列表中已经到达的港口的状态isArrival置为true
		HashMap<String, Object> vars = (HashMap<String, Object>) runtimeService
				.getVariables(exec.getProcessInstanceId());
		Location preport = (Location) vars.get("PrePort");
		@SuppressWarnings("unchecked")
		List<VPort> targLocList = (List<VPort>) vars.get("V_TargetLocationList");
		for (int i = 0; i < targLocList.size(); i++) {
			VPort now = targLocList.get(i);
			if (now.getPname().equals(preport.getLname())) {
				now.setState("InAD");
				targLocList.set(i, now);
				System.out.println(preport.getLname()+" 到达，更新TargLocList完毕!");
			}
		}
	}

}
