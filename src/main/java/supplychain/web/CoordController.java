package supplychain.web;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import supplychain.activiti.rest.service.api.CustomArrayListRestVariableConverter;
import supplychain.entity.Location;
import supplychain.entity.VPort;
import supplychain.event.EventType;
import supplychain.event.VWFEvent;
import supplychain.global.GlobalEventQueue;
import supplychain.global.GlobalVariables;

import javax.inject.Inject;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


@RestController
public class CoordController extends AbstractController {
    @Autowired
    private RestTemplate restTemplate;

    @Inject
    private Environment environment;

    @Autowired
    private GlobalVariables globalVariables;

    @Autowired
    private GlobalEventQueue globalEventQueue;

    @RequestMapping(value = "/coord/messages/{MsgName}", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<HashMap<String, Object>> startProcessInstanceByMessage(@PathVariable("MsgName") String Msg_Name, @RequestBody HashMap<String, Object> mp)
            throws InterruptedException {
        System.out.println(Msg_Name);
        if (environment.getProperty("lambda.use").equals("no")) {
            System.out.println("不用lambda");
            runtimeService.startProcessInstanceByMessage(Msg_Name, mp);
        } else {
            System.out.println("用lambda");
            globalVariables.sendMessageToCoordinator(Msg_Name, mp);
        }
        return new ResponseEntity<>(mp, HttpStatus.OK);
    }

    @RequestMapping(value = "/coord/runtime/{MsgName}", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<HashMap<String, Object>> startProcessInstanceByLambdaMessage(@PathVariable("MsgName") String Msg_Name, @RequestBody HashMap<String, Object> mp)
            throws InterruptedException {
        System.out.println("startProcessInstanceByLambdaMessage: " + mp.toString());
        System.out.println(Msg_Name);
        runtimeService.startProcessInstanceByMessage(Msg_Name, mp);
        System.out.println("startProcessInstanceByLambdaMessage done...");
        return new ResponseEntity<>(mp, HttpStatus.OK);
    }

    @RequestMapping(value = "/coord/event", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<HashMap<String, Object>> getEventFromLambda(@RequestBody HashMap<String, Object> mp)
            throws InterruptedException {
        System.out.println("/coord/event: " + mp.toString());

        @SuppressWarnings("unchecked")
        List<HashMap<String, Object>> targLocMap = (List<HashMap<String, Object>>) mp.get("MSC_TargPorts");
        CustomArrayListRestVariableConverter vpac = new CustomArrayListRestVariableConverter();
        List<VPort> targLocList = vpac.Map2VPortList(targLocMap);

        VWFEvent e = new VWFEvent(EventType.values()[(Integer) (mp.get("type"))]);
        e.getData().put("createAt", (new Date()).toString());
        e.getData().put("MSC_TargPorts", targLocList);
        globalEventQueue.sendMsg(e);

        return new ResponseEntity<>(mp, HttpStatus.OK);
    }

    @RequestMapping(value = "/getPaths", method = RequestMethod.GET, produces = "application/json")
    public int hello() {
        String url = "http://restapi.amap.com/v3/direction/driving?origin=115.13506,30.21027&destination=115.5674,29.83692&output=json&key=ec15fc50687bd2782d7e45de6d08a023";
        String s = restTemplate.getForEntity(url, String.class).getBody();
//        System.out.println(s);
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

    @RequestMapping(value = "/supplier/location", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Location> postSupLoc(@RequestBody HashMap<String, Object> mp) {
        String x_coor = mp.get("x_coor").toString();
        String y_coor = mp.get("y_coor").toString();
        System.out.println(x_coor + " " + y_coor);
        String lname = (String) mp.get("slname");
        Location sloc = new Location(lname, x_coor, y_coor);
        System.out.println(sloc.toString());
        globalVariables.setSupLoc(sloc);
        //JSONObject sljson = new JSONObject(sloc);
        return new ResponseEntity<Location>(sloc, HttpStatus.OK);
    }

}
