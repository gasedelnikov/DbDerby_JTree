package DbDerbyPlusJTree.UTree.UInterfaces;

import DbDerbyPlusJTree.UTree.UExceptions.ExceptionSQLTextErr;
import DbDerbyPlusJTree.Utils.UFields;
import DbDerbyPlusJTree.Utils.UFieldsValues;
import javax.swing.ImageIcon;

/**
 *
 * @author gsedelnikov
 */
public interface InterfaceForUTrees {
        public void refresh(Object[] inId) throws ExceptionSQLTextErr;
        public void refresh() throws ExceptionSQLTextErr;
        public void setObjectsRefreshOnDoubleClick(Object[] dependetObjects);
        public void setObjectsRefreshOnSelection(Object[] dependetObjects);       
        public void setTreeMainProp(String sqlTextRoot, String sqlTextNodes, String sqlTextBrunch, Object[] in_fields, Object[] rootsID);
        public void setFields(Object[] in_fields);
        public void setExceptionMode(int exceptionMode);
        public void setSQL(String sqlTextRoot, String sqlTextNodes, String sqlTextBrunch);
        public void setTreeSelectionMode(int treeSelectionMode);
        public void setRootText(boolean rootShow, String rootText);
        public void setRootFieldsProp(Object[] rootsID);
        public void setIcons(Object[] nodeIcons) ;
        public void setDefIcon(ImageIcon DefIcon) ;
        public void setDefSelIcon(ImageIcon DefSelIcon);
        public void setDefCutIcon(ImageIcon DefCutIcon); 
        public void setDefCutSelIcon(ImageIcon DefCutSelIcon);
        public Object[]    getTreeRootsID();
        public UFields     getTreeFields();
        public Integer[]   getTreeInt(int index);   
        public Integer[]   getTreeInt(String fieldName) ;
        public String[]    getTreeString(int index);
        public String[]    getTreeString(String fieldName);
        public String      getTreeStringWithDelimiter(String strDelimiter);
        public void        refreshObjectOnSelection();
        public void        refreshObjectOnDoubleClick() ;
        public UFieldsValues[] getValue();
        public void setSchema(String schema);
        public void setExpandAll(boolean expandAll);
        public void setRootSelect(boolean rootAutoSelect);
        public int getFrm_id() ;
        public void setFrm_id(int frm_id);  
        
}
