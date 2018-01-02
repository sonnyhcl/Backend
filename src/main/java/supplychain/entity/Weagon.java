package supplychain.entity;

import java.io.Serializable;
import java.util.Date;

import org.activiti.engine.form.AbstractFormType;

public class Weagon extends AbstractFormType implements Serializable {
	
	private String W_id;
	
	private String W_Name;
	
	//W_Location
	private String X_Coor;
	private String Y_Coor;
	
	private String W_Velocity;
	
	//V_ETime
	private Date  Start;
	private Date  End;
	
	//PlanPath V_ETime
	private Date  pArri;
	
	//isNeedPlan
	private boolean needPlan;
	private int planRes;

	//当前目的地，一般是最有可能的会和点，初始一般是距离出发位置最近的港口，
	private Location W_TargLoc;
	
	
	public Weagon() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Weagon(String w_id, String w_Name, String x_Coor, String y_Coor, String w_Velocity, Date start, Date end,
			Location w_TargetLocation) {
		super();
		W_id = w_id;
		W_Name = w_Name;
		X_Coor = x_Coor;
		Y_Coor = y_Coor;
		W_Velocity = w_Velocity;
		Start = start;
		End = end;
		W_TargLoc = w_TargetLocation;
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

	public boolean isNeedPlan() {
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
		return "Weagon";
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
	
	
}
