package smartstation.entities;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Vehicle {
	@Id
	private String vehNum;
	private Date lastServiceDate;
	private int lastServiceKms;
	private Integer yearOfPurchase;
	
	@ManyToOne(cascade=CascadeType.PERSIST)
	private Model model;
	
	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name="customer_id", referencedColumnName="id")
	private Customer customer;

	public String getVehNum() {
		return vehNum;
	}

	public void setVehNum(String vehNum) {
		this.vehNum = vehNum;
	}

	public Date getLastServiceDate() {
		return lastServiceDate;
	}

	public void setLastServiceDate(Date lastServiceDate) {
		this.lastServiceDate = lastServiceDate;
	}

	public int getLastServiceKms() {
		return lastServiceKms;
	}

	public void setLastServiceKms(int lastServiceKms) {
		this.lastServiceKms = lastServiceKms;
	}

	public Integer getYearOfPurchase() {
		return yearOfPurchase;
	}
	public void setYearOfPurchase(Integer yearOfPurchase) {
		this.yearOfPurchase = yearOfPurchase;
	}
	
	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	@Override
	public String toString() {
		
		return vehNum;
	}
}
