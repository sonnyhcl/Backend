package supplychain.activiti.service;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import java.io.Serializable;

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
