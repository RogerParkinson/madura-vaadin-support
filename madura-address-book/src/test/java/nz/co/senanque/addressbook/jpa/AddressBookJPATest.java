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

import static org.junit.Assert.assertEquals;

import java.util.List;

import nz.co.senanque.addressbook.instances.Person;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=ConfigJPA.class, loader=AnnotationConfigContextLoader.class)
public class AddressBookJPATest {

    @Autowired private AddressBookDAO m_addressBookDAO;
    
	@Test
	public void loadData() {

		List<Person> persons = getAddressBookDAO().getAllPersons();
		assertEquals(13,persons.size());
	}

	@Test
	public void test1() {
		
		List<Person> persons = getAddressBookDAO().getAllPersons();
		assertEquals(persons.size(),13);
		Object id = getAddressBookDAO().getId(persons.get(0));
		assertEquals(id,1L);
	}

	protected AddressBookDAO getAddressBookDAO() {
		return m_addressBookDAO;
	}

	protected void setAddressBookDAO(AddressBookDAO addressBookDAO) {
		m_addressBookDAO = addressBookDAO;
	}

}
