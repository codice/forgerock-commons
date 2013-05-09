package org.forgerock.jaspi.container.modules;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.module.ServerAuthModule;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: jonathan
 * Date: 4/26/13
 * Time: 11:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class DummyAuthnModule implements ServerAuthModule {

    /**
     * Initialize this module with request and response message policies
     * to enforce, a CallbackHandler, and any module-specific configuration
     * properties.
     * <p/>
     * <p> The request policy and the response policy must not both be null.
     *
     * @param requestPolicy  The request policy this module must enforce,
     *                       or null.
     * @param responsePolicy The response policy this module must enforce,
     *                       or null.
     * @param handler        CallbackHandler used to request information.
     * @param options        A Map of module-specific configuration properties.
     * @throws javax.security.auth.message.AuthException
     *          If module initialization fails, including for
     *          the case where the options argument contains elements that are not
     *          supported by the module.
     */
    public void initialize(MessagePolicy requestPolicy, MessagePolicy responsePolicy, CallbackHandler handler, Map options) throws AuthException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Get the one or more Class objects representing the message types
     * supported by the module.
     *
     * @return An array of Class objects, with at least one element
     *         defining a message type supported by the module.
     */
    public Class[] getSupportedMessageTypes() {
        return new Class[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Authenticate a received service request.
     * <p/>
     * This method is called to transform the mechanism-specific request
     * message acquired by calling getRequestMessage (on messageInfo)
     * into the validated application message to be returned to the message
     * processing runtime.
     * If the received message is a (mechanism-specific) meta-message,
     * the method implementation must attempt to transform the meta-message
     * into a corresponding mechanism-specific response message, or to the
     * validated application request message.
     * The runtime will bind a validated application message into the
     * the corresponding service invocation.
     * <p/>
     * This method conveys the outcome of its message processing either
     * by returning an AuthStatus value or by throwing an AuthException.
     *
     * @param messageInfo    A contextual object that encapsulates the
     *                       client request and server response objects, and that may be
     *                       used to save state across a sequence of calls made to the
     *                       methods of this interface for the purpose of completing a
     *                       secure message exchange.
     * @param clientSubject  A Subject that represents the source of the
     *                       service
     *                       request.  It is used by the method implementation to store
     *                       Principals and credentials validated in the request.
     * @param serviceSubject A Subject that represents the recipient of the
     *                       service request, or null.  It may be used by the method
     *                       implementation as the source of Principals or credentials to
     *                       be used to validate the request. If the Subject is not null,
     *                       the method implementation may add additional Principals or
     *                       credentials (pertaining to the recipient of the service
     *                       request) to the Subject.
     * @return An AuthStatus object representing the completion status of
     *         the processing performed by the method.
     *         The AuthStatus values that may be returned by this method
     *         are defined as follows:
     *         <p/>
     *         <ul>
     *         <li> AuthStatus.SUCCESS when the application request message
     *         was successfully validated. The validated request message is
     *         available by calling getRequestMessage on messageInfo.</li>
     *         <p/>
     *         <li> AuthStatus.SEND_SUCCESS to indicate that validation/processing
     *         of the request message successfully produced the secured application
     *         response message (in messageInfo). The secured response message is
     *         available by calling getResponseMessage on messageInfo.</li>
     *         <p/>
     *         <li> AuthStatus.SEND_CONTINUE to indicate that message validation is
     *         incomplete, and that a preliminary response was returned as the
     *         response message in messageInfo.
     *         <p/>
     *         When this status value is returned to challenge an
     *         application request message, the challenged request must be saved
     *         by the authentication module such that it can be recovered
     *         when the module's validateRequest message is called to process
     *         the request returned for the challenge.</li>
     *         <p/>
     *         <li> AuthStatus.SEND_FAILURE to indicate that message validation failed
     *         and that an appropriate failure response message is available by
     *         calling getResponseMessage on messageInfo.</li>
     *         </ul>
     * @throws javax.security.auth.message.AuthException
     *          When the message processing failed without
     *          establishing a failure response message (in messageInfo).
     */
    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject) throws AuthException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Secure a service response before sending it to the client.
     * <p/>
     * This method is called to transform the response message acquired by
     * calling getResponseMessage (on messageInfo) into the mechanism-specific
     * form to be sent by the runtime.
     * <p/>
     * This method conveys the outcome of its message processing either
     * by returning an AuthStatus value or by throwing an AuthException.
     *
     * @param messageInfo    A contextual object that encapsulates the
     *                       client request and server response objects, and that may be
     *                       used to save state across a sequence of calls made to the
     *                       methods of this interface for the purpose of completing a
     *                       secure message exchange.
     * @param serviceSubject A Subject that represents the source of the
     *                       service
     *                       response, or null. It may be used by the method implementation
     *                       to retrieve Principals and credentials necessary to secure
     *                       the response. If the Subject is not null,
     *                       the method implementation may add additional Principals or
     *                       credentials (pertaining to the source of the service
     *                       response) to the Subject.
     * @return An AuthStatus object representing the completion status of
     *         the processing performed by the method.
     *         The AuthStatus values that may be returned by this method
     *         are defined as follows:
     *         <p/>
     *         <ul>
     *         <li> AuthStatus.SEND_SUCCESS when the application response
     *         message was successfully secured. The secured response message may be
     *         obtained by calling getResponseMessage on messageInfo.</li>
     *         <p/>
     *         <li> AuthStatus.SEND_CONTINUE to indicate that the application response
     *         message (within messageInfo) was replaced with a security message
     *         that should elicit a security-specific response (in the form of a
     *         request) from the peer.
     *         <p/>
     *         This status value serves to inform the calling runtime that
     *         (to successfully complete the message exchange) it will
     *         need to be capable of continuing the message dialog by processing
     *         at least one additional request/response exchange (after having
     *         sent the response message returned in messageInfo).
     *         <p/>
     *         When this status value is returned, the application response must
     *         be saved by the authentication module such that it can be recovered
     *         when the module's validateRequest message is called to process
     *         the elicited response.</li>
     *         <p/>
     *         <li> AuthStatus.SEND_FAILURE to indicate that a failure occurred while
     *         securing the response message and that an appropriate failure response
     *         message is available by calling getResponseMeessage on messageInfo.</li>
     *         </ul>
     * @throws javax.security.auth.message.AuthException
     *          When the message processing failed without
     *          establishing a failure response message (in messageInfo).
     */
    public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Remove method specific principals and credentials from the subject.
     *
     * @param messageInfo a contextual object that encapsulates the
     *                    client request and server response objects, and that may be
     *                    used to save state across a sequence of calls made to the
     *                    methods of this interface for the purpose of completing a
     *                    secure message exchange.
     * @param subject     the Subject instance from which the Principals and
     *                    credentials are to be removed.
     * @throws javax.security.auth.message.AuthException
     *          If an error occurs during the Subject
     *          processing.
     */
    public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
