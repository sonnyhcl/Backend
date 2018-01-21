package supplychain.entity;

import org.activiti.engine.form.AbstractFormType;

import java.io.Serializable;
import java.util.Date;

public class Weagon extends AbstractFormType implements Serializable {
    //Type Name
    private String name = "Weagon";
    private String pid; //流程实例的id

    private String W_id;

    private String W_Name;

    //W_Location
    private String X_Coor;
    private String Y_Coor;

    private String W_Velocity;

    //V_ETime
    private Date Start;
    private Date End;

    //PlanPath V_ETime
    private Date pArri;

    //isNeedPlan
    private boolean needPlan;
    private int planRes;


    //当前目的地，一般是最有可能的会和点，初始一般是距离出发位置最近的港口，
    private Location W_TargLoc;

    //isArrival
    private boolean isArrival;

    public Weagon() {
        super();
        // TODO Auto-generated constructor stub
    }

    public String getW_id() {
        return W_id;
    }

    public void setW_id(String w_id) {
        W_id = w_id;
    }

    public String getW_Name() {
        return W_Name;
    }

    public void setW_Name(String w_Name) {
        W_Name = w_Name;
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

    public String getW_Velocity() {
        return W_Velocity;
    }

    public void setW_Velocity(String w_Velocity) {
        W_Velocity = w_Velocity;
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

    public Location getW_TargLoc() {
        return W_TargLoc;
    }

    public void setW_TargLoc(Location w_TargetLocation) {
        W_TargLoc = w_TargetLocation;
    }

    public Date getpArri() {
        return pArri;
    }

    public void setpArri(Date pArri) {
        this.pArri = pArri;
    }

    public boolean getNeedPlan() {
        return needPlan;
    }

    public void setNeedPlan(boolean needPlan) {
        this.needPlan = needPlan;
    }

    public int getPlanRes() {
        return planRes;
    }

    public void setPlanRes(int planRes) {
        this.planRes = planRes;
    }

    //override AbstractFormType.
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return name;
    }

    @Override
    public Object convertFormValueToModelValue(String propertyValue) {
        // TODO Auto-generated method stub
        return (Object) propertyValue;
    }

    @Override
    public String convertModelValueToFormValue(Object modelValue) {
        // TODO Auto-generated method stub
        return modelValue.toString();
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsArrival() {
        return isArrival;
    }

    public void setIsArrival(boolean isArrival) {
        this.isArrival = isArrival;
    }

    public Weagon(String name, String pid, String w_id, String w_Name, String x_Coor, String y_Coor, String w_Velocity,
                  Date start, Date end, Date pArri, boolean needPlan, int planRes, Location w_TargLoc, boolean isArrival) {
        super();
        this.name = name;
        this.pid = pid;
        W_id = w_id;
        W_Name = w_Name;
        X_Coor = x_Coor;
        Y_Coor = y_Coor;
        W_Velocity = w_Velocity;
        Start = start;
        End = end;
        this.pArri = pArri;
        this.needPlan = needPlan;
        this.planRes = planRes;
        W_TargLoc = w_TargLoc;
        this.isArrival = isArrival;
    }

    @Override
    public String toString() {
        return "Weagon [name=" + name + ", pid=" + pid + ", W_id=" + W_id + ", W_Name=" + W_Name + ", X_Coor=" + X_Coor
                + ", Y_Coor=" + Y_Coor + ", W_Velocity=" + W_Velocity + ", Start=" + Start + ", End=" + End + ", pArri="
                + pArri + ", needPlan=" + needPlan + ", planRes=" + planRes + ", W_TargLoc=" + W_TargLoc
                + ", isArrival=" + isArrival + "]";
    }


}
