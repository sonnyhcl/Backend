package supplychain.activiti.service;

import java.io.Serializable;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import supplychain.util.HelloTest;

@Service("updateVesselInfoService")
public class UpdateWeagonInfoService implements Serializable, JavaDelegate {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3232539848590791815L;

	@Autowired
	private HelloTest helloTest;
	@Override
	public void execute(DelegateExecution dexe) {
		// TODO Auto-generated method stub
		System.out.println("\033[33;1m 正在航行 》》》》》 \033[0m");
		helloTest.printHello();
	}

}
