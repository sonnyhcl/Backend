package supplychain.activiti.listener;

import java.io.Serializable;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zbq.GlobalVariables;


@Service("voyaTaskStartListener")
public class VoyaTaskStartListener implements ExecutionListener, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4149621500319226872L;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private GlobalVariables globalVariables;

	@Override
	public void notify(DelegateExecution execution) {
		// TODO Auto-generated method stub
		runtimeService.setVariable(execution.getId(), "State", "voyaging");
		
		//修改某个流程实例中的变量
		String pid = execution.getProcessInstanceId();
		try {
			globalVariables.createOrUpdateVariableByNameAndValue(pid, "State" , "voyaging");
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }

}
