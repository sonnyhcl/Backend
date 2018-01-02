package supplychain.activiti.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.activiti.app.model.runtime.RuntimeAppDefinitionSaveRepresentation;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.runtime.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import supplychain.entity.Weagon;

@Service("planPathService")
public class PlanPathService implements JavaDelegate, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8770010597954761639L;

	@Autowired
	private RuntimeService runtimeService ;
	@Override
	public void execute(DelegateExecution exec) {
		// TODO Auto-generated method stub
		System.out.println("\033[33;1m 正在重新规划路径....... \033[0m");
		String executionId = exec.getId();
		runtimeService.setVariable(executionId, "NeedPlan", true);//告知前端需要规划路径
		while(true){
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//检查前端是否传来新规划的路径信息
			boolean need = (boolean) runtimeService.getVariable(executionId,  "NeedPlan");
			if(need == false) {
				//send messge to coordinator
				Execution coord = runtimeService.createExecutionQuery().messageEventSubscriptionName("Msg_StartCoord").singleResult();
				 Map<String, Object> vars = new HashMap<String, Object>();
				 vars.put("W_Info", runtimeService.getVariable(executionId, "W_Info"));
				runtimeService.messageEventReceived("Msg_StartCoord", coord.getId(),vars);
				break;
			}
		}
	}

}
