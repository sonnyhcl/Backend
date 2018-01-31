package supplychain.activiti.listener;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import supplychain.global.GlobalVariables;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;


@Service("sengMsgToLambdaListener")
public class SengMsgToLambdaListener implements ExecutionListener, Serializable {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private GlobalVariables globalVariables;

    @Override
    public void notify(DelegateExecution exec) {
        System.out.println("测试时间： " + new Date());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        HashMap<String, Object> vars = (HashMap<String, Object>) runtimeService.getVariables(exec.getProcessInstanceId());

        String url = "http://localhost:5000/post";
        JSONObject postData = new JSONObject();
        try {
            postData.put("key1", vars.get("key1"));
            postData.put("key2", vars.get("key2"));
            postData.put("key3", vars.get("key3"));
        } catch (JSONException e) {
            System.out.println("sengMsgToLambdaListener" + e.toString());
        }

        System.out.println("sengMsgToLambdaListener" + postData.toString());
        HttpEntity<String> entity = new HttpEntity<String>(postData.toString(), headers);

        String json = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
        System.out.println("SengMsgToLambdaListener: " + json);
    }
}
