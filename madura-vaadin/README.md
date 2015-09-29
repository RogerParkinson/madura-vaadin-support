#MaduraVaadinSupport#
	

MaduraVaadinSupport ties together all the back-end Madura projects (Madura Objects[[3]](https://github.com/RogerParkinson/madura-objects-parent) , Madura Rules[[4]](https://github.com/RogerParkinson/MaduraRules) , Madura Bundles[[5]](https://github.com/RogerParkinson/madura-bundles) ) and delivers them with a Vaadin UI[[2]](https://vaadin.com/home) . So it is worth taking a brief moment to review what those back-ends do:

 * Madura Objects builds your domain objects as annotated POJOs from an XSD file (using JAXB). The resulting objects behave just like POJOs except, when configured with the Madura Objects validation engine, they self validate as well as maintaining field metadata such as choice lists, permissions, labels etc.
 * Madura Rules plugs into the Madura Objects validation engine to support a rules/constraints based environment that does cross-field validation as well as deriving new values (eg total of the invoice lines on this invoice). It does 'truth maintenance' which means when the data changes rules might be 'unfired', keeping the derived data always 'true'. The rules can also operate on metadata which means they can change the list of valid choices on a choice field, make a field visible or read-only etc.
 * Madura Bundles builds sub-applications into jar files which can be published to maven or held on a local directory. These Bundles are hot loaded into a host application which sees only a Spring injected bean which supports an interface. You can deploy new versions of sub-applications on the fly.

This project wraps those tools to make them easy to use in a Vaadin application. The result delivers highly dynamic applications with very little application code. For example:

 * You can pass a POJO to a generic form which builds display fields for all the POJO fields. Each field is automatically validated according to specifications in the XSD (eg field length, numeric range checks) and error messages delivered where necessary. Required fields (again, as specfied in the XSD) are noted and the submit button is disabled until they are filled in, it is also disabled if there is an error. The generic form can accept a list of fields so you can specify which ones you want, you don't have to have them all.
 * All field captions are fetched from the relevant Madura label and translated to the current locale.
 * All validations on all fields are handled automatically. That means range checks, regex checks etc, but it also means rules based validation if you have configured it. The application code is unaware of this.
 * Error handling and delivery of any messages is synched with Vaadin's error message delivery and messages are locale translated.
 * Permissions are enforced. If this user only has read-only permission on a field it will be rendered but disabled. If they do not have read permission it will not be rendered. If no permissions are specified on the field it will be rendered normally, so you only add permissions to fields you care about.
 * Fields marked Secret are rendered as Password style fields.
 * Default values, if specified in the XSD, will be loaded in the obvious way.
 * Required fields are indicated as such. This is done with a red asterisk but is customisable.
 * The submit buttons disable themselves until all the required fields are all filled in. They also disable if there are any errors in the fields they are watching. Menu items are treated much the same as buttons, so a submit menu item will be disabled until the form is complete.
 * Any appropriate Vaadin controls can be used, not just the standard ones. All the usual theme features available in ordinary Vaadin applications are still in place. The main change is the use of a specialised FieldFactory which can be used on any form, not just the Madura generic form.

In addition, if you have configured a rules plugin into your validation engine:

 * The validation can include cross field validation.
 * Choice lists dynamically change as the available choices change.
 * Fields may change to/from read-only or invisible as rules fire.
 * One or more buttons may be tied to boolean fields that are, in turn, controlled by rules. These buttons become enabled or disabled depending on the current value of the boolean. Same with menu items.
 * Labels and read-only fields may contain data derived from rules. This automatically updates as the rules change the data.

Still with us? Good. In addition to all that we added some extra things that come in handy:

 * Support for Mobile applications using Touchkit[[12]](https://vaadin.com/add-ons/touchkit) and PhoneGap[[14]](https://vaadin.com/blog/-/blogs/packaging-vaadin-apps-for-home-screens-and-app-stores-with-phonegap) .
 * Support for SpringFrameworks[[1]](http://www.springframework.org) applications, which can be a bit awkward for Vaadin.
 * A view manager to make it easier to handle several Vaadin forms at once.
 * An extension of the Vaadin JPAContainer which supports @Transactional better. It also supports nice popup edit forms for each row. The edit forms use a validaton engine.
 * User permissions supplied through Spring Security. These are used to match against field permissions to see if this user can see this field.
 * Support for our perspectives manager which allows you to plug sub-applications into a perspectives framework. The result looks a bit like Eclipse perspectives. The sub-applications are delivered as bundles and can be hot loaded into the perspectives manager where they contribute menu items to the main menu bar.

The details of how to use all this are best explained by examples.

 * The address book demo[[6]](https://github.com/RogerParkinson/MaduraAddressbook) (not the same as the Vaadin demo of the same name). This one shows the extended JPAContainer in action, including the pop-up row editor, which has validation on its form.
 * The pizza order demo[[7]](https://github.com/RogerParkinson/MaduraPizzaOrderDemo) shows more advanced, ie rules based, validation. It has an on-line demo as well[[8]](http://pizzaorderdemo-madura.rhcloud.com/) 
 * The perspectives manager demo[[9]](https://github.com/RogerParkinson/MaduraPerspectivesManager) shows bundled sub-applications. It also has an on-line demo[[10]](http://perspectivesmanager-madura.rhcloud.com/) 

The main documentation draws on these demos for examples.

The code here is packaged as a vaadin-addon[[13]](https://vaadin.com/directory#!authoring/edit/addon=449) available from the Vaadin site.

Support Vaadin 7. However it still uses com.vaadin.ui.Form which is now deprecated. 