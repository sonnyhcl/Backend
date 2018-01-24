package supplychain.web;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.util.HashMap;


@RestController
public class CoordController extends AbstractController {
    @Autowired
    private RestTemplate restTemplate;

    @Inject
    private Environment environment;

    @RequestMapping(value = "/coord/messages/{MsgName}", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<HashMap<String, Object>> startProcessInstanceByMessage(@PathVariable("MsgName") String Msg_Name,
                                                                                 @RequestBody HashMap<String, Object> mp)
            throws InterruptedException {
        System.out.println("这里判断用不用");
        System.out.println(Msg_Name);
        String useLambda = environment.getProperty("lambda.use");
        if (useLambda.equals("no")) {
            System.out.println("不用lambda");
            runtimeService.startProcessInstanceByMessage(Msg_Name, mp);
        } else {
            System.out.println("用lambda");
            runtimeService.startProcessInstanceByMessage(Msg_Name, mp);
        }
        return new ResponseEntity<HashMap<String, Object>>(mp, HttpStatus.OK);
    }

    @RequestMapping(value = "/getPaths", method = RequestMethod.GET, produces = "application/json")
    public int hello() {
        String url = "http://restapi.amap.com/v3/direction/driving?origin=115.13506,30.21027&destination=115.5674," +
                "29.83692&output=json&key=ec15fc50687bd2782d7e45de6d08a023";
        String s = restTemplate.getForEntity(url, String.class).getBody();
        System.out.println(s);
        //  JSONObject json = new JSONObject(s);
        JSONObject res = new JSONObject(s);
        JSONObject route = (JSONObject) res.get("route");
        @SuppressWarnings("unchecked")
        JSONArray paths = (JSONArray) route.get("paths");
        @SuppressWarnings("unchecked")
        JSONObject path = (JSONObject) paths.get(0);
        int esti = Integer.parseInt((String) path.get("duration"));
        return esti;
    }

}
