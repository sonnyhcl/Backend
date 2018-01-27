package supplychain.global;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import org.springframework.stereotype.Component;
import supplychain.util.Topic;


@Component
public class GlobalMqttClient {
    private AWSIotMqttClient client;

    private String clientEndpoint = "<prefix>.iot.<region>.amazonaws.com";       // replace <prefix> and <region> with your own
    private String clientId = "<unique client id>";                              // replace with your own client ID. Use unique
    // client IDs for concurrent connections.
    private String certificateFile = "<certificate file>";                       // X.509 based certificate file
    private String privateKeyFile = "<private key file>";                        // PKCS#1 or PKCS#8 PEM encoded private key file

    public GlobalMqttClient() throws
            AWSIotException {
        SampleUtil.KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile);
        this.client = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);
        connect();
        subscribe();
    }

    private void connect() throws AWSIotException {
        this.client.connect();
    }

    private void subscribe() throws AWSIotException {
        String root_topic = "clhu/+";
        AWSIotQos qos = AWSIotQos.QOS0;
        Topic topic = new Topic(root_topic, qos);
        this.client.subscribe(topic);
    }
}
