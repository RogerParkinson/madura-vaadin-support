/**
 * 
 */
package nz.co.senanque.vaadindemo;

import nz.co.senanque.addressbook.instances.Person;

/**
 * @author Roger Parkinson
 *
 */
public class PersonFactory {
	
	public static Person getPerson() {
		Person ret = new Person();
		ret.setId(0);
		ret.setName("");
		ret.setAddress("");
		ret.setEmail("");
		return ret;
	}

}
