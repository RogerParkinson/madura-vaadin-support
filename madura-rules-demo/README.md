#madura-rules-demo#

This demo shows Vaadin and Madura Rules working together providing cross-field validation, dynamic enabling and disabling of fields, dynamic adjusting of choice lists to only offer valid choices in the current context.

Additionally the features offered by Madura Objects that you get without adding Madura Rules are also present, such as automatically adjusting captions and formats for locale, single field validation and disabling the submit buttons until the current dialog is complete and error-free.

Because we have the rules enabled you see fields appear and disappears, depending on what options are picked. They also switch between required and optional, with automatic implications for the submit button.

There is also a button enabled and disabled more directly by the rules. This is the BMI button which is only enabled if the name field contains 'fred'. The example is not very realistic but it shows the technique. The BMI button is configured to know about one boolean field. If it is true BMI is enabled, if false it is disabled. There is a rule that sets the field to true if the name is 'fred'. The important thing to remember is that neither the button nor the name field know about the rule, so there is no specific coding in the UI to change if the rules changes.

Finally there is an example of directed questioning. This uses the rules to work out what field we need to know about next and prompt for it. In this case we want to know the BMI which is calculated using the subject's weight and hieght, so we need to know those. But both of those items can be specified in imperial or metric units and the subject may, for example, know his weight in feet and inches but not metres. It is easier to ask in metric because that is only one prompt, but if the subject does not know the metric value the rules prompt for feet and inches (two fields). The example is very simple and only shows numeric inputs but each prompt is a fully monitored by Madura. So if the field is a choice list a list of valid options will present and so on, but only the fields the rules need to figure the BMI. Other fields will not be prompted.

More detailed documentation for this is found in the [madura-vaadin](../madura-vaadin/READ.me) project.

##Build and Run##

To compile the entire project, run "mvn install".
To run the application, run "mvn jetty:run" and open http://localhost:8080/

Or you can use Eclipse and WTP, which is our development environment, using Tomcat 7 and Java7

##Script##

login with admin/admin

The Customer tab has two required fields, one of the email. This is the same as the (madura-vaadin-demo)[../madura-vaadindemo/READ.me]. But it also has another button which is disabled for now.

Enter 'fred' into the Name field and the BMI button enables
This is driven from a rule, ie no UI coding needed.
Now press BMI and you will see a series of popup windows each asking for one field.
This is the directed questioning feature. The fields prompted for are rules driven and only fields needed to work out the BMI are asked for.

Now go to the Order tab
There are two Add Item buttons here. They do the same thing, ie they present a popup form that inputs a pizza specification.
There is a subtle difference bewteen the two, ie the last field is a read-only text field in one and a label in the other.
But they both show the dynamic nature of the fields. Pick Tpooing: Seafood and then check the size options. Only one, Small, right?
Now change the topping to Greek. There is still only one size but it is Large. You can only have small seafood pizzas and large Greek ones. Now clear the topping value and set the size to Large. Now check the toppings. There are two: Hawaiian and Greek. The choices change dynamically depending on wht else was picked.
The other thing that is changing is the Amount which varies by Size in this case. The field below that, testing, switches between invisible, required and optional depending on the other picks.
Finally, at the bottom, is the description field which includes the base-size-topping combination.
All of this is driven entirely by rules rather than custom UI code.
The dynamic changes to the drop downs ensure that only valid combinations of a potentially complex configuration can be specified.

The C2 tab is a variation on the Customer tab, the difference is in the programming.
The C3 tab is also similar but instead of submit and cancel buttons there is a menu bar with menu options. You can see the submit menu option enables and disables in the same way as the submit button does.
