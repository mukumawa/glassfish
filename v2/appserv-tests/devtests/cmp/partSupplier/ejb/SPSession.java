
package Data;

import javax.ejb.*;

/**
 * Created Dec 16, 2002 2:08:21 PM
 * Code generated by the Forte For Java EJB Builder
 * @author mvatkina
 */

public interface SPSession extends javax.ejb.EJBObject {
    
    public int checkAllParts() throws java.rmi.RemoteException;
    
    public int checkAllSuppliers() throws java.rmi.RemoteException;
    
    public void createPartsAndSuppliers() throws java.rmi.RemoteException;
    
    public void removePart(java.lang.Integer partid) throws java.rmi.RemoteException;
    
    public void removeSupplier(java.lang.Integer partid, java.lang.Integer supplierid) throws java.rmi.RemoteException;
    
}

