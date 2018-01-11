package supplychain.activiti.listener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.rest.service.api.RestResponseFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbq.GlobalEventQueue;
import com.zbq.GlobalVariables;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import supplychain.entity.Location;

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

	@Autowired
	private ObjectMapper objectMapper;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private GlobalVariables globalVariables;

	@Autowired
	private RestResponseFactory restResponseFactory;

	@Override
	public void notify(DelegateExecution dExe) {
		// TODO Auto-generated method stub
		System.out.println("\033[33;1m 初始化vessel-process : \033[0m" + runtimeService);
		Map<String, Object> vars = new HashMap<String, Object>();
		String pid = dExe.getProcessInstanceId();

		vars.put("pid", pid);
		vars.put("V_TargLoc", new Location());
		vars.put("NextPort", new Location());
		vars.put("NowLoc", new Location());
		vars.put("PrePort", new Location()); // 上一港口
		vars.put("State", "voyaging"); // 船的状态
		vars.put("StartTime", new Date()); // 每段航行的起始时间
		// runtimeService.setVariables(dExe.getId(), vars);

		try {
			globalVariables.createOrUpdateVariablesByValue(pid, vars);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
