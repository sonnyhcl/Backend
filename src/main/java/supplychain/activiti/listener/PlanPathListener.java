package supplychain.activiti.listener;


import java.io.Serializable;
import java.util.Date;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.util.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbq.GlobalEventQueue;
import com.zbq.VWFEvent;
import com.zbq.EventType;

import supplychain.entity.Weagon;

@Service("planPathListener")
public class PlanPathListener implements TaskListener, Serializable {

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
		System.out.println("\033[33;1m 正在重新规划路径 \033[0m");
		String pid = exec.getProcessInstanceId();
		Weagon w = (Weagon) runtimeService.getVariable(pid, "W_Info");
		VWFEvent e = new VWFEvent(EventType.W_PLAN);
		e.getData().put("createAt", (new Date()).toString());
		JSONObject wjson = new JSONObject(w);
		e.getData().put("W_Info", wjson);
		try {
			System.out.println("Send a W_PLAN message");
			globalEventQueue.getSendQueue().put(e);
		} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
		}
	}
}

