package supplychain.entity;

import java.io.Serializable;
import java.util.Date;

import org.activiti.engine.form.AbstractFormType;
public class Location  extends AbstractFormType implements Serializable {
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
	public Location() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "Location [Name=" + Lname + ", X_coor=" + X_coor + ", Y_coor=" + Y_coor + ", timeStamp=" + timeStamp
				+ ", velocity=" + velocity + "]";
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
		return "Location";
	}

}
