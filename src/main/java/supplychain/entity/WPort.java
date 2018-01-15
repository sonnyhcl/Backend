package supplychain.entity;

import java.io.Serializable;

import org.activiti.engine.form.AbstractFormType;

public class WPort extends AbstractFormType implements Serializable{

	private String name = "WPort";
	
	private String wpid; //车的流程实例名称
	private String pname; //港口名
	private double carryRate;
	private String esTime; //估计到达港口的时间点
	private double dist;
	private double supCost;
	private String x_coor;
	private String y_coor;
	public double getSupCost() {
		return supCost;
	}

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

	public void setSupCost(double supCost) {
		this.supCost = supCost;
	}

	public String getWpid() {
		return wpid;
	}

	public void setWpid(String wpid) {
		this.wpid = wpid;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	public String getEsTime() {
		return esTime;
	}

	public void setEsTime(String esTime) {
		this.esTime = esTime;
	}

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
		return (Object)propertyValue;
	}

	@Override
	public String convertModelValueToFormValue(Object modelValue) {
		// TODO Auto-generated method stub
		return (String)modelValue.toString();
	}

	public double getDist() {
		return dist;
	}

	public void setDist(double dist) {
		this.dist = dist;
	}

	public double getCarryRate() {
		return carryRate;
	}

	public void setCarryRate(double carryRate) {
		this.carryRate = carryRate;
	}

}
