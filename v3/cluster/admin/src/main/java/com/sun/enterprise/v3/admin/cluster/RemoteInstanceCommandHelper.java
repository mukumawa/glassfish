/*
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package com.sun.enterprise.v3.admin.cluster;

import com.sun.enterprise.universal.i18n.LocalStringsImpl;
import org.glassfish.api.admin.ServerEnvironment;
import com.sun.enterprise.config.serverbeans.*;
import com.sun.grizzly.config.dom.NetworkListener;
import java.util.*;

/**
 * @author Byron Nevins
 * Everything is pkg-private or private in this class
 * 
 * Implementation Note:
 *
 * Ideally this class would be extended by AdminCommand's that need these
 * services.  The problem is getting the values out of the habitat.  The ctor
 * call would be TOO EARLY  in the derived classes.  The values are injected AFTER
 * construction.  We can't easily inject here -- because we don't want this class
 * to be a Service.
 * We could do it by having the derived class call a set method in here but that
 * gets very messy as we have to make sure we are in a valid state for every single
 * method call.
 *
 */

final class RemoteInstanceCommandHelper {
    RemoteInstanceCommandHelper(ServerEnvironment env0, Servers servers0, Configs configs0) {
        env = env0;
        // get rid of the annoying extra level of indirection...
        configs = configs0.getConfig();
        servers = servers0.getServer();
    }

    final boolean isDas() {
        return env.isDas();
    }

    final boolean isInstance() {
        return env.isInstance();
    }

    final int getAdminPort(final String serverName) {
        return getAdminPort(getServer(serverName));
    }

    final Server getServer(final String serverName) {
        for(Server server : servers) {
            final String name = server.getName();

            // ??? TODO is this crazy?
            if(serverName == null) {
                if(name == null) // they match!!
                    return server;
            }
            else if(serverName.equals(name))
                return server;
        }
        return null;
    }

    final int getAdminPort(Server server) {
        if(server == null)
            return -1; // get out quick.  it is kosher to call with a null Server

        try {
            Config config = getConfig(server);

            if(config != null) {
                List<NetworkListener> listeners = config.getNetworkConfig().getNetworkListeners().getNetworkListener();

                for(NetworkListener listener : listeners) {
                    if("admin-listener".equals(listener.getProtocol()))
                        return Integer.parseInt(listener.getPort());
                }
            }
        }
        catch (Exception e) {
            // handled below...
        }
        return -1;
    }
    
    ///////////////////////////////////////////////////////////////////////////
    //  All private below.  If you need something below in a derived class then
    // upgrade to pkg-private and move it above this line.  Change the keyword
    // private to final on the method
    ///////////////////////////////////////////////////////////////////////////
    

    private Config getConfig(final Server server) {
        // multiple returns makes this short method more readable...
        if(server == null)
            return null;

        String cfgName = server.getConfigRef();

        if(cfgName == null)
            return null;

        for(Config config : configs)
            if(cfgName.equals(config.getName()))
                return config;

        return null;
    }

    final private ServerEnvironment env;
    final private List<Server> servers;
    final private List<Config> configs;
    final private static LocalStringsImpl strings = new LocalStringsImpl(RemoteInstanceCommandHelper.class);
}
