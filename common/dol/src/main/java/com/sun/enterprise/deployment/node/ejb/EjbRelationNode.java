/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

/*
 * EjbRelationRole.java
 *
 * Created on February 1, 2002, 3:07 PM
 */

package com.sun.enterprise.deployment.node.ejb;

import java.util.Map;
import org.w3c.dom.Node;

import com.sun.enterprise.deployment.node.XMLElement;
import com.sun.enterprise.deployment.node.DeploymentDescriptorNode;
import com.sun.enterprise.deployment.node.LocalizedInfoNode;
import com.sun.enterprise.deployment.node.DescriptorFactory;

import com.sun.enterprise.deployment.Descriptor;
import com.sun.enterprise.deployment.RelationRoleDescriptor;
import com.sun.enterprise.deployment.RelationshipDescriptor;
import com.sun.enterprise.deployment.xml.EjbTagNames;

/**
 *
 * @author  dochez
 * @version 
 */
public class EjbRelationNode extends DeploymentDescriptorNode {

    RelationRoleDescriptor source = null;
    RelationRoleDescriptor sink = null;
    RelationshipDescriptor descriptor = null; 
    
    public EjbRelationNode() {
       super();
       registerElementHandler(new XMLElement(EjbTagNames.EJB_RELATIONSHIP_ROLE),
                                                            EjbRelationshipRoleNode.class);                   
    }
        
   /**
    * @return the descriptor instance to associate with this XMLNode
    */    
    public Object getDescriptor() {
        if (descriptor==null) {
            descriptor = (RelationshipDescriptor) DescriptorFactory.getDescriptor(getXMLPath());
        } 
        return descriptor;
    }        
    
    /**
     * Adds  a new DOL descriptor instance to the descriptor instance associated with 
     * this XMLNode
     *
     * @param descriptor the new descriptor
     */
    public void addDescriptor(Object newDescriptor) {
        if (newDescriptor instanceof RelationRoleDescriptor) {
            if (source==null) {
                source = (RelationRoleDescriptor) newDescriptor;
            } else {
                sink = (RelationRoleDescriptor) newDescriptor;
                
                descriptor.setSource(source);
                source.setPartner(sink);
                source.setRelationshipDescriptor(descriptor);
                descriptor.setSink(sink);
                sink.setPartner(source);
                sink.setRelationshipDescriptor(descriptor);

                if ( source.getCMRField() != null && sink.getCMRField() != null )
                    descriptor.setIsBidirectional(true);
                else
                    descriptor.setIsBidirectional(false);                
            }
        }
    }
        
    /**
     * all sub-implementation of this class can use a dispatch table to map xml element to
     * method name on the descriptor class for setting the element value. 
     *  
     * @return the map with the element name as a key, the setter method as a value
     */    
    protected Map getDispatchTable() {
        // no need to be synchronized for now
        Map table = super.getDispatchTable();
        table.put(EjbTagNames.EJB_RELATION_NAME, "setName");
        return table;
    }    

   /**
     * write the relationships descriptor class to a DOM tree and return it
     *
     * @param parent node in the DOM tree 
     * @param node name for the root element of this xml fragment      
     * @param the descriptor to write
     * @return the DOM tree top node
     */
    public Node writeDescriptor(Node parent, String nodeName, RelationshipDescriptor descriptor) {
        Node ejbRelationNode = super.writeDescriptor(parent, nodeName, descriptor);        
        writeLocalizedDescriptions(ejbRelationNode, descriptor);
        appendTextChild(ejbRelationNode, EjbTagNames.EJB_RELATION_NAME, descriptor.getName());     
        EjbRelationshipRoleNode roleNode = new EjbRelationshipRoleNode();
        roleNode.writeDescriptor(ejbRelationNode, EjbTagNames.EJB_RELATIONSHIP_ROLE, descriptor.getSource());
        roleNode.writeDescriptor(ejbRelationNode, EjbTagNames.EJB_RELATIONSHIP_ROLE, descriptor.getSink());
        return ejbRelationNode;
    }
}
