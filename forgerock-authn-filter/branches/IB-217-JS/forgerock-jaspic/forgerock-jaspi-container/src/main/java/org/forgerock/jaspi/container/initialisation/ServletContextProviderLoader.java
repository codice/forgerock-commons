package org.forgerock.jaspi.container.initialisation;

import javax.security.auth.message.config.AuthConfigFactory;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: jonathan
 * Date: 3/29/13
 * Time: 6:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServletContextProviderLoader implements AuthConfigProviderLoader {



    /**
     * Performs the loading and parsing of the AuthConfigProviders, to register, from a persistent declarative source
     * and registers them in the given AuthConfigFactory.
     *
     * @param authConfigFactory The AuthConfigFactory instance to register the AuthConfigProviders in.
     * @throws java.io.IOException If there is a problem loading the AuthConfigProvider registrations.
     */
    public void loadAuthConfigProviders(AuthConfigFactory authConfigFactory) throws IOException {



    }
}
