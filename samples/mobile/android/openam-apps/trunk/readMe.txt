ForgeRock OpenAM / OAuth2.0 Demo Android Apps

- The following demonstrates an interface and basic implementation for communicating with a collection of OpenAM Rest endpoints through Android applications.

- These Apps and their supported libraries present an asynchronous Model-View-Presenter approach to building Android apps with support for OpenAM's REST interfaces. The supplied clients support basic functions such as logging in, requesting validity of a token, storing and offering the stored token out to other applications on the Android system through the content provider mechanism.

- There are two apps. The Authentication app [android_auth_app] is supported by its authentication client library [android_auth]. Likewise, the OAuth2.0 app [android_oauth2_app] is supported by its authorization client library [android_oauth]. Both applications are supported by the content-sharing library [android_auth_content] as well as the common REST library [android_commons].

The package breakdown is as follows:

- [android_commons]

Shared library used between both applications. This contains classes to allow users to generate their own clients using the base requests supplied. This library implements a simple series of asynchronous requests built from the Android-supplied AsyncTask and Apache HttpClient libraries. A number of supported requests are included (GetRestRequest, JSONRestRequest, etc.) see inside the individual files for more information.

The library provides a simple framework for using these asynchronous requests through a listener system, the core interfaces of which are simply Listener and Relay. In this system, Listeners are the endpoints (often Android Activities) which will finally act upon the information sent to them, and Relays act as intermediaries between the asynchronous request and the Listener endpoints. 

For example, in the supplied demo application, android_auth_app, the Request/Relay/Listener implementation resembles:

ASynchronousRequest [android_commons] created and executed by the AuthenticationClient [android_auth] which notifies the Presenter [android_auth_app] which notifies the appropriate Activity [android_auth_app].

In this model, the Presenter is able to perform some tasks on the response provided by the  Client before informing the UI to update. 

**The Client can either be made simple and passive (as in the current version) where all error-checking and validation of the response is the responsibility of the appropriate app's Presenter, but would be better smarter - that is, with the ability to look at responses and mark those which have failed as being in a fail state, so the presenter does not have to interrogate the returned objects itself.**

This system uses ActionTypes to determine which request has been performed. The ActionType interface is defined within [android_commons], and is used and extended by both [android_oauth2] and [android_auth] projects. These ActionTypes are used by the Listener and Relay system to inform the respective implementing classes.


- [android_auth]

Authentication client library. The core of this package is the AuthenticationClient along with its configuration class, OpenAMServerResource. The basic implementation of AuthenticationClient provides functionality to:

- Request the cookie domain from the OpenAM server - cookieName(), ActionTypes: GET_COOKIE_NAME, GET_COOKIE_NAME_FAIL
- Request the list of appropriate domains from the OpenAM server - cookieDomain(), ActionTypes: GET_COOKIE_DOMAIN, GET_COOKIE_DOMAIN_FAIL
- Attempt to start or continue an authentication process - authenticate(JSONObject data), ActionTypes: AUTH, AUTH_FAIL, AUTH_CONT
- Logout - logout(String token) - ActionTypes: LOGOUT, LOGOUT_FAIL
- Query if a token is valid - isTokenValid(String token) - ActionTypes: VALIDATE, VALIDATE_FAIL

Each of these methods uses the Listener/Relay mechanism provided from the [android_commons] libraries to asynchronously return the results, with the appropriate ActionType being set.

The client acts as a relay without altering the data contained by the response - it will however alter the ActionType returned if it's able to detect a failure (TODO).

- [android_oauth2]

Authorization client library. The core of this package is the AuthorizationClient along with its configuration class, OAuth2ServerResource. The basic implementation of AuthorizationClient provides functionality to:

- Convert an OAuth2.0 grant code into an access token - convertCodeToAccesToken(String code, String base, String ssoToken, String cookieName), ActionTypes: GET_TOKEN, GET_TOKEN_FAIL
- Read the authenticated user's profile from OpenAM - getProfile(String base, String accessToken, String cookieName, String ssoToken), ActionTypes: GET_PROFILE, GET_PROFILE_FAIL
- Validate an OAuth2.0 token - isAccessTokenValid(String base, String accessToken, String cookieName, String ssoToken), AcitonTypes: VALIDATE, VALIDATE_FAIL.

The OAuth2.0 client does not provide any functionality for getting the OAuth2.0 code from the OpenAM server. In the example application detailed below this initial step is done via a web view, following the authorization grant flow. To ensure this functionality is buitl in the same style as the rest of the application however, the supported ActionTypes are GET_CODE and GET_CODE_FAIL. 

The client acts as a relay without altering the data contained by the response - it will however alter the ActionType returned if it's able to detect a failure (TODO).

- [android_auth_content] 

This small library allows for the sharing of information between the two supplied applications. It is an implementation of a SQLite Content Provider, allowing for the sharing of a single SSO token between the Auth and OAuth2.0 apps. It is not recommended to be used as anything more than a simple demonstration of sharing a token.

- [android_auth_app]

The Android Activities and supporting Presenter used to authenticate the user and retrieve an SSO token. This SSO token is then stored and exposed using the [android_auth_content]. The application offers two mechanisms for logging in to OpenAM - the first through a native UI (described in more detail below) and the second through a web view interface. While the REST communication with the client is performed by the AuthenticatonClient provided by the [android_auth] package, the apps rendering of this information and interface is demonstrated in a number of ways.

First, through allowing the user to log in via a web view instead of through the native interface we demonstrate capturing an SSO token from a browser and storing it.

Second, the native UI implementation of the same login functionality has two implementations. The first of these is an adaptive implementation, the latter is a static one. 

The adaptive implementation - AuthenticateActivity - demonstrates how a single Activity can be used to draw the appropriate authentication module. In this case, the AuthenticateActivity supports the drawing of NameCallbacks and PasswordCallbacks. The Activity continually loops on itself, drawing the appropriate callbacks to the screen and passing their results to the OpenAM server via the AuthenticationClient until the Activity determines that it has either succeeded or failed in logging in (this process is documented in the code, and is performed through the Activity acting as a Listener and responding to the AUTH_CONT ActionType).

The static implementation - of which there is currently only a single authentication module coded, DataStoreActivity - demonstrates determining the returning authentication module and displaying a specific Activity in response. 

- [android_oauth2_app]

The Android Activities and supporting Presenter used to display an interface to the authentication client's basic functionality. The application allows the user to configure the OAuth2.0 client (client ID, client secret, etc.), and retrieves an OpenAM authentication token from the Auth App, as such this app requires the Auth App to function. This app uses a web view to perform the code retrieval part of the authorization grant process, and then uses the AuthorizationClient to transform the code into an access token. The code is documented to describe its mechanisms.

*** NOTES AND BUGS ***

Currently the clients are passive in their attitude to responses, and leave it up to the application's Presenter to determine whether the response was successful and respond accordingly. This will change so each client is aware of certain parts of fail-state information so it can mark known-failures as such directly through altering the ActionType.

- After initial setting of the Authentication application's settings for the first time, the application will close instead of returning to the home screen.
- Querying for an authorization token generates a new android activity instance without shutting down the old home screen activity, so multiple home screen activities occur in the task stack.