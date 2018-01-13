package supplychain.activiti.listener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("sendOrderToMSC")
public class SendOrderToMSC implements TaskListener, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6111970011296715447L;
	@Autowired
	private RuntimeService runtimeService;
	
	@Override
	public void notify(DelegateTask dT) {
		// TODO Auto-generated method stub
		//TODO : 将当前的Order数据发送给
		UUID orderId = java.util.UUID.randomUUID();
		System.out.println("orderId : " + orderId);
		
		HashMap<String , Object> mp = (HashMap<String, Object>) runtimeService.getVariables(dT.getProcessInstanceId());
		mp.put("OrderId", orderId.toString());
		//启动Supplier Process
		runtimeService.startProcessInstanceByMessage("Msg_StartMSC" , mp);
		System.out.println("MSC实例已启动");
	}
	
	

}
