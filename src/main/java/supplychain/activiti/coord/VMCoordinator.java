package supplychain.activiti.coord;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbq.GlobalVariables;

import supplychain.entity.Location;
import supplychain.entity.VPort;

@Service("VMCoordinator")
public class VMCoordinator implements JavaDelegate, Serializable {
	
	@Autowired
	private RuntimeService runtimeService;
	
	@Autowired 
	private GlobalVariables globalVariables;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5334846840309131394L;

	@Override
	public void execute(DelegateExecution exec) {
		// TODO Auto-generated method stubVWCoordinator.java
		
		//TODO : VMC Locgic --- 取出所有变量转发给Manager流程
		HashMap<String , Object>  mp = (HashMap<String, Object>) runtimeService.getVariables(exec.getId());
		
		//mp.put("PortsWeightInfo", ports);
		//筛选港口
		 double w_thre = (double) mp.get("SparePartWeight");
		 @SuppressWarnings("unchecked")
		List<VPort> targLocList = (List<VPort>) mp.get("V_TargetLocationList");
 		 for(int i = 0 ; i < targLocList.size() ; i++) {
 			 VPort now = targLocList.get(i);
 			 VPort portInfo = (VPort) globalVariables.getPortsInfo().get(now.getPname());
 			 if(portInfo.getWeight() < w_thre) {
 				 now.setIsMeetWeightCond(true);
 			 }else {
 				 now.setIsMeetWeightCond(false);
 			 }
 			 
 			 if(portInfo.getIsCraneStart() == false) {
 				now.setIsMeetWeightCond(false);
 			 }
 			 targLocList.set(i, now);
 		 }
 		 
 		 String V_pid = (String) mp.get("V_pid");
 		 runtimeService.setVariable(V_pid, "TargLocList", targLocList );
 		 
 		 System.out.println("根据港口起重机启动与否及载重筛选港口完毕！");
		
		// 消息启动Manager流程
 		 //不需要将V_TargetLocationList下传， 从mp中移除
 		 mp.remove("V_TargetLocationList");
		runtimeService.startProcessInstanceByMessage("Msg_StartMana" , mp);
		System.out.println("Manager流程实例已启动");
		
		 //TODO : 通知前端使不合要求的港口变灰
	}
}
