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
package nz.co.senanque.vaadin.application;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * Preauthenticate the request.
 * It needs to check for credentials in the request and perform the login function.
 * Then it returns the user name (or null if it fails).
 * Note that this might be called multiple times in a session so ensure it is session scoped
 * and has a flag to suppress processing if the user is already authenticated
 * 
 * @author Roger Parkinson
 * @version $Revision:$
 */
public interface Preauthenticator
{

    String preauthenticate(HttpServletRequest request);

}
