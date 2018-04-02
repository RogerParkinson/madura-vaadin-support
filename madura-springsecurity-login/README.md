madura-springsecurity-login
==

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/nz.co.senanque/madura-vaadin-support/badge.svg)](http://mvnrepository.com/artifact/nz.co.senanque/madura-vaadin-support)

[![build_status](https://travis-ci.org/RogerParkinson/madura-vaadin-support.svg?branch=master)](https://travis-ci.org/RogerParkinson/madura-vaadin-support)

(A more detailed document can be found at [Madura Vaadin (PDF)](http://www.madurasoftware.com/madura-vaadin.pdf)) 

Web fragment that provides authentication and authorization services for application.
Probably only useful for demos.

This is a rewrite of [madura-login](../madura-login/README.md) and supersedes that project. The differences:

 * Less customisation of the login page. You can set properties to change the wording etc but you can't supply a different login page.
 * Works on Chrome (the old one did not work on Chrome at all)
 * Uses Spring Security, which means all the permissions get 'ROLE\_' prepended eg 'ROLE\_ADMIN'
  
More detailed documentation for this is found in the [madura-vaadin](../madura-vaadin/README.md) project.

![Default login screen](../madura-vaadin/docs/images/Login.png)