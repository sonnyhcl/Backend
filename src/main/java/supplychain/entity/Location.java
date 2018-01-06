package supplychain.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.activiti.engine.form.AbstractFormType;

public class Location  extends AbstractFormType implements Serializable {
	private String name = "Location";
	private String Lname = "NowLocation";
	private String X_coor;
	private String Y_coor;
	private String timeStamp;
	private String velocity;
	private Date realTime;
	
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getVelocity() {
		return velocity;
	}
	public void setVelocity(String velocity) {
		this.velocity = velocity;
	}
	public String getX_coor() {
		return X_coor;
	}
	public void setX_coor(String x_coor) {
		this.X_coor = x_coor;
	}
	public String getY_coor() {
		return Y_coor;
	}
	public void setY_coor(String y_coor) {
		Y_coor = y_coor;
	}

	
	public Location(String name , String x_coor, String y_coor) {
		super();
		Lname = name;
		X_coor = x_coor;
		Y_coor = y_coor;
	}
	public String getLname() {
		return Lname;
	}
	public void setLname(String lname) {
		Lname = lname;
	}
	public Location() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String toString() {
		return "Location [name=" + name + ", Lname=" + Lname + ", X_coor=" + X_coor + ", Y_coor=" + Y_coor
				+ ", timeStamp=" + timeStamp + ", velocity=" + velocity + ", realTime=" + realTime + "]";
	}
	public Date getRealTime() {
		return realTime;
	}
	public void setRealTime(Date realTime) {
		this.realTime = realTime;
	}
	@Override
	public Object convertFormValueToModelValue(String propertyValue) {
		// TODO Auto-generated method stub
		return (Object)propertyValue;
	}
	@Override
	public String convertModelValueToFormValue(Object modelValue) {
		// TODO Auto-generated method stub
		return modelValue.toString();
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Location(String name, String lname, String x_coor, String y_coor, String timeStamp, String velocity,
			Date realTime) {
		super();
		this.name = name;
		Lname = lname;
		X_coor = x_coor;
		Y_coor = y_coor;
		this.timeStamp = timeStamp;
		this.velocity = velocity;
		this.realTime = realTime;
	}
	
	

}
