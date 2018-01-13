package supplychain.entity;

import java.io.Serializable;

import org.activiti.engine.form.AbstractFormType;
import org.activiti.engine.impl.form.StringFormType;

import com.fasterxml.jackson.core.sym.Name;
import com.google.common.primitives.Doubles;

public class VPort extends AbstractFormType implements Serializable{
	
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
	private boolean isMeetWeightCond;
	private String State; // BeforeAD , InAD , AfterAD
	private double cost;

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
		return (Object)propertyValue;
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

	public VPort(String pname, double quayRate, double weight, boolean isCraneStart) {
		super();
		this.pname = pname;
		this.quayRate = quayRate;
		this.weight = weight;
		this.isCraneStart = isCraneStart;
	}


	public VPort(String name, String pname, double quayRate,  double weight, boolean isCraneStart,
			String eStart, String eEnd, boolean isMeetWeightCond, String state) {
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
	
	
}
