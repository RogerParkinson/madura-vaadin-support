madura-vaadin-directed
==

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/nz.co.senanque/madura-vaadin-support/badge.svg)](http://mvnrepository.com/artifact/nz.co.senanque/madura-vaadin-support)

[![build_status](https://travis-ci.org/RogerParkinson/madura-vaadin-support.svg?branch=master)](https://travis-ci.org/RogerParkinson/madura-vaadin-support)

Library to provide directed questioning driven by the rules. This makes use of the rules to prompt for any additional fields required to solve for the value of a field. It only prompts for necessary fields, one at a time. You do not have to restructure the rules in any way to use this, it navigates the dependencies automatically from the existing rules. Fields that already have values are not re-prompted.

This is way to simplify a very complex user input process that has multiple pathways and a lot of fields that are only useful in  particular contexts and can be ignored in others.

A more detailed document can be found at [Madura Vaadin (PDF)](http://www.madurasoftware.com/madura-vaadin.pdf)

