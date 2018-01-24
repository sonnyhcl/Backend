package supplychain.activiti.coord;

import com.zbq.EventType;
import com.zbq.GlobalEventQueue;
import com.zbq.GlobalVariables;
import com.zbq.VWFEvent;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import supplychain.activiti.rest.service.api.CustomArrayListRestVariableConverter;
import supplychain.entity.VPort;
import supplychain.entity.WPort;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service("MSCoordinator")
public class MSCoordinator implements JavaDelegate, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 5334846840309131394L;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private GlobalVariables globalVariables;
    @Autowired
    private GlobalEventQueue globalEventQueue;


    @Override
    public void execute(DelegateExecution exec) {
        // TODO Auto-generated method stubVWCoordinator.java
        HashMap<String, Object> msgData = (HashMap<String, Object>) runtimeService.getVariables(exec.getId());
        String msgType = (String) msgData.get("msgType");
        if (msgType.equals("Msg_StartSupplier")) {
            //TODO: 筛选港口
            double w_thre = (double) msgData.get("SparePartWeight");
            @SuppressWarnings("unchecked")
            List<HashMap<String, Object>> targLocMap = (List<HashMap<String, Object>>) msgData.get("V_TargLocList");
            CustomArrayListRestVariableConverter vpac = new CustomArrayListRestVariableConverter();
            List<VPort> targLocList = vpac.Map2VPortList(targLocMap);


            List<VPort> candiVPorts = new ArrayList<VPort>();
            int lastId = -1; // 最后一个港口序号
            for (int i = 0; i < targLocList.size(); i++) {
                VPort now = targLocList.get(i);
                if (now.getWeight() >= w_thre) {
                    now.setIsMeetWeightCond(true);
                } else {
                    now.setIsMeetWeightCond(false);
                }

                if (now.getIsCraneStart() == false) {
                    now.setIsMeetWeightCond(false);
                }
                if (now.getIsMeetWeightCond() == true) {
                    candiVPorts.add(now);
                    lastId  = i;
                    
                }
                targLocList.set(i, now);  
            }
            String vpid = (String) msgData.get("V_pid");
            System.out.println("last valid port : " + lastId+" : vpid "+vpid);
            runtimeService.setVariable(vpid, "lastValidId", lastId);

            //SendMsg to VWF
            VWFEvent e = new VWFEvent(EventType.MSC_MeetWeightCond);
            e.getData().put("createAt", (new Date()).toString());
            e.getData().put("MSC_TargPorts", targLocList);
            globalEventQueue.sendMsg(e);
            msgData.remove("V_TargLocList");

            System.out.println("根据港口起重机启动与否及载重筛选港口完毕！");

            //TODO : Start Manager 获取候选港口列表 ， 并填入运费
            // 消息启动Supplier流程
            @SuppressWarnings("unchecked")
            List<WPort> wtarglocs = new ArrayList<WPort>();
            for (int i = 0; i < candiVPorts.size(); i++) {
                VPort vp = candiVPorts.get(i);
                WPort wp = new WPort();
                wp.setPname(vp.getPname());
                wp.setCarryRate(globalVariables.getCarRateMp().get(vp.getPname()));
                wp.setX_coor(vp.getX_coor());
                wp.setY_coor(vp.getY_coor());
                if (vp.getIsMeetWeightCond() == true) {
                    wtarglocs.add(wp);
                }
            }
            msgData.put("W_TargLocList", wtarglocs);
            msgData.remove("msgType");
            //	msgData.remove("V_pid");
            runtimeService.startProcessInstanceByMessage("Msg_StartSupplier", msgData);
            System.out.println("Supplier流程实例已启动");
        }

        if (msgType.equals("#")) {
            //TODO :
        }

    }

}
