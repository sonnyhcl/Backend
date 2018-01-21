package supplychain.entity;

import java.io.Serializable;
import java.util.Date;

public class VesselVariablesResponse implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 7998008760827142344L;
    //船号
    private String V_id;
    private String V_Name;
    //Location
    private String X_Coor;
    private String Y_Coor;
    private String V_Velocity;
    //V_ETime
    private Date Start;
    private Date End;
    //船首向
    private String V_HeadOrie;
    //对地航向
    private String V_EarthOrie;

    //当前目的地（u一般是下一港口）
    private Location V_TargetLocation;

    public Location getV_TargetLocation() {
        return V_TargetLocation;
    }

    public void setV_TargetLocation(Location v_TargetLocation) {
        V_TargetLocation = v_TargetLocation;
    }

    public String getV_id() {
        return V_id;
    }

    public void setV_id(String v_id) {
        V_id = v_id;
    }

    public String getV_Name() {
        return V_Name;
    }

    public void setV_Name(String v_Name) {
        V_Name = v_Name;
    }

    public String getX_Coor() {
        return X_Coor;
    }

    public void setX_Coor(String x_Coor) {
        X_Coor = x_Coor;
    }

    public String getY_Coor() {
        return Y_Coor;
    }

    public void setY_Coor(String y_Coor) {
        Y_Coor = y_Coor;
    }

    public String getV_Velocity() {
        return V_Velocity;
    }

    public void setV_Velocity(String v_Velocity) {
        V_Velocity = v_Velocity;
    }

    public Date getStart() {
        return Start;
    }

    public void setStart(Date start) {
        Start = start;
    }

    public Date getEnd() {
        return End;
    }

    public void setEnd(Date end) {
        End = end;
    }

    public String getV_HeadOrie() {
        return V_HeadOrie;
    }

    public void setV_HeadOrie(String v_HeadOrie) {
        V_HeadOrie = v_HeadOrie;
    }

    public String getV_EarthOrie() {
        return V_EarthOrie;
    }

    public void setV_EarthOrie(String v_EarthOrie) {
        V_EarthOrie = v_EarthOrie;
    }

    @Override
    public String toString() {
        return "Vessel [V_id=" + V_id + ", V_Name=" + V_Name + ", X_Coor=" + X_Coor + ", Y_Coor=" + Y_Coor
                + ", V_Velocity=" + V_Velocity + ", Start=" + Start + ", End=" + End + ", V_HeadOrie=" + V_HeadOrie
                + ", V_EarthOrie=" + V_EarthOrie + "]";
    }

    public VesselVariablesResponse(String v_id, String v_Name, String x_Coor, String y_Coor, String v_Velocity, Date start, Date end,
                                   String v_HeadOrie, String v_EarthOrie) {
        super();
        V_id = v_id;
        V_Name = v_Name;
        X_Coor = x_Coor;
        Y_Coor = y_Coor;
        V_Velocity = v_Velocity;
        Start = start;
        End = end;
        V_HeadOrie = v_HeadOrie;
        V_EarthOrie = v_EarthOrie;
    }

    public VesselVariablesResponse() {
        super();
        // TODO Auto-generated constructor stub
    }


}
