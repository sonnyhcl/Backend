package supplychain.activiti.listener;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbq.GlobalEventQueue;
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

	@Override
	public void notify(DelegateExecution dExe) {
		// TODO Auto-generated method stub
		System.out.println("\033[33;1m 初始化Weagon : \033[0m" + runtimeService);
		 Map<String, Object> vars = new HashMap<String, Object>();
		 Weagon W_Info = new Weagon();
		// Location curloc = new Location("杭州" ,"120.1958370209" ,"30.2695940578");
		 Location tloc = new Location("南京" ,"118.800095" ,"32.146214");
		 W_Info.setPid(dExe.getProcessInstanceId());
		 W_Info.setW_TargLoc(tloc);
		 W_Info.setW_Name("杭州");                           
		 W_Info.setX_Coor("120.1958370209");
		 W_Info.setY_Coor("30.2695940578");
		 W_Info.setPlanRes(1);
		 W_Info.setNeedPlan(true);
		 vars.put("W_Info", W_Info);
		 runtimeService.setVariables(dExe.getId(), vars);
//		 VoyageEvent e = new VoyageEvent(VoyageEventType.W_START);
//		 e.getData().put("createAt", (new Date()).toString());
//		 try {
//			System.out.println("Send a W_STARt message");
//			globalEventQueue.getQueue().put(e);
//		 } catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//		}
	}
}
