package supplychain.activiti.coord;

import com.zbq.EventType;
import com.zbq.GlobalEventQueue;
import com.zbq.GlobalVariables;
import com.zbq.VWFEvent;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import supplychain.entity.VPort;
import supplychain.entity.WPort;
import supplychain.util.DateUtil;

import java.io.Serializable;
import java.util.*;

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
	 @Autowired
	 private GlobalVariables globalVariables;
	 @Autowired
	 private GlobalEventQueue globalEventQueue;
	
	@SuppressWarnings("unused")
	@Override
	public void execute(DelegateExecution exec) {
		// TODO Auto-generated method stubVWCoordinator.java
		HashMap<String , Object>  msgData= (HashMap<String, Object>) runtimeService.getVariables(exec.getId());
		String msgType = (String) msgData.get("msgType");
		String vpid = (String) msgData.get("V_pid");
		String wpid = (String) msgData.get("W_pid");
		runtimeService.setVariable(vpid, "W_pid" , wpid);	
		globalVariables.createOrUpdateVariableByNameAndValue(vpid,"W_pid" , wpid);

		
		//需要计算目的地
		if(msgType.equals("msg_UpdateDest")) {
			//TODO : handle Anchring/Docking duration changes or  handle ET changes
			
			//获取Vessel , Weagon 两边的港口列表
			List<VPort> vTargLocList = getVports(vpid ,  "TargLocList");
			List<WPort> wTargLocList = getWports(wpid ,  "W_TargLocList");
			double sp_weight = (double) runtimeService.getVariable(wpid, "SparePartWeight");
			
			HashMap<String, VPort> vpMap = new HashMap<String , VPort>();
			//现将newTargLocList转为map
			for(int i = 0 ; i < vTargLocList.size() ; i++) {
				VPort nowWport = vTargLocList.get(i);
				vpMap.put(nowWport.getPname(), nowWport);
			}
			
			//计算当前时间 ， 以Vessel实例启动时间为基准
			Date v_start_date = (Date) runtimeService.getVariable(vpid, "StartTime");
			Date cur_date = new Date();
			long t_ms = v_start_date.getTime();   
			t_ms += (cur_date.getTime() - v_start_date.getTime())*globalVariables.getZoomInRate();
			
			//获取车的当前位置
			
			JSONObject w_info= globalVariables.getVariableByName(wpid, "W_Info");
			String w_xc = w_info.getJSONObject("value").getString("x_Coor");
			String w_yc = w_info.getJSONObject("value").getString("y_Coor");
			System.out.println("车当位置 ： " + w_xc + " : " + w_yc);
			List<WPort> candinateWports = new ArrayList<WPort>(); //候选港口信息
			HashMap<String , JSONObject> routeMp = new HashMap<String , JSONObject>();
//			List<WPort> validPorts =  new ArrayList<WPort>();
			System.out.println("车当前位置 ： （"+ w_xc+ " , "+w_yc+")");
			for(int i = 0 ; i < wTargLocList.size() ; i++) {		//计算成本	
				WPort nowWport = wTargLocList.get(i);
				VPort nowVport = vpMap.get(nowWport.getPname());
				if((nowVport.getState().equals("InAD") || nowVport.getState().equals("AfterAD"))) {//考虑未到达的港口
					//计算预计到达是时间
					JSONObject route = PlanPath(w_xc,w_yc, nowVport.getX_coor(), nowVport.getY_coor());
					Date estiDate = DateUtil.transForDate(getEsti_Ms(route)*1000+t_ms);
					String estiDateStr= DateUtil.date2str(estiDate);
					double estiDist = getEsti_dist(route);
					nowWport.setDist(estiDist);
					nowWport.setEsTime(estiDateStr);
					if(DateUtil.TimeMinus(nowVport.getEEnd() ,  nowWport.getEsTime()) >  0) {
						double c = Math.max(DateUtil.TimeMinus(nowVport.getEStart() , nowWport.getEsTime()), 0)*nowVport.getQuayRate()*sp_weight/(1000 * 60 * 60) + nowWport.getDist()*nowWport.getCarryRate()*sp_weight;
						nowWport.setSupCost(c); //暂时把总成本记在这
						nowWport.setSortFlag(nowVport.getSortFlag());
//						nowWport.setRoute(route);
						routeMp.put(nowWport.getPname(), route);
						candinateWports.add(nowWport);
						//System.out.println(nowWport.getPname()+" : "+nowWport.getSupCost());
					}
//					validPorts.add(nowWport);
				}
			}
			
			double minCost = infinite;
			WPort destPort = null;
			//按靠港顺序排序
			 @SuppressWarnings("rawtypes")
			Comparator c = new Comparator<WPort>() {  
		            @Override  
		            public int compare(WPort a, WPort b) {  
		                // TODO Auto-generated method stub  
		                if(a.getSortFlag() > b.getSortFlag()) {
		                	 return 1;  
		                }else {
		                	 return -1;  
		                }
		            }
		        }; 
			candinateWports.sort(c);
			JSONObject pathResult = null;
			for(int i = 0 ; i < candinateWports.size() ; i++) {
				WPort twp = candinateWports.get(i);
				double co = (1 - Math.pow(k, i+1))*twp.getSupCost();
				//twp.setSupCost(co);
			//	candinateWports.set(i, twp);
				System.out.println(twp.getPname()+" : "+twp.getSupCost()+" , "+co);
				if(co < minCost) {
					minCost =co;
					destPort = twp;
					pathResult = routeMp.get(twp.getPname());
				}
			}
			
		
			VWFEvent e = new VWFEvent(EventType.W_RUN);
			e.getData().put("createAt", (new Date()).toString());
			e.getData().put("sp_weight",sp_weight);
			if(destPort != null){
				runtimeService.setVariable(vpid, "dpName", destPort.getPname());
				//runtimeService.setVariable(wpid,"W_TargPortList" , candinateWports);
				globalVariables.createOrUpdateVariableByNameAndValue(wpid, "DestPort", destPort);
				globalVariables.createOrUpdateVariableByNameAndValue(wpid,"W_TargPortList" , candinateWports);
				//SendMsg to VWF
				e.getData().put("W_Info", w_info);
				e.getData().put("wDestPort" , new JSONObject(destPort));
				e.getData().put("vDestPort",  new JSONObject(vpMap.get(destPort.getPname())));
				e.getData().put("W_TargPortList" , candinateWports);
				e.getData().put("pathResult", pathResult);
				e.getData().put("V_pid", vpid);
				e.getData().put("StartTime",v_start_date);
				e.getData().put("State", "success");
				String rea = (String) msgData.get("reason");
				e.getData().put("Reason", rea);
				System.out.println("DestPort : "+destPort.getPname());
			}else{
				e.getData().put("W_Info", w_info);
				runtimeService.setVariable(vpid, "dpName", null);
				e.getData().put("State", "fail");
				//if(validPorts.isEmpty()) {
					//runtimeService.setVariable(vpid,"isMissing", true);
					//e.getData().put("State", "Missing");
			//	}
				
			}
			globalEventQueue.sendMsg(e);
		}
		
		if(msgType.equals("msg_CreateVWConn")) {
			//在Vessel流程中设置W_pid , 建立关联
//			runtimeService.setVariable(vpid, "W_pid" , wpid);		
            System.out.println("Vessel 和 Weagon 联系建立");
        }
    }

    public long getEsti_Ms(JSONObject route) {
        @SuppressWarnings("unchecked")
        JSONArray paths = (JSONArray) route.get("paths");
        @SuppressWarnings("unchecked")
        JSONObject path = (JSONObject) paths.get(0);
        long esti = Integer.parseInt((String) path.get("duration"));
        return esti;
    }

    public double getEsti_dist(JSONObject route) {
        @SuppressWarnings("unchecked")
        JSONArray paths = (JSONArray) route.get("paths");
        @SuppressWarnings("unchecked")
        JSONObject path = (JSONObject) paths.get(0);
        double esti = Double.parseDouble((String) path.get("distance"));
        return esti;
    }

    public JSONObject PlanPath(String x1, String y1, String x2, String y2) {
        String url = "http://restapi.amap.com/v3/direction/driving?origin=" + x1 + "," + y1 + "&destination=" + x2 + "," + y2 + "&output=json&key=ec15fc50687bd2782d7e45de6d08a023";
        String s = restTemplate.getForEntity(url, String.class).getBody();
        // System.out.println(s);
        JSONObject res = new JSONObject(s);
        JSONObject route = (JSONObject) res.get("route");
        return route;
    }

    public List<VPort> getVports(String vpid, String vname) {
        @SuppressWarnings("unchecked")
        List<VPort> vtargLocMap = (List<VPort>) runtimeService.getVariable(vpid, vname);
        return vtargLocMap;
    }

    public List<WPort> getWports(String wpid, String vname) {

        @SuppressWarnings("unchecked")
        List<WPort> vtargLocMap = (List<WPort>) runtimeService.getVariable(wpid, vname);
        return vtargLocMap;
    }
}
