/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008-2010 Sun Microsystems, Inc. All rights reserved.
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

package com.sun.enterprise.admin.launcher;

import com.sun.enterprise.util.i18n.LocalStringsImpl;
import java.io.*;
import java.util.logging.*;

/**
 * A POL (plain old logger).  
 *
 * @author bnevins
 */

public class GFLauncherLogger {

    public static void info(String msg, Object... objs)
    {
        logger.info(strings.get(msg, objs));
    }
    public static void severe(String msg, Object... objs)
    {
        logger.severe(strings.get(msg, objs));
    }
    public static void fine(String msg, Object... objs)
    {
        logger.fine(strings.get(msg, objs));
    }

    /////////////////////////  non-public below  //////////////////////////////
    
    static synchronized void setConsoleLevel(Level level) {
        Logger parent = logger;
        
        while(parent != null) {
            Handler[] handlers = parent.getHandlers();
            
            for(Handler h : handlers) {
                if(ConsoleHandler.class.isAssignableFrom(h.getClass())) {
                    h.setLevel(level);
                }
            }

            parent = parent.getParent();
        }
    }
    /**
     * IMPORTANT!  
     * The server's logfile is added to the *local* logger.  But it is never
     * removed.  The files are kept open by the logger.  One really bad result
     * is that Windows will not be able to delete that server after stopping it.
     * Solution: remove the file handler when done.
     * @param logFile The logfile
     * @throws GFLauncherException if the info object has not been setup
     */
    static synchronized void addLogFileHandler(String logFile, GFLauncherInfo info) throws GFLauncherException
    {
        try
        {
            if (logFile == null || logfileHandler != null) {
                return;
            }
            logfileHandler = new FileHandler(logFile, true);
            logfileHandler.setFormatter(new SimpleFormatter());
            logfileHandler.setLevel(Level.INFO);
            logger.addHandler(logfileHandler);
        }
        catch(IOException e)
        {
            // should be seen in verbose mode for debugging
            e.printStackTrace();
        }

    }
    static  synchronized void removeLogFileHandler()  {
        if(logfileHandler != null) {
            logger.removeHandler(logfileHandler);
            logfileHandler.close();
            logfileHandler = null;
        }          
    }
    
    private GFLauncherLogger() {
    }
    private final static Logger logger;
    private final static LocalStringsImpl strings = new LocalStringsImpl(GFLauncherLogger.class);
    private static FileHandler logfileHandler;
    
    static  {
        logger = Logger.getLogger(GFLauncherLogger.class.getName());
        logger.setLevel(Level.INFO);
    }
}
