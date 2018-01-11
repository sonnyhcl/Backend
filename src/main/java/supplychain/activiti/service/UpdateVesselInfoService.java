package supplychain.activiti.service;

import java.io.Serializable;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import supplychain.util.HelloTest;

@Service("updateWeagonInfoService")
public class UpdateVesselInfoService implements Serializable, JavaDelegate {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3232539848590791815L;
	@Override
	public void execute(DelegateExecution dexe) {
		// TODO Auto-generated method stub
		System.out.println("\033[33;1m 正在更新车辆信息...... \033[0m");
		String pid = dexe.getProcessInstanceId();
		
	}

}
