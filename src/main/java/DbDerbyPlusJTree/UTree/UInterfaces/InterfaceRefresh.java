package DbDerbyPlusJTree.UTree.UInterfaces;

import DbDerbyPlusJTree.UTree.UExceptions.ExceptionSQLTextErr;
import DbDerbyPlusJTree.Utils.UFields;
import DbDerbyPlusJTree.Utils.UFieldsValues;
import javax.swing.JComponent;

/**
 *
 * @author gsedelnikov
 */
public interface InterfaceRefresh {
    
    public void refreshDependetObjectsUsingAllSelectionData(Object obj);
    public void refreshUsingAllSelectionData(UFieldsValues[] fvs);
    public Object[] getObjectsRefreshOnSelectionUsingAllSelectionData(); 
    public void setObjectsRefreshOnSelectionUsingAllSelectionData(Object[] objectsRefreshOnSelectionUsingAllSelectionData);
    
    public void refresh() throws ExceptionSQLTextErr;
    public void refresh(Object[] inId) throws ExceptionSQLTextErr;
    public void refreshDependetObjects(Object obj);
    public Object[] getObjectsRefreshOnSelection();
    public void setObjectsRefreshOnSelection(Object[] objectsRefreshOnSelection);
    
    public void setSchema(String schema);

    public UFields getFields();
    
    public UFieldsValues[] getValue();    
    public Object[] getValue(String field);
    
    /**
     * возвращает значение полного ключа в формате 
     * имя поля 1ой части ключа - значение - имя поля 2ой части ключа - значение - ...
     * @return 
     */
    public Object[] getValueForRefresh();    
    
    public void setDefRefreshData(Object[] defRefreshData);
    
//    public void setJCompKeyStroke(AbstractAction action, String actionMapKey, KeyStroke keyStroke);
    
    public JComponent[] getFocusedComponents();

    public void setId(int fct_id, int fcom_id); 
 
    public int getFcom_id();

    public int getFct_id();
    
}
