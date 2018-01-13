package supplychain.activiti.coord;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.runtime.Execution;
import org.apache.naming.StringManager;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbq.GlobalVariables;

import supplychain.entity.Location;
import supplychain.entity.VPort;
import supplychain.entity.WPort;

@Service("VWCoordinator")
public class VWCoordinator implements JavaDelegate, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5334846840309131394L;
	
	private static final double k = 0.5; //紧迫性参数

	@Autowired
	private RuntimeService runtimeService;
	
	@Autowired 
	private GlobalVariables globalVariables;
	
	@Override
	public void execute(DelegateExecution exec) {
		// TODO Auto-generated method stubVWCoordinator.java
		HashMap<String , Object>  msgData= (HashMap<String, Object>) runtimeService.getVariables(exec.getId());
		String msgType = (String) msgData.get("Msg_Type");
		HashMap<String , Object> replyData = new HashMap<String , Object>();
		String vpid = (String) msgData.get("V_pid");
		String wpid = (String) msgData.get("W_pid");
		//如果是ET变化
		if(msgType.equals("Msg_ETChange")) {
			//TODO : handle ET changes
			
		}
		
		//如果是抛卯时间变化
		if(msgType.equals("msg_ADTimeChange")) {
			//TODO : handle Anchring/Docking duration changes
			@SuppressWarnings("unchecked")
			List<VPort> oldTargLocList = (List<VPort>) msgData.get("Old_TagLocList");
			@SuppressWarnings("unchecked")
			List<VPort> newTargLocList = (List<VPort>) msgData.get("New_TagLocList");
			@SuppressWarnings("unchecked")
			List<WPort> wportList = (List<WPort>) msgData.get("wports");
			double sp_weight = (double) msgData.get("SparePartWeight");
			HashMap<String, Object> resultMap = new HashMap<String , Object>();
			List<VPort> candinateVports = new ArrayList<VPort>();
			for(int i = 0 ; i < newTargLocList.size() ; i++) {
				VPort nowVport = newTargLocList.get(i);
				WPort nowWport = wportList.get(i);
				if((nowVport.getState().equals("InAD") || nowVport.getState().equals("AfterAD")) && nowVport.getIsCraneStart() == true  && nowVport.getIsMeetWeightCond() == true ) {
					VPort vp = new VPort();
					double c = Math.max(TimeMinus(nowVport.getEEnd(),nowWport.getEsTime() ), 0)*nowVport.getQuayRate()*sp_weight + nowWport.getDist()*nowWport.getCarryRate()*sp_weight;
					vp.setCost(c);
					candinateVports.add(vp);
				}
			}
			
			double minCost = 10000000000000.0;
			VPort destPort = null;
			for(int i = 0 ; i < candinateVports.size() ; i++) {
				VPort tvp = candinateVports.get(i);
				double c = (1 - Math.pow(k, i+1))*tvp.getCost();
				tvp.setCost(c);
				candinateVports.set(i, tvp);
				if(c < minCost) {
					minCost =c;
					destPort = tvp;
				}
			}
			runtimeService.setVariable(wpid, "DestPort", destPort);
		}
		
		if(msgType.equals("msg_CreateVWConn")) {
			//在Vessel流程中设置W_pid , 建立关联
			runtimeService.setVariable(vpid, "W_pid" , wpid);
			//将后续港口的列表传给车流程
			@SuppressWarnings("unchecked")
			List<WPort> tls = (List<WPort>) runtimeService.getVariable(vpid, "TargLocList");
			List<WPort> wports = new ArrayList<WPort>();
			for(int i = 0 ; i < tls.size() ; i++) {
				WPort wp = new WPort();
				wp.setPname(tls.get(i).getPname());
				wports.add(wp);
			}
			runtimeService.setVariable(wpid, "wports" , wports);
			
			System.out.println("Vessel 和 Weagon 联系建立");
		}
		
		//返回结果给Weagon --> Msg_AfterPlan
	
		String replyMsgType = "Msg_AfterPlan";
		Execution replyExe = runtimeService.createExecutionQuery().messageEventSubscriptionName(replyMsgType).singleResult();
		runtimeService.messageEventReceived(replyMsgType, replyExe.getId(), replyData);
	}
	
	/**
	 * return hours
	 * @param left
	 * @param right
	 * @return
	 * @throws ParseException
	 */
	public double TimeMinus(String left , String right) { 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
		try {
			Date d1 = sdf.parse(left);
			Date d2 = sdf.parse(right);
			return (d1.getTime() - d2.getTime())*1.0/(1000 * 60 * 60);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}

}
