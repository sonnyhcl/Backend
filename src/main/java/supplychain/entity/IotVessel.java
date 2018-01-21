package supplychain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity(name = "iot_Ves")
public class IotVessel implements Serializable {
    @Id
    private long id;
    private String V_id;
}
