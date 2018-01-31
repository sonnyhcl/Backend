package supplychain.util;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;

public class Topic extends AWSIotTopic {
    public Topic(String topic, AWSIotQos qos) {
        super(topic, qos);
    }

    @Override
    public void onMessage(AWSIotMessage message) {
        System.out.println("IoT日志");
//        System.out.println(message.toString());
//        System.out.println(message.getTopic());
//        System.out.println(message.getStringPayload());
        // update devices' shadow info in message
        // distinguish device by /{device_name}/{id}/{attribute_name}
    }
}