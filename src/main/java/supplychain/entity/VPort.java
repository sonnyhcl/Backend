package supplychain.entity;

import org.activiti.engine.form.AbstractFormType;

import java.io.Serializable;

public class VPort extends AbstractFormType implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -137992023291373522L;


    private String name = "VPort";


    private String pname;
    private double quayRate;

    private double weight;
    private boolean isCraneStart;
    private String EStart;
    private String EEnd;
    private boolean isMeetWeightCond = true; // 默认都满足载重
    private String State; // BeforeAD , InAD , AfterAD
    private double cost;
    private String x_coor;
    private String y_coor;
    private double sortFlag;

    public String getX_coor() {
        return x_coor;
    }

    public void setX_coor(String x_coor) {
        this.x_coor = x_coor;
    }

    public String getY_coor() {
        return y_coor;
    }

    public void setY_coor(String y_coor) {
        this.y_coor = y_coor;
    }

    public double getSortFlag() {
        return sortFlag;
    }

    public void setSortFlag(double sortFlag) {
        this.sortFlag = sortFlag;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean getIsCraneStart() {
        return isCraneStart;
    }

    public void setIsCraneStart(boolean isCraneStart) {
        this.isCraneStart = isCraneStart;
    }

    /****************************************************/
    public void setName(String name) {
        this.name = name;
    }

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

    /***********************************************************/
    public double getQuayRate() {
        return quayRate;
    }

    public void setQuayRate(double quayRate) {
        this.quayRate = quayRate;
    }

    public String getEStart() {
        return EStart;
    }

    public void setEStart(String eStart) {
        EStart = eStart;
    }

    public String getEEnd() {
        return EEnd;
    }

    public void setEEnd(String eEnd) {
        EEnd = eEnd;
    }

    public boolean getIsMeetWeightCond() {
        return isMeetWeightCond;
    }

    public void setIsMeetWeightCond(boolean isMeetWeightCond) {
        this.isMeetWeightCond = isMeetWeightCond;
    }

    public VPort() {
        super();
        // TODO Auto-generated constructor stub
    }

    public VPort(String pname, String state, double weight, double quayRate, String x_coor, String y_coor, boolean
            isCraneStart, int sortFlag) {
        super();
        this.pname = pname;
        this.State = state;
        this.quayRate = quayRate;
        this.weight = weight;
        this.isCraneStart = isCraneStart;
        this.x_coor = x_coor;
        this.y_coor = y_coor;
        this.sortFlag = sortFlag;
    }


    public VPort(String name, String pname, double quayRate, double weight, boolean isCraneStart, String eStart,
                 String eEnd, boolean isMeetWeightCond, String state, double cost, String x_coor, String y_coor,
                 double sortFlag) {
        super();
        this.name = name;
        this.pname = pname;
        this.quayRate = quayRate;
        this.weight = weight;
        this.isCraneStart = isCraneStart;
        EStart = eStart;
        EEnd = eEnd;
        this.isMeetWeightCond = isMeetWeightCond;
        State = state;
        this.cost = cost;
        this.x_coor = x_coor;
        this.y_coor = y_coor;
        this.sortFlag = sortFlag;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

	@Override
	public String toString() {
		return "VPort [name=" + name + ", pname=" + pname + ", quayRate=" + quayRate + ", weight=" + weight
				+ ", isCraneStart=" + isCraneStart + ", EStart=" + EStart + ", EEnd=" + EEnd + ", isMeetWeightCond="
				+ isMeetWeightCond + ", State=" + State + ", cost=" + cost + ", x_coor=" + x_coor + ", y_coor=" + y_coor
				+ ", sortFlag=" + sortFlag + "]";
	}


}
