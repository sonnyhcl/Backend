package supplychain.web;

import java.util.HashMap;

import org.activiti.engine.impl.util.json.JSONObject;
import org.activiti.engine.runtime.Execution;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CoordController extends AbstractController {

	@RequestMapping(value = "/coord/messge", method = RequestMethod.POST , produces = "application/json")
	public ResponseEntity<String> createMessage(@RequestBody HashMap<String, Object> mp) throws InterruptedException {
		//流程引擎发送消息
		String Msg_Name = mp.get("Msg_Name").toString();
		Execution e = runtimeService.createExecutionQuery().messageEventSubscriptionName(Msg_Name).singleResult();
		runtimeService.messageEventReceived(Msg_Name, e.getId(), mp);
		JSONObject  msgData = new JSONObject(mp);
		return new ResponseEntity<String>(msgData.toString(),HttpStatus.OK);
	}

}
