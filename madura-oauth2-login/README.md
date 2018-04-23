madura-oauth2-login
==

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/nz.co.senanque/madura-vaadin-support/badge.svg)](http://mvnrepository.com/artifact/nz.co.senanque/madura-vaadin-support)

[![build_status](https://travis-ci.org/RogerParkinson/madura-vaadin-support.svg?branch=master)](https://travis-ci.org/RogerParkinson/madura-vaadin-support)

(A more detailed document can be found at [Madura Vaadin (PDF)](http://www.madurasoftware.com/madura-vaadin.pdf)) 

Web fragment that provides authentication and authorization services for applications. It uses OAuth2 to outsource the authentication to an external server. It is very easy to convert an application using [madura-springsecurity-login](../madura-springsecurity-login/README.md) which changes the authentication from demo-ware to something closer to production, though you do have to deploy an authentication server.

The values here will change depending on your authentication server. These values work for the sample authentication server we tested against.
The public.txt file contains the <emph>public</emph> component of the key pair held in the authentication server.
You can find samples of both files in the vaadin7-demo project.
The server we tested this with is a simple spring-boot annotation configured authentication server[authserver](https://github.com/RogerParkinson/authserver). 
The idea is that you replace this sample server with your own. Depending on what authentication server you choose you may need to recode part of
the madura-oauth2-login library. The OAuth2 standard has variations. But you do have this proof of concept to start with.
As with madura-springsecurity-login the permissions are loaded from Spring ROLEs and that means every permission starts with 'ROLE_' eg ROLE_ADMIN etc.

The configuration is all in one properties file oauth.properties:

```
oauth2.client.scope=default
oauth2.client.grantType=code
oauth2.client.accessToken=http://acme:acmesecret@localhost:9999/uaa/oauth/token
oauth2.client.callback=http://localhost:8080/vaadin7-demo/login
oauth2.client.client-id=acme
oauth2.client.client-secret=acmesecret
oauth2.client.authorizationUri=http://localhost:9999/uaa/oauth/authorize
oauth2.client.keyPairName=test
```

Like [madura-springsecurity-login](../madura-springsecurity-login/README.md) this uses Spring Security so the permissions have to all start with 'ROLE\_' eg 'ROLE\_ADMIN'. These appear as ordinary Madura Permissions.

Unlike [madura-springsecurity-login](../madura-springsecurity-login/README.md) there is no opportunity to change the Locale on the login page, which is useful for demos but not so much in production.

A normal login should pop the auth server's login page (user=user, password=password), and then it displays the allow/deny page. Once you 'allow' the actual application UI appears.