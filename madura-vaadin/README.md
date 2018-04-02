Madura Vaadin Support
==

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/nz.co.senanque/madura-vaadin-support/badge.svg)](http://mvnrepository.com/artifact/nz.co.senanque/madura-vaadin-support)

[![build_status](https://travis-ci.org/RogerParkinson/madura-vaadin-support.svg?branch=master)](https://travis-ci.org/RogerParkinson/madura-vaadin-support)

(A more detailed document can be found at [Madura Vaadin (PDF)](http://www.madurasoftware.com/madura-vaadin.pdf)) 

`madura-vaadin` ties together all the back-end Madura projects ([Madura Objects](https://github.com/RogerParkinson/madura-objects-parent) and [Madura Rules](https://github.com/RogerParkinson/madura-objects-parent/tree/master/madura-rules)) , and delivers them with a [Vaadin UI](https://vaadin.com/home) . So it is worth taking a brief moment to review what those back-ends do:

 * Madura Objects builds your domain objects as annotated POJOs from an XSD file (using JAXB). The resulting objects behave just like POJOs except, when configured with the Madura Objects validation engine, they self validate as well as maintaining field metadata such as choice lists, permissions, labels etc.
 * Madura Rules plugs into the Madura Objects validation engine to support a rules/constraints based environment that does cross-field validation as well as deriving new values (eg total of the invoice lines on this invoice). It does 'truth maintenance' which means when the data changes rules might be 'unfired', keeping the derived data always 'true'. The rules can also operate on metadata which means they can change the list of valid choices on a choice field, make a field visible or read-only etc.

This project wraps those tools to make them easy to use in a Vaadin application. The result delivers highly dynamic applications with very little application code. For example:

 * You can pass a POJO to a generic form which builds display fields for all the POJO fields. Each field is automatically validated according to specifications in the XSD (eg field length, numeric range checks) and error messages delivered where necessary. Required fields (again, as specified in the XSD) are noted and the submit button is disabled until they are filled in, it is also disabled if there is an error. The generic form can accept a list of fields so you can specify which ones you want, you don't have to have them all.
 * All field captions are fetched from the relevant XSD label and translated to the current locale.
 * All validations on all fields are handled automatically. That means range checks, regex checks etc, but it also means rules based validation if you have configured it. The application code is unaware any validation is active.
 * Error handling and delivery of any messages is synced with Vaadin's error message delivery and messages are locale translated.
 * Permissions are enforced. If this user only has read-only permission on a field it will be rendered but disabled. If they do not have read permission it will not be rendered. If no permissions are specified on the field it will be rendered normally, so you only add permissions to fields you care about.
 * Fields marked Secret are rendered as Password style fields.
 * Default values, if specified in the XSD, will be loaded in the obvious way.
 * Required fields are indicated as such using Vaadin defaults.
 * The submit buttons disable themselves until all the required fields are all filled in. They also disable if there are any errors in the fields they are watching. Menu items are treated much the same as buttons, so a submit menu item will be disabled until the form is complete.
 * Any appropriate Vaadin controls can be used, not just the standard ones. All the usual theme features available in ordinary Vaadin applications are still in place. The main change is the use of a specialised FieldFactory which can be used on any form or field group, though this is optional anyway.

In addition, if you have configured a rules plugin, such as Madura Rules, into your validation engine:

 * The validation can include cross field validation.
 * Choice lists dynamically change as the available choices change.
 * Fields may change to/from read-only or invisible or required as rules fire.
 * One or more buttons may be tied to boolean fields that are, in turn, controlled by rules. These buttons become enabled or disabled depending on the current value of the boolean.
 * Labels and read-only fields may contain data derived from rules. This automatically updates as the rules change the data.

Still with us? Good. We added some extra things that come in handy:

 * Support for Mobile applications using [Touchkit](https://vaadin.com/add-ons/touchkit) and [PhoneGap](https://build.phonegap.com) .
 * An extension of the Vaadin [JPAContainer](https://vaadin.com/directory#!addon/vaadin-jpacontainer) which supports `@Transactional` better. It also supports nice popup edit forms for each row. The edit forms use Madura Objects.
 * A login filter that pops a login dialog if this user is not yet logged in. We use this for demos rather than production, but it could be customised for production.

The details of how to use all this are best explained by examples.

 * [madura-vaadin-demo](https://github.com/RogerParkinson/madura-vaadin-support/tree/master/madura-vaadin-demo): This is a basic demo of Madura working with Vaadin. Specifically Madura Objects without Madura Rules.
 * [madura-rules-demo](https://github.com/RogerParkinson/madura-vaadin-support/tree/master/madura-rules-demo): This is the full demo that shows Madura Objects and Madura Rules working with Vaadin.
 * [madura-address-book](https://github.com/RogerParkinson/madura-vaadin-support/tree/master/madura-address-book): Demonstrates the extended JPA container and a pop-up row editor which has Madura Objects backing the fields.
 * [madura-mobile-demo](https://github.com/RogerParkinson/madura-vaadin-support/tree/master/madura-mobile-demo): Demonstrates an application that presents both a desktop and mobile UI, both backed by Madura Objects and Madura Rules.

There are [on-line demos](http://madurasoftware.com/?page_id=43) for all of these. 

