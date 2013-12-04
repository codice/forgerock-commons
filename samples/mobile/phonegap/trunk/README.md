This project demonstrates how to build a simple PhoneGap app using The OpenAM REST API.

Main frameworks and libraries used:
AngularJS 1.2.0   http://www.angularjs.org/ - (AngularSeed - https://github.com/angular/angular-seed)
PhoneGap 3.1.0    http://docs.phonegap.com/en/3.1.0/index.html
Bootstrap 3       http://getbootstrap.com/

The application is built using the AngularJS framework with PhoneGap used as a container to host the site within an internal webserver and webview inside of a device. The result is a native application containing a webview. The AngularJS application also works independently of PhoneGap as a single page web app within a browser .

If the application is opened from a web browser and the is hosted from a different domain than the OpenAM server the REST calls will generate cross domain errors. You must disable web security in the browser in order to access the REST API. This should only be used for development as it poses a security risk. To disable web security in Chrome use the flag win [chrome.exe --disable-web-security] or [$ open -a Google\ Chrome --args --disable-web-security] from the command prompt or terminal window. 

Install and set up PhoneGap and AngularSeed using http://projectpoppycock.com/angularjs-phonegap-and-angular-seed-lets-go/

To stop PhoneGap timeout errors, amend the www/config.xml file to <content src="boot.html" /> and create a tiny boot.html file. 

