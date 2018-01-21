package supplychain.activiti.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import supplychain.entity.Location;
import supplychain.util.EqualUtil;

import java.util.Date;

public class VoyagingListener implements TaskListener {


    @Override
    public void notify(DelegateTask dTask) {
        // TODO Auto-generated method stub
        Location next_port = (Location) dTask.getVariable("Next_Port");
        Location now_loc = (Location) dTask.getVariable("Now_Loc");
        System.out.println("\033[33;1m 正在航行至 : \033[0m" + next_port);
        while (EqualUtil.IsEqual(now_loc, next_port)) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            System.out.println(new Date() + "当前位置 ： Now_Location : " + now_loc.toString());
        }

        //循环结束，完成任务
    }

}
