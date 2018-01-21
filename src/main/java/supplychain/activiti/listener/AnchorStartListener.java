package supplychain.activiti.listener;

import com.zbq.GlobalVariables;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.util.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbq.EventType;
import com.zbq.GlobalVariables;
import com.zbq.VWFEvent;

import supplychain.entity.VPort;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service("anchStartListener")
public class AnchorStartListener implements ExecutionListener, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4149621500319226872L;
	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private GlobalVariables globalVariables;
	@Override
	public void notify(DelegateExecution exec) {
		// TODO Auto-generated method stub
		String pid = exec.getProcessInstanceId();
		//runtimeService.setVariable(pid , "State" , "voyaging");
		HashMap<String, Object> vars = (HashMap<String, Object>) runtimeService
				.getVariables(pid);
		VPort nextport = (VPort) vars.get("NextPort");
		@SuppressWarnings("unchecked")
		List<VPort> targLocList = (List<VPort>) vars.get("TargLocList");
		for (int i = 0; i < targLocList.size(); i++) {
			VPort now = targLocList.get(i);
			if (now.getPname().equals(nextport.getPname())) {
				runtimeService.setVariable(pid, "State", "InAD");
				System.out.println(nextport.getPname()+" 到达，更新TargLocList完毕!");
			}
		}
		runtimeService.setVariable(pid ,"TargLocList", targLocList);
		runtimeService.setVariable(pid, "NextPort", nextport);
		globalVariables.createOrUpdateVariableByNameAndValue(pid, "TargLocList", targLocList);
		globalVariables.createOrUpdateVariableByNameAndValue(pid, "NextPort", nextport);
	//	globalVariables.createOrUpdateVariableByNameAndValue(pid,  "State" , "a");
//		VWFEvent e = new VWFEvent(EventType.V_AnchorStart);
//		e.getData().put("createAt", (new Date()).toString());
//		e.getData().put("achorSta", );
//		globalEventQueue.sendMsg(e);
		System.out.println("进入anchoring : " + new Date());
	}


}
