package smartstation.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Model {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String companyName;
	private String model;
	private String variant;
	private Integer serviceFreq;
	
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getVariant() {
		return variant;
	}
	public void setVariant(String variant) {
		this.variant = variant;
	}
	public Integer getServiceFreq() {
		return serviceFreq;
	}
	public void setServiceFreq(Integer serviceFreq) {
		this.serviceFreq = serviceFreq;
	}
	
	@Override
	public String toString() {
		
		return "[Company Name : " + companyName + " Model : " + model + " Variant : " +
		variant + "Service Freq : " + serviceFreq + "]";
	}
}
