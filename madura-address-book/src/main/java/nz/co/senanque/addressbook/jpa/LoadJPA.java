package nz.co.senanque.addressbook.jpa;

import javax.annotation.PostConstruct;

import nz.co.senanque.addressbook.instances.Person;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Roger Parkinson
 *
 */
@Component
public class LoadJPA {
	
	@Autowired AddressBookDAO m_addressBookDAO;

	public AddressBookDAO getAddressBookDAO() {
		return m_addressBookDAO;
	}

	public void setAddressBookDAO(AddressBookDAO addressBookDAO) {
		m_addressBookDAO = addressBookDAO;
	}

	/**
	 * Load the data into the memory-based database.
	 */
	@PostConstruct
	@Transactional
	public void loadData() {
		Person person = new Person();
		person.setName("Amy Leith");
		person.setEmail("amy.leith@waituhi.com");
		person.setAddress("Waituhi Valley, Ruatane");
		person.setGender("F");
		person.setStartDate(java.sql.Date.valueOf("2000-10-01"));
		person.setAmount(100);
		getAddressBookDAO().createPerson(person);
		person = new Person();
		person.setName("Jack Leith");
		person.setEmail("jack.leith@waituhi.com");
		person.setAddress("Waituhi Valley, Ruatane");
		person.setGender("M");
		person.setStartDate(java.sql.Date.valueOf("2000-10-01"));
		person.setAmount(100);
		getAddressBookDAO().createPerson(person);
		person = new Person();
		person.setName("John Leith");
		person.setEmail("john.leith@waituhi.com");
		person.setAddress("Waituhi Valley, Ruatane");
		person.setGender("M");
		person.setStartDate(java.sql.Date.valueOf("2000-10-01"));
		person.setAmount(100);
		getAddressBookDAO().createPerson(person);
		person = new Person();
		person.setName("Harry Leith");
		person.setEmail("harry.leith@waituhi.com");
		person.setAddress("Waituhi Valley, Ruatane");
		person.setGender("M");
		person.setStartDate(java.sql.Date.valueOf("2000-10-01"));
		person.setAmount(100);
		getAddressBookDAO().createPerson(person);
		person = new Person();
		person.setName("Susannah Leith");
		person.setEmail("susy.leith@waituhi.com");
		person.setAddress("Waituhi Valley, Ruatane");
		person.setGender("F");
		person.setStartDate(java.sql.Date.valueOf("2000-10-01"));
		person.setAmount(100);
		getAddressBookDAO().createPerson(person);
		person = new Person();
		person.setName("Tom Leith");
		person.setEmail("tom.leith@waituhi.com");
		person.setAddress("Waituhi Valley, Ruatane");
		person.setGender("M");
		person.setStartDate(java.sql.Date.valueOf("2000-10-01"));
		person.setAmount(100);
		getAddressBookDAO().createPerson(person);
		person = new Person();
		person.setName("George Leith");
		person.setEmail("george.leith@waituhi.com");
		person.setAddress("Waituhi Valley, Ruatane");
		person.setGender("M");
		person.setStartDate(java.sql.Date.valueOf("2000-10-01"));
		person.setAmount(100);
		getAddressBookDAO().createPerson(person);
		person = new Person();
		person.setName("Arthur Leith");
		person.setEmail("arthur.leith@waituhi.com");
		person.setAddress("Waituhi Valley, Ruatane");
		person.setGender("M");
		person.setStartDate(java.sql.Date.valueOf("2000-10-01"));
		person.setAmount(100);
		getAddressBookDAO().createPerson(person);
		person = new Person();
		person.setName("Edith Leith");
		person.setEmail("edith.leith@waituhi.com");
		person.setAddress("Waituhi Valley, Ruatane");
		person.setGender("F");
		person.setStartDate(java.sql.Date.valueOf("2000-10-01"));
		person.setAmount(100);
		getAddressBookDAO().createPerson(person);
		person = new Person();
		person.setName("Elizabeth Leith");
		person.setEmail("lizzy.leith@waituhi.com");
		person.setAddress("Waituhi Valley, Ruatane");
		person.setGender("F");
		person.setStartDate(java.sql.Date.valueOf("2000-10-01"));
		person.setAmount(100);
		getAddressBookDAO().createPerson(person);
		person = new Person();
		person.setName("William Leith");
		person.setEmail("bill.leith@waituhi.com");
		person.setAddress("Waituhi Valley, Ruatane");
		person.setGender("M");
		person.setStartDate(java.sql.Date.valueOf("2000-10-01"));
		person.setAmount(100);
		getAddressBookDAO().createPerson(person);
		person = new Person();
		person.setName("Alfred Leith");
		person.setEmail("alf.leith@waituhi.com");
		person.setAddress("Waituhi Valley, Ruatane");
		person.setGender("M");
		person.setStartDate(java.sql.Date.valueOf("2000-10-01"));
		person.setAmount(100);
		getAddressBookDAO().createPerson(person);
		person = new Person();
		person.setName("Ernest Leith");
		person.setEmail("ernie.leith@waituhi.com");
		person.setAddress("Waituhi Valley, Ruatane");
		person.setGender("M");
		person.setStartDate(java.sql.Date.valueOf("2000-10-01"));
		person.setAmount(100);
		getAddressBookDAO().createPerson(person);
	}
}
