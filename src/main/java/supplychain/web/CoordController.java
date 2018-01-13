package supplychain.web;

import java.util.HashMap;

import org.activiti.engine.impl.util.json.JSONObject;
import org.activiti.engine.runtime.Execution;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.sym.Name;

@RestController
public class CoordController extends AbstractController {

	@RequestMapping(value = "/coord/messges/{MsgName}", method = RequestMethod.POST , produces = "application/json")
	public ResponseEntity<String> startProcessInstanceByMessage(@PathVariable("MsgName") String Msg_Name  , @RequestBody HashMap<String, Object> mp) throws InterruptedException {
		//流程引擎发送消息
		runtimeService.startProcessInstanceByMessage(Msg_Name , mp);
		JSONObject  msgData = new JSONObject(mp);
		return new ResponseEntity<String>(msgData.toString(),HttpStatus.OK);
	}

}
