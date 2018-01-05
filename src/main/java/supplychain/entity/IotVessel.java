package supplychain.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name="iot_Ves")
public class IotVessel implements Serializable {
       @Id
       private long id;
       private String V_id;
}
