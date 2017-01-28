package smartstation.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
@Entity
public class Customer {
@Id
@GeneratedValue(strategy=GenerationType.AUTO)
private Long id;
private String name;
private String address;
private Long contactNum;

public Long getId() {
	return id;
}
public void setId(Long id) {
	this.id = id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getAddress() {
	return address;
}
public void setAddress(String address) {
	this.address = address;
}
public Long getContactNum() {
	return contactNum;
}
public void setContactNum(Long contactNum) {
	this.contactNum = contactNum;
}

}
