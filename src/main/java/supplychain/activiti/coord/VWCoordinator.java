package supplychain.activiti.coord;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.activiti.engine.runtime.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import supplychain.entity.VPort;
import supplychain.entity.WPort;
import supplychain.util.DateUtil;

@Service("VWCoordinator")
public class VWCoordinator implements JavaDelegate, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5334846840309131394L;
	
	private static final double k = 0.5; //紧迫性参数
	private static final double infinite = 10000000000000.0;
	@Autowired
	private RuntimeService runtimeService;
	
	 @Autowired  
	 private RestTemplate restTemplate; 
	
	@Override
	public void execute(DelegateExecution exec) {
		// TODO Auto-generated method stubVWCoordinator.java
		HashMap<String , Object>  msgData= (HashMap<String, Object>) runtimeService.getVariables(exec.getId());
		String msgType = (String) msgData.get("Msg_Type");
		HashMap<String , Object> replyData = new HashMap<String , Object>();
		String vpid = (String) msgData.get("V_pid");
		String wpid = (String) msgData.get("W_pid");
		//需要计算目的地
		if(msgType.equals("msg_UpdateDest")) {
			//TODO : handle Anchring/Docking duration changes or  handle ET changes
			@SuppressWarnings("unchecked")
			List<VPort> newTargLocList = (List<VPort>) msgData.get("V_TagLocList");
			@SuppressWarnings("unchecked")
			List<WPort> wportList = (List<WPort>) msgData.get("W_TargPortList");
			double sp_weight = (double) runtimeService.getVariable(vpid, "SparePartWeight");
			HashMap<String, VPort> vpMap = new HashMap<String , VPort>();
			//现将newTargLocList转为map
			for(int i = 0 ; i < newTargLocList.size() ; i++) {
				VPort nowWport = newTargLocList.get(i);
				vpMap.put(nowWport.getPname(), nowWport);
			}
			
			String w_startTime = (String) runtimeService.getVariable(wpid ,"W_StartTime");
			Date w_realStartTime = (Date) runtimeService.getVariable(wpid, "W_RealStartTime");
			SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			Long t_ms = 0L;
			try {
				Date w_stDate= sdf.parse(w_startTime);
				t_ms = w_stDate.getTime();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Date curDate = new Date();
			t_ms += (curDate.getTime() - w_realStartTime.getTime());
			//Date simuCurTime  = DateUtil.transForDate(t_ms);
			
			List<VPort> candinateVports = new ArrayList<VPort>();
			List<WPort>candinateWports = new ArrayList<WPort>();
			for(int i = 0 ; i < wportList.size() ; i++) {
				
			
				WPort nowWport = wportList.get(i);
				VPort nowVport = vpMap.get(nowWport.getPname());
				if(nowVport.getState().equals("InAD") || nowVport.equals("AfterAD")) {
					//计算预计到达是时间
					Date date = DateUtil.transForDate(getEsti_Ms(nowWport.getX_coor(), nowWport.getY_coor(), nowVport.getX_coor(), nowVport.getY_coor())+t_ms);
					String esti_ms = sdf.format(date);
					nowWport.setEsTime(esti_ms);
					double c = Math.max(DateUtil.TimeMinus(nowVport.getEEnd(),nowWport.getEsTime() ), 0)*nowVport.getQuayRate()*sp_weight + nowWport.getDist()*nowWport.getCarryRate()*sp_weight;
					 nowVport .setCost(c);
					candinateVports.add(nowVport);
					candinateWports.add(nowWport);
				}
			}
			
			double minCost = infinite;
			VPort destPort = null;
			//排序
			 @SuppressWarnings("rawtypes")
			Comparator c = new Comparator<VPort>() {  
		            @Override  
		            public int compare(VPort a, VPort b) {  
		                // TODO Auto-generated method stub  
		                if(a.getSortFlag() > b.getSortFlag()) {
		                	 return 1;  
		                }else {
		                	  return -1;  
		                }
		            }
		        }; 
			candinateVports.sort(c);
			for(int i = 0 ; i < candinateVports.size() ; i++) {
				VPort tvp = candinateVports.get(i);
				double co = (1 - Math.pow(k, i+1))*tvp.getCost();
				tvp.setCost(co);
				candinateVports.set(i, tvp);
				if(co < minCost) {
					minCost =co;
					destPort = tvp;
				}
			}
			runtimeService.setVariable(wpid, "DestPort", destPort);
			runtimeService.setVariable(wpid,"W_TargPortList" , candinateWports);
			String replyMsgType = "Msg_AfterPlan";
			Execution replyExe = runtimeService.createExecutionQuery().messageEventSubscriptionName(replyMsgType).singleResult();
			runtimeService.messageEventReceived(replyMsgType, replyExe.getId());
		}
		
		if(msgType.equals("msg_CreateVWConn")) {
			//在Vessel流程中设置W_pid , 建立关联
			runtimeService.setVariable(vpid, "W_pid" , wpid);		
			System.out.println("Vessel 和 Weagon 联系建立");
		}
	}
	
	
	
	public long getEsti_Ms(String x1 ,  String  y1 , String x2 , String y2 ) {
		 String url = "http://restapi.amap.com/v3/direction/driving?origin="+x1+","+y1+"&"+x2+","+y2+"&output=json&key=ec15fc50687bd2782d7e45de6d08a023";
	     String s = restTemplate.getForEntity(url, String.class).getBody();
	     System.out.println(s);
	     JSONObject res = new JSONObject(s); 
	     JSONObject route =  (JSONObject) res.get("route");
		 @SuppressWarnings("unchecked")
		 JSONArray paths = (JSONArray) route.get("paths");
	     @SuppressWarnings("unchecked")
	     JSONObject path  = (JSONObject) paths.get(0);
	     long esti = Integer.parseInt((String) path.get("duration"));
	     return esti;
	}

}
