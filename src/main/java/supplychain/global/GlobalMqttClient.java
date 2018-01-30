package supplychain.global;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import org.springframework.stereotype.Component;
import supplychain.util.Topic;


@Component
public class GlobalMqttClient {
    private AWSIotMqttClient client;

    private String clientEndpoint = "a1sg3bdz3kie9t.iot.us-east-1.amazonaws.com";  // replace <prefix> and <region> with your own
    private String clientId = "activiti"; // replace with your own client ID. Use unique client IDs for concurrent connections.
    private String certificateFile = "G:\\Lab\\Backend\\src\\main\\resources\\activiti.cert.pem";  // X.509 based certificate file
    private String privateKeyFile = "G:\\Lab\\Backend\\src\\main\\resources\\activiti.private.key";   // PKCS#1 or PKCS#8 PEM encoded private key file


    //  Wildcard Description
    //  #:  Must be the last character in the topic to which you are subscribing. Works as a wildcard by matching the current
    //      tree and all subtrees. For example, a subscription to Sensor/# will receive messages published to Sensor/,
    //      Sensor/temp, Sensor/temp/room1, but not the messages published to Sensor.
    //
    //  +:  Matches exactly one item in the topic hierarchy. For example, a subscription to Sensor/+/room1 will receive
    //      messages published to Sensor/temp/room1, Sensor/moisture/room1, and so on.
    private String rootTopic = "activiti/#";   // root Topic that activiti subscribe

    public GlobalMqttClient() throws
            AWSIotException {
        System.out.println("单例mqtt客户端实例化");
//        SampleUtil.KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile);
//        this.client = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);
//        connect();
//        subscribe();
    }

    private void connect() throws AWSIotException {
        this.client.connect();
    }

    private void subscribe() throws AWSIotException {
        AWSIotQos qos = AWSIotQos.QOS0;
        Topic topic = new Topic(rootTopic, qos);
        this.client.subscribe(topic);
    }
}
