package supplychain.activiti.listener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zbq.GlobalEventQueue;
import com.zbq.GlobalVariables;

import supplychain.entity.Location;
import supplychain.entity.Weagon;

@Service("sendArraInfoToSWC")
public class SendArraInfoToSWC implements TaskListener, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7591079159186056195L;

	@Autowired
	private RuntimeService runtimeService;
	@Override
	public void notify(DelegateTask dT) {
		// TODO Auto-generated method stub
		// TODO : 将当前的Arranging数据发送给
		HashMap<String, Object> vars = (HashMap<String, Object>) runtimeService
				.getVariables(dT.getProcessInstanceId());
		Weagon w = new Weagon();
		w.setW_Name("weagon_1");
		w.setX_Coor("120.1958370209");
		w.setY_Coor("30.2695940578");
		w.setIsArrival(false);
		vars.put("W_Info", w);
		// 启动Supplier Process
		runtimeService.startProcessInstanceByMessage("Msg_StartSWC", vars);
		System.out.println("SWC实例已启动");
	}

}
