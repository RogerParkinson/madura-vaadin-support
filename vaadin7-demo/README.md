vaadin7-demo
==

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/nz.co.senanque/madura-vaadin-support/badge.svg)](http://mvnrepository.com/artifact/nz.co.senanque/madura-vaadin-support)

[![build_status](https://travis-ci.org/RogerParkinson/madura-vaadin-support.svg?branch=master)](https://travis-ci.org/RogerParkinson/madura-vaadin-support)

(A more detailed document can be found at [Madura Vaadin (PDF)](http://www.madurasoftware.com/madura-vaadin.pdf)) 

This is a template for a simple Vaadin application that only requires a Servlet 3.0 container to run. The reason for including this is to give us a baseline of Vaadin without Madura. It is mostly Vaadin tutorial examples running with vaadin-spring. The rest of the (madura) applications use this as a template.


Workflow
--
To compile the entire project, run "mvn install" then deploy the war file to your favour servlet engine. Or you can use Eclipse and WTP, which is our development environment, using Tomcat 8 and Java8.

To develop the theme, simply update the relevant theme files and reload the application.
Pre-compiling a theme eliminates automatic theme updates at runtime - see below for more information.

Debugging client side code
  - run "mvn vaadin:run-codeserver" on a separate console while the application is running
  - activate Super Dev Mode in the debug window of the application

To produce a deployable production mode WAR:
- change productionMode to true in the servlet class configuration (nested in the UI class)
- run "mvn clean vaadin:compile-theme package"
  - See below for more information. Running "mvn clean" removes the pre-compiled theme.

Using a precompiled theme
-------------------------

When developing the application, Vaadin can compile the theme on the fly when needed,
or the theme can be precompiled to speed up page loads.

To precompile the theme run "mvn vaadin:compile-theme". Note, though, that once
the theme has been precompiled, any theme changes will not be visible until the
next theme compilation or running the "mvn clean" target.

When developing the theme, running the application in the "run" mode (rather than
in "debug") in the IDE can speed up consecutive on-the-fly theme compilations
significantly.

