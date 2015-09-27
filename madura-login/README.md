madura-login
============

Web fragment that provides authentication and authorization services for application.
Probably only useful for demos.

Can be customised by:
 * adding a customised login.html to the resources directory
 * adding a customised login.css to resources/css
 * adding a logo.gif file to resources/images
 * adding a users.csv to WEB-INF. You actually need this file if you want anyone to log in.
 * deploying a different AuthenticationDelegate implementation as a bean.
 
Things a production version ought to have:
 * remember me
 * security timeout
 * forgot password, change password
  
To do:
 * review CSS, not sure about the blue writing
 * doesn't work at all on Chromium browser, ie doesn't redirect to application.
 * Is there a way to integrate css with Vaadin themes?