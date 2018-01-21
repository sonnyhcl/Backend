package supplychain.activiti.listener;


import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.util.json.JSONObject;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbq.GlobalEventQueue;
import com.zbq.VWFEvent;
import com.zbq.EventType;

import supplychain.entity.Weagon;

@Service("runListener")
public class RunListener implements TaskListener, Serializable {

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
		System.out.println(pid);
		
		HashMap<String, Object> connVMData = (HashMap<String, Object>) runtimeService.getVariables(pid);
		connVMData.put("msgType" , "msg_UpdateDest");
		connVMData.put("W_pid" , pid);
		connVMData.put("reason" , "货车出发， 规划路径");
		runtimeService.startProcessInstanceByMessage("Msg_StartVWC" ,connVMData);
		System.out.println("Send  Msg_StartVWC message to VWC to connect to vessel");
		
	}
}

