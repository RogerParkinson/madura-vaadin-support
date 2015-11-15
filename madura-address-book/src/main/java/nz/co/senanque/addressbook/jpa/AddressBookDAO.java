/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package nz.co.senanque.addressbook.jpa;

import java.lang.reflect.Method;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;

import nz.co.senanque.addressbook.instances.Person;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
/**
 * DAO class for the address book.
 * 
 * @author Roger Parkinson
 *
 */
@Repository
public class AddressBookDAO {

	private EntityManager entityManager;

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager)
	{
	this.entityManager = entityManager;
	}

	/**
	* This method is used to create the user
	*/
	@Transactional
	public void createPerson(Person person)
	{
	entityManager.persist(person);
	entityManager.flush();
	}

	/**
	* This method is used to get all the user names from the table User Detail
	* @return list of user names
	*/
	@Transactional(readOnly=true)
	public List<Person> getAllPersons()
	{
	return entityManager.createQuery("from nz.co.senanque.addressbook.instances.Person", Person.class).getResultList();
	}

	/**
	* This method is used to delete an user
	* @param userDetail
	*/
	@Transactional
	public void deleteUser(Person Person)
	{
	entityManager.remove(Person);
	}
	
	/**
	 * Get the id value for this object. Assumes there is a single Id field, not a composite.
	 * 
	 * @param object
	 */
	public Object getId(Object object) {
		assert object != null : "object must not be null";
		for (Method method: object.getClass().getMethods()) {
			if (method.isAnnotationPresent(Id.class)) {
				try {
					return method.invoke(object, new Object[]{});
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}			
		}
		throw new RuntimeException("No Id field found on "+object.getClass().getName());
	}

}
