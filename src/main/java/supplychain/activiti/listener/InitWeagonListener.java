package supplychain.activiti.listener;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.rest.service.api.RestResponseFactory;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.engine.variable.RestVariable.RestVariableScope;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbq.GlobalEventQueue;
import com.zbq.GlobalVariables;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zbq.EventType;

import supplychain.entity.Location;
import supplychain.entity.Weagon;

@Service("initWeagonListener")
public class InitWeagonListener implements ExecutionListener, Serializable {

	private static final long serialVersionUID = -51948726954754158L;
	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private GlobalEventQueue globalEventQueue;
	
	@Autowired
	private GlobalVariables globalVariables; 
	
	@Override
	public void notify(DelegateExecution dExe) {
		// TODO Auto-generated method stub
		System.out.println("\033[33;1m 初始化Weagon : \033[0m" + runtimeService);
		 Map<String, Object> vars = new HashMap<String, Object>();
		 //Weagon w = new Weagon();
		 Location curloc = new Location("杭州" ,"120.1958370209" ,"30.2695940578");
		 String pid = dExe.getProcessInstanceId();
		 Weagon w = (Weagon) runtimeService.getVariable(pid, "W_Info");
		// Location tloc = new Location("南京" ,"118.800095" ,"32.146214");
		 w.setPid(pid);
		// w.setW_TargLoc(tloc);
//		 w.setW_Name("杭州");                           
//		 w.setX_Coor("120.1958370209");
//		 w.setY_Coor("30.2695940578");
//		 w.setPlanRes(1);
//		 w.setNeedPlan(true);
		 w.setIsArrival(false);
		 vars.put("W_Info", w);
		 runtimeService.setVariable(pid, "W_Info" , w);
		
		 //上传变量到全局变量中
		 try {
			globalVariables.createOrUpdateVariablesByValue(pid, vars);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
