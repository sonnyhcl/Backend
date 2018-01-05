package supplychain.activiti.listener;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbq.GlobalEventQueue;
import com.zbq.VWFEvent;
import com.zbq.EventType;

import supplychain.entity.Location;
import supplychain.entity.VesselVariablesResponse;

@Service("initListener")
public class InitListener implements ExecutionListener, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 298971968212119081L;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private GlobalEventQueue globalEventQueue;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void notify(DelegateExecution dExe) {
		// TODO Auto-generated method stub
		System.out.println("\033[33;1m 初始化vessel-process : \033[0m" + runtimeService);
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("V_TargLoc", new Location());
		vars.put("NextPort", new Location());
		vars.put("NowLoc", new Location());
		vars.put("PrePort", new Location()); // 上一港口
		vars.put("State", "voyaging"); // 船的状态
		vars.put("StartTime", new Date()); // 每段航行的起始时间
		runtimeService.setVariables(dExe.getId(), vars);

		VWFEvent e = new VWFEvent(EventType.V_START);
		e.getData().put("createAt", (new Date()).toString());

		try {
			globalEventQueue.getSendQueue().put(e);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
