package supplychain.web;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.activiti.app.service.editor.ActivitiTaskActionService;
import org.activiti.engine.impl.form.StringFormType;
import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.cache.LoadingCache;
import com.zbq.GlobalEventQueue;
import com.zbq.VWFEvent;
import com.zbq.ACTFEvent;
import com.zbq.EventType;

import supplychain.activiti.rest.service.api.CustomActivitiTaskActionService;
import supplychain.entity.Weagon;


@RestController
public class VesselController extends AbstractController{

	@Autowired
	protected CustomActivitiTaskActionService taskActionService;
	
	@Autowired
	private GlobalEventQueue globalEventQueue;
	
	//@CrossOrigin  // 单独测试起了作用
	@RequestMapping(value = "/test")
	public String test() {
		return "test ok";
	}
	@RequestMapping(value = "/tasks/{taskId}/action/complete", method = RequestMethod.PUT)
	@ResponseStatus(value = HttpStatus.OK)
	public void completeTask(@PathVariable String taskId) {
		System.out.println("*******************completeTask**************************");
	    taskActionService.completeTask(taskId);
	}
	
	@RequestMapping(value = "/sevents", method = RequestMethod.GET)
	public ResponseEntity<String> queryEvents() throws InterruptedException {
	//	System.out.println("lastId : "+lastId);
		JSONArray result = new JSONArray();
		LinkedBlockingQueue<VWFEvent> q = globalEventQueue.getSendQueue();
		while(!q.isEmpty()) {
			VWFEvent e = q.take();
		//	System.out.println("send lastId ：" + lastId +" --- e.getId() : "+e.getId());
			//if(e.getId()>=lastId) {
				result.put(e.getJson());
			//}
		}
		
		return new ResponseEntity<String>(result.toString(), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/revents", method = RequestMethod.POST , produces = "application/json")
	public ResponseEntity<String> createEvent(@RequestBody HashMap<String, Object> map) throws InterruptedException {
		System.out.println("Events from frontEnd");
		System.out.println("Type : "+map.get("type"));
		ACTFEvent e= new ACTFEvent(translateType((String) map.get("type")));
		System.out.println(map.get("id").toString());
		e.setId(Long.parseLong(map.get("id").toString()));
		HashMap<String, Object> dataMap = new HashMap<String , Object>();
		dataMap.put("data" , map.get("data"));
		JSONObject data = new JSONObject(dataMap);
		e.setData(data.getJSONObject("data"));
		LinkedBlockingQueue<ACTFEvent> q = globalEventQueue.getRecQueue();
		//create receive event
		q.put(e);
		JSONArray result = new JSONArray();
		result.put(e.getJson());
		return new ResponseEntity<String>(result.toString(),HttpStatus.OK);
	}
	@RequestMapping(value = "/revents", method = RequestMethod.GET)
	public ResponseEntity<String> queryREvents() throws InterruptedException {
		//System.out.println("GET sevents");
		JSONArray result = new JSONArray();
		LinkedBlockingQueue<ACTFEvent> q = globalEventQueue.getRecQueue();
		while(!q.isEmpty()) {//取空队列里面的所有事件
			ACTFEvent e = q.take();	
			result.put(e.getJson());
		}
		return new ResponseEntity<String>(result.toString(), HttpStatus.OK);
	}
	
	
	private EventType translateType(String eventType) {
		// TODO Auto-generated method stub
		if(eventType.equals("RW_PLAN")) {
			return EventType.RW_PLAN;
		}
		if(eventType.equals("RW_STOP")) {
			return EventType.RW_STOP;
		}
		return null;
	}
}
