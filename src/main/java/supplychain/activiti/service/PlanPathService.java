package supplychain.activiti.service;


import java.io.Serializable;
import java.util.Date;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.util.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbq.GlobalEventQueue;
import com.zbq.VWFEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zbq.EventType;


@Service("planPathService")
public class PlanPathService implements JavaDelegate, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8770010597954761639L;

	@Autowired
	private RuntimeService runtimeService ;

	@Autowired
	private GlobalEventQueue globalEventQueue;

	@Override
	public void execute(DelegateExecution exec) {
		// TODO Auto-generated method stub
		System.out.println("\033[33;1m 正在重新规划路径 \033[0m");
		String pid = exec.getProcessInstanceId();
		Object w = runtimeService.getVariable(pid, "W_Info");
		VWFEvent e = new VWFEvent(EventType.W_PLAN);
		e.getData().put("createAt", (new Date()).toString());
		JSONObject wjson = new JSONObject(w);
		e.getData().put("W_Info", wjson);
		System.out.println(wjson.toString());
		System.out.println(e.getData());
	
		try {
			System.out.println("Send a W_PLAN message");
			globalEventQueue.getSendQueue().put(e);
		} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
		}
	}


}

