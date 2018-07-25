package DbDerbyPlusJTree.UTree;

import DbDerbyPlusJTree.DB.Database;
import DbDerbyPlusJTree.UTree.UExceptions.ExceptionSQLTextErr;
import DbDerbyPlusJTree.UTree.UExceptions.UExceptionSQLTextErr;
import DbDerbyPlusJTree.UTree.UExceptions.UExceptions;
import DbDerbyPlusJTree.Utils.UFieldsValues;
import DbDerbyPlusJTree.Utils.UFields;
import DbDerbyPlusJTree.UTree.UEvents.UTreeRefreshEvent;
import DbDerbyPlusJTree.UTree.UInterfaces.InterfaceComponentSelectRegime;
import DbDerbyPlusJTree.UTree.UInterfaces.InterfaceForUTrees;
import DbDerbyPlusJTree.UTree.UInterfaces.InterfaceRefresh;
import DbDerbyPlusJTree.UTree.UInterfaces.UTreeDoubleClickListener;
import DbDerbyPlusJTree.UTree.UInterfaces.UTreeRefreshListner;
import DbDerbyPlusJTree.UTree.UInterfaces.UTreeSelectListener;
import UTree.UEvents.UTreeUnivEvent;
import java.awt.event.MouseEvent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.ImageIcon;
import java.util.Map;
import java.util.TreeMap;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreeSelectionModel;
import DbDerbyPlusJTree.Utils.arrayOfObjects;
import java.awt.Point;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTextArea;

/**
 * Элементарный (Родительский) Класс для всех видов деревьев наследник от JTree
 * @author gsedelnikov
 */
 public class UTreeSimple extends JTree  implements InterfaceRefresh, InterfaceForUTrees, InterfaceComponentSelectRegime, Serializable {     
// treeSelectionMode           - Определение типа выделения узлов в дереве     
// iconIndex                   - Переменная определяющая индекс поля в запросе отвечающего за иконку (Опред. автоматически)
// exceptionMode               - Определение уровня обработки ошибок  
// sqlTextRoot                 - Запрос для заполнения корневых узлов
// sqlTextNodes                - Запрос для потомков
// sqlTextBrunch               - Запрос для построения иерархии (поиск нужного элемента)
// rootText                    - Заголовок корня дерева
// rootsID                     - Параметры (значения) для выполнения запроса заполнения дерева
// treeFields                  - Определение типов полей в запросах r_Id, r_Text, r_Parent
// rootShow                    - Индикатор необходимости отображения корня дерева
// defIcon                     - Иконка по умолчанию
// defSelIcon                  - Иконка выбранного элемента по умолчанию
// defCutIcon                  - Иконка вырезанного элемента по умолчанию
// defCutSelIcon               - Иконка выбранного вырезанного элемента по умолчанию     
// objectsRefreshOnClick       - Объекты, обновляемые при выделении
// objectsRefreshOnDoubleClick - Объекты, обновляемые двойном клике
// iconMap                     - Карта иконок дерева
    protected int                    treeSelectionMode             = TreeSelectionModel.SINGLE_TREE_SELECTION;
    protected int                    iconIndex                     = -1;
    protected int                    exceptionMode                 = UExceptions.exceptionModeDef;    
    protected String                 schema                        = null;    
    protected String                 sqlTextRoot                   = "SELECT MLT_ID, CLF_ID, CLS_ID, CODE||' - '||NAME as TEXT, PARENT FROM CLS WHERE MLT_ID = :MLT_ID AND CLF_ID = :CLF_ID AND CLS_CLS_ID IS NULL  ORDER BY CODE";    
    protected String                 sqlTextNodes                  = "SELECT MLT_ID, CLF_ID, CLS_ID, CODE||' - '||NAME as TEXT, PARENT FROM CLS WHERE MLT_ID = :MLT_ID AND CLF_ID = :CLF_ID AND CLS_CLS_ID = :CLS_ID  ORDER BY CODE";    
    protected String                 sqlTextBrunch                 = "SELECT MLT_ID, CLF_ID, CLS_ID, CODE||' - '||NAME as TEXT, PARENT FROM CLS A START WITH A.MLT_ID = :MLT_ID AND A.CLF_ID = :CLF_ID AND A.CLS_ID = :CLS_ID CONNECT BY A.MLT_ID = :MLT_ID AND PRIOR A.CLF_ID = A.CLF_ID AND PRIOR A.CLS_CLS_ID = A.CLS_ID ORDER BY level desc";  
    protected String                 rootText                      = "Классификация";
    protected Object[]               rootsID                       = new Object[]{"MLT_ID", 1, "CLF_ID", 6};
    protected Object[]               defRefreshData;    
    protected UFields                fields                        = new UFields(new Object[]{  "MLT_ID", UFields.r_Id
                                                                                              , "CLF_ID", UFields.r_Id
                                                                                              , "CLS_ID", UFields.r_Id
                                                                                              , "TEXT"  , UFields.r_Text
                                                                                              , "PARENT", UFields.r_Parent}); 
    protected boolean                expandAll                     = false;
    protected boolean                rootSelect                    = false;
    protected boolean                rootShow                      = false;
    protected Icon                   defIcon                       ;
    protected Icon                   defSelIcon                    ;
    protected Icon                   defCutIcon                    ;
    protected Icon                   defCutSelIcon                 ;
    protected Object[]               objectsRefreshOnSelection;    
    protected Object[]               objectsRefreshOnDoubleClick;    
    protected Object[]               objectsRefreshOnSelectionUsingAllSelectionData;    
    protected Map<String,ImageIcon>  iconMap                       = new TreeMap(); 
    protected JPopupMenu             treePopupMenu;
    protected AbstractAction[]       treeAbstractAction;
    protected DefaultTreeModel       uTreeModel;
    protected String                 strInnerFilter                = null;
    
    protected boolean isNotInit = true;
    
    protected boolean            isSourceDnD = false;
    protected boolean            isTargetDnD = false;
    protected boolean            refreshSourceWhenDropSuccess = false;    
    protected boolean            refreshTargetWhenDropSuccess = false;    
   
    protected String[]           sourceTags = new String[]{"sourceDefTag"};
    protected String[]           targetTags = new String[]{"targetDefTag"};

    protected UFieldsValues[]      cuttedNode = null;
    private   int                  onDroppedOpenFrmId = -1;
    private   String               onDroppedProcedure = null;
   
    private int frm_id = -1;
    private int fcom_id = -1;    
    private int fct_id = -1;      
    /**
     * Пустой конструктор элементарного дерева
     */    
    public UTreeSimple()  {   
      super();
    }    

    /**
     * Основной конструктор элементарного дерева
     * @param sqlTextRoot Запрос для заполнения корневых узлов
     * @param sqlTextNodes Запрос для потомков
     * @param sqlTextBrunch Запрос для построения иерархии (поиск нужного элемента)
     * @param in_fields Определение типов полей в запросах
     * @param rootsID Значения параметров для определения корневых узлов
     */
    public UTreeSimple(String sqlTextRoot, String sqlTextNodes, String sqlTextBrunch, Object[] in_fields, Object[] rootsID) {
      super();

      this.sqlTextNodes = sqlTextNodes;
      this.sqlTextRoot = sqlTextRoot;        
      this.sqlTextBrunch = sqlTextBrunch;        
      this.fields = new UFields(in_fields);
//      this.rootsID = new Integer[rootsID.length];
      this.rootsID = rootsID;      
    }  
    /**
     * Инициализация основных параметров элементарного дерева
     * @param sqlTextRoot Запрос для заполнения корневых узлов
     * @param sqlTextNodes Запрос для потомков
     * @param sqlTextBrunch Запрос для построения иерархии (поиск нужного элемента)
     * @param in_fields Определение типов полей в запросах
     * @param rootsID Значения параметров для определения корневых узлов
     */
    @Override
    public void setTreeMainProp(String sqlTextRoot, String sqlTextNodes, String sqlTextBrunch, Object[] in_fields, Object[] rootsID) {
      this.sqlTextNodes = sqlTextNodes;
      this.sqlTextRoot = sqlTextRoot;        
      this.sqlTextBrunch = sqlTextBrunch;        
      this.fields = new UFields(in_fields);
      this.rootsID = rootsID;
    }     
    /**
    /** Определение AbstractAction (начинки) для Контекстного меню 
     * @param treeAbstractAction - AbstractAction для Контекстного меню
     */
    public void setAbstractAction(AbstractAction[] treeAbstractAction) {
      this.treeAbstractAction = treeAbstractAction;
    } 
 
    /**
     * После обновления происходит позиционирование на ранее выделенных узлах
     * @param inId Массив значений идентификаторов согласно массиву полей запросов
     * @throws ExceptionSQLTextErr Ошибки выполнения запроса Oracle 
     */
    @Override
    public void refresh() throws ExceptionSQLTextErr{
      if (isNotInit) {
        isNotInit = false;
        init();
      }
            
      int SelectionRowsCount = this.getSelectionRows().length;
      
      if ((SelectionRowsCount != 0) && (sqlTextBrunch != null)) {
        TreePath[] TreePaths = new TreePath[SelectionRowsCount];
      
        for (int i = 0; i < SelectionRowsCount; i++){
           TreePaths = this.getSelectionPaths();
        }

        TreeFillRoot() ;        

        for (int i = 0; i < SelectionRowsCount; i++){
          DefaultMutableTreeNode dmt = (DefaultMutableTreeNode) TreePaths[i].getLastPathComponent();                
          UFieldsValues mto = (UFieldsValues) dmt.getUserObject();  
               
          if (! createTreeBrunch(mto)) {
            DefaultMutableTreeNode pNode = (DefaultMutableTreeNode) dmt.getParent();
            if (pNode != null){
              mto = (UFieldsValues) (pNode).getUserObject(); 
              createTreeBrunch(mto);
            }
          }
        }
      }
      else 
         TreeFillRoot();
      
      if (this.getRowCount() > 0)
        selectFirstRow();  
      
      refreshDependentObjectsIsEmpty();
    }     
    
  /**
     *  Происходит поиск и позиционирование на соответствующем узле в дереве
     * @param inData Массив значений полей идентификаторов и значений
     * @throws ExceptionSQLTextErr Ошибки выполнения запроса Oracle 
     */
    @Override
    public void refresh(Object[] inData) throws ExceptionSQLTextErr {
      if (isNotInit) {
        isNotInit = false;
        init();
      }        
        
        // Изменение значений в this.rootsID
      if (this.rootsID != null)  
        if (inData != null){
          for (int i =0; i < inData.length/2; i++){
            for (int j = 0; j < this.rootsID.length/2; j++) {
              if (inData[i*2].toString().equals(this.rootsID[j*2].toString()))
                this.rootsID[j*2+1] = inData[i*2+1];  
            }  
          }    
        }
        else{
          for (int j = 0; j < this.rootsID.length/2; j++) 
            this.rootsID[j*2+1] = null;              
        }
      
      TreeFillRoot() ;   
//      UFieldsValues mto = new UFieldsValues(this.fields);
      
      inData = arrayOfObjects.split(inData, this.defRefreshData); // дополнение данных для обновления из данных по умолчанию
      
      if (inData != null){
        UFieldsValues mto = new UFieldsValues(this.fields, inData);  
//        if (inData.length %2!=0) //Заглушка: Добавить норм. обработку
//            throw new IllegalArgumentException("Нечётное количество параметров");         
//      
//        for (int i =0; i < inData.length/2; i++){
//          int j = fields.getIndexByColumnName((String)inData[2*i]);  
//          if (j != -1) mto.SetValue(j, inData[2*i+1]);   
//        }  

        // проверка на заполненность всех полей типа isID      
        boolean allIdIn = true; 
        for (int i =0; i < this.fields.getLength(); i++){
          if (   (this.fields.isId(i))
              && (mto.getValue(i) == null))
            allIdIn = false;      
        }  
      
        if (allIdIn) {
          this.selectionModel.clearSelection();
          createTreeBrunch(mto); // получение пути к узлу и раскрытие всех родителей
        }
      }
      
      if ((this.getRowCount() > 0) && (this.getSelectionCount() == 0)) 
        selectFirstRow();   
      
      refreshDependentObjectsIsEmpty();
    }   
    
    
//    public void selectRowByValue(Object[] inData) {
//
//    
//    }      
    
    private void refreshDependentObjectsIsEmpty(){
      if (   (this == null)
          || (this.getRowCount() < (rootShow?2:1))){
          
    // обновление зависимых объектов
        if (this.objectsRefreshOnSelection != null){
            for (Object obj : this.objectsRefreshOnSelection) {
                refreshDependetObjects(obj);
            }           
        }    
          
        if (this.objectsRefreshOnSelectionUsingAllSelectionData != null){
           for (Object obj: this.objectsRefreshOnSelectionUsingAllSelectionData){ 
              refreshDependetObjectsUsingAllSelectionData(obj);   
           }   
        }   
      }  
    }
    
    /**
     * Установка свойств полей запросов и значений параметров корневых узлов
     * @param in_fields Определение типов полей в запросах
     */    
    @Override
    public void setFields(Object[] in_fields) {
       this.fields = new UFields(in_fields);
    } 
    
    @Override
    public UFields getFields() {
       return this.fields;
    }     
    
    /**
     * Установка свойств и значений параметров корневых узлов
     * @param rootsID Значения параметров для определения корневых узлов 
     */    
    @Override
    public void setRootFieldsProp(Object[] rootsID) {
       this.rootsID    = rootsID;       
    }     
    
    /**
     * Определение режима обработки исключительных ситуаций
     * @param exceptionMode Режим обработки исключительных ситуаций. 
     * Значения: 
     *<p> UExceptions.exceptionModeJustOnScreen (0)- Выдается сообщение на экран, исключительные ситуации не генерируются; 
     *<p> UExceptions.exceptionModeJustException (1) - Исключительные ситуации генерируются, сообщение на экран НЕ выдаются;
     *<p> UExceptions.exceptionModeBoth (2)- Выдается сообщение на экран, генерируются исключительные ситуации; 
     *<p> UExceptions.exceptionModeDef - Значение по умолчанию, определенное в проекте.
     */
    @Override
    public void setExceptionMode(int exceptionMode) {
       this.exceptionMode = exceptionMode;
    } 
    /**
    /** Определение запросов, необходимых для заполнения дерева
     * @param sqlTextRoot Запрос для заполнения корневых узлов
     * @param sqlTextNodes Запрос для потомков
     * @param sqlTextBrunch Запрос для построения иерархии (поиск нужного элемента)
     */
    @Override
    public void setSQL(String sqlTextRoot, String sqlTextNodes, String sqlTextBrunch) {
        this.sqlTextRoot = sqlTextRoot;
        this.sqlTextNodes = sqlTextNodes;
        this.sqlTextBrunch = sqlTextBrunch;
    }
    /**
     * Определение режима выделения узлов дерева
     * @param treeSelectionMode режим выделения узлов дерева
     */
    @Override
    public void setTreeSelectionMode(int treeSelectionMode) {
        this.treeSelectionMode = treeSelectionMode;
        this.getSelectionModel().setSelectionMode(treeSelectionMode);
    }
    
    /**
     * Определение режима выделения 
     * @param selectRegime режима выделения (0 - одиночное; 1 множественное)
     */
    @Override
    public void setComponentSelectRegime(int selectRegime){
       if (selectRegime == InterfaceComponentSelectRegime.singleSelectRegime){
         setTreeSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
       } 
       else {
         setTreeSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
       }
    }
    
    /**
     * Установка свойств корневой вершины дерева
     * @param rootShow Индикатор необходимости отображения корневой вершины дерева
     * @param rootText Заголовок корневой вершины дерева
     */     
    @Override
    public void setRootText(boolean rootShow, String rootText) {
        this.rootShow = rootShow;
        if (rootText != null) this.rootText = rootText;
    }
    /**
     * Определение Иконок дерева
     * @param nodeIcons Массив типа Object, нечетные элементы - значения поля запроса типа r_Icon, четные - иконка, соответствующая значению
     */
    @Override
    public void setIcons(Object[] nodeIcons) {
        iconMap.clear();
        if (   (nodeIcons != null)
            && (nodeIcons.length      > 0) 
            && (nodeIcons.length % 2 == 0)) {  

          for (int i =0; i < nodeIcons.length; i = i + 2) 
             iconMap.put(nodeIcons[i].toString(), (ImageIcon)nodeIcons[i+1]);   

          for (int i =0; i < fields.getLength(); i++)
               if (fields.isIcon(i)) 
                 iconIndex = i;        
        } 
        else iconIndex = -1;
    }  
    
    public void setIcons(int empty, Map<String,ImageIcon> iconMap) {
      this.iconMap = iconMap;
      iconIndex = -1;   
      if (!iconMap.isEmpty())
        for (int i =0; i < fields.getLength(); i++)
          if (fields.isIcon(i)) 
            iconIndex = i;        
    }    
    
    /**
     * Определение иконки по умолчанию
     * @param DefIcon иконка по умолчанию
     */
    @Override
    public void setDefIcon(ImageIcon DefIcon) {
        this.defIcon = DefIcon;
    }
     /**
      * Определение иконки выделенного узла по умолчанию
      * @param DefSelIcon иконка выделенный узла по умолчанию
      */
    @Override
     public void setDefSelIcon(ImageIcon DefSelIcon) {
         this.defSelIcon = DefSelIcon;
     }   
     /**
      * Определение иконки вырезанного узла по умолчанию
      * @param DefSelIcon иконка выразанный узла по умолчанию
      */
    @Override
     public void setDefCutIcon(ImageIcon DefCutIcon) {
         this.defCutIcon = DefCutIcon;
     } 
       
     /**
      * Определение иконки вырезанного выделенного узла по умолчанию
      * @param DefSelIcon иконка выразанный узла по умолчанию
      */
     @Override
    public void setDefCutSelIcon(ImageIcon DefCutSelIcon) {
         this.defCutSelIcon = DefCutSelIcon;
     }     
     /**
      * Определение вырезанного узла
      * @param cuttedNode вырезанный узел
      */
     public void setCuttedNode(UFieldsValues[] cuttedNode) {
         this.cuttedNode = cuttedNode;
     }     
    /**
     * Получение числовых значений выбранных узлов
     * @param index Порядковый номер поля запроса (начиная с нулевого)
     * @return Возвращает числовые значения поля index всех выделенных узлов.
     */
    @Override
    public Integer[] getTreeInt(int index) {
      int SelectionRowsCount = this.getSelectionRows().length;
      Integer[] retVal = null;
      
      if ((SelectionRowsCount != 0) && ((this.fields.getValtype(index) == UFields.t_Integer))){
        retVal = new Integer[this.getSelectionRows().length];
        
        for (int i=0; i < SelectionRowsCount; i++){
         int j = this.getSelectionRows()[i];   
         DefaultMutableTreeNode dmt = (DefaultMutableTreeNode) this.getPathForRow(j).getLastPathComponent();                
         UFieldsValues mto = (UFieldsValues) dmt.getUserObject();  
         retVal[i] = mto.getInteger(index);
        }
      }   
//      else 
//        retVal = new int[0];
                
      return retVal; 
    }  
  
    /**
     * Метод получения данных выделенных узлов
     * @return Данные выделенных узлов
     */
   @Override
   public UFieldsValues[] getValue() {
      UFieldsValues[] result;
      UFieldsValues adds = getTableDndAddsParams();  
      if (    (this.getSelectionRows() != null)&& (this.getSelectionRows().length != 0)){
        int size = this.getSelectionRows().length;  
        result = new UFieldsValues[size];  
          
        for (int i=0; i < this.getSelectionRows().length; i++){  
         DefaultMutableTreeNode dmt = (DefaultMutableTreeNode) this.getSelectionPaths()[i].getLastPathComponent();                
         result[i] =  (UFieldsValues) dmt.getUserObject();  
         result[i] = arrayOfObjects.split(adds, result[i]);         
        }
      }else{
          result=new UFieldsValues[]{adds};
      }
                
      return result; 
    }      
    
    private UFieldsValues getTableDndAddsParams(){
      List<Object> newFields = this.fields.getFieldsArray();
      UFieldsValues result = new UFieldsValues(new UFields(newFields.toArray()), new Object[]{});

      return result; 
    }    
    
    /**
     * Метод получения данных узла ближайщего к точке
     * @param p точка (Point)    
     * @return Данные ближайщего к точке узла
     */
    public UFieldsValues getValueAtPoit(Point p) {
      UFieldsValues result = null;
      TreePath parentpath = this.getClosestPathForLocation(p.x, p.y);
      if (parentpath != null){
        DefaultMutableTreeNode dmt = (DefaultMutableTreeNode) parentpath.getLastPathComponent();
        if (dmt != null)
          result = (UFieldsValues) dmt.getUserObject();  
      }          
      return result; 
    }    
    
    @Override
    public Object[] getValueForRefresh(){
      Object[] result;  
      
      if ((this.getSelectionRows() != null) && (this.getSelectionRows().length != 0)){
          DefaultMutableTreeNode dmt = (DefaultMutableTreeNode) this.getSelectionPaths()[0].getLastPathComponent();                
          UFieldsValues  fv = (UFieldsValues) dmt.getUserObject();           
          result = fv.createRefreshArrayByMask();  
      }else{
          result=rootsID;
      }  
      return result;
    }    
    
    @Override
    public Object[] getValue(String field) {
      Object[] result = null;
      
      UFieldsValues[] tmpFVs = getValue();
      if (tmpFVs != null){
        result = new Object[tmpFVs.length];  
        for (int i=0; i < tmpFVs.length; i++)  
          result[i] = tmpFVs[i].getValue(field); 
      }    
      
//      if ((this.getSelectionRows() != null)){
//        int size = this.getSelectionRows().length;  
//        result = new Object[size];  
//        
//        for (int i=0; i < size; i++){  
//         DefaultMutableTreeNode dmt = (DefaultMutableTreeNode) this.getSelectionPaths()[i].getLastPathComponent();                
//         UFieldsValues tmpFV = (UFieldsValues) dmt.getUserObject();          
//         result[i] = tmpFV.getValue(field); 
//        }
//      }
                
      return result; 
    }    
    
    
    /**
     * Получение числовых значений выбранных узлов по имени поля запроса
     * @param fieldName Имя поля запроса
     * @return Возвращает числовые значения поля всех выделенных узлов.
     */
    @Override
    public Integer[] getTreeInt(String fieldName) {
      Integer[] retVal = null;  
      int index = this.fields.getIndexByColumnName(fieldName);  
      if (index != -1)          
        retVal = this.getTreeInt(index); 
      
      return retVal;
    }      
    
    /**
     * Метод получения свойст полей дерева.
     * @return Свойсва полей дерева
     */
    @Override
    public UFields getTreeFields() {
      return this.fields; 
    }    
    
    /**
     * Метод получения значений полей корней дерева.
     * @return Значения полей корней дерева
     */
    @Override
    public Object[] getTreeRootsID() {
      return this.rootsID; 
    }      
    
    /**
     * Получение строковых значений выбранных узлов
     * @param index Порядковый номер поля запроса (начиная с нулевого)
     * @return Возвращает строковые значения поля index всех выделенных узлов.
     */ 
    @Override
    public String[] getTreeString(int index) {
//  String[] - усли выделено несколько узлов дерева       
      int SelectionRowsCount = this.getSelectionRows().length;
      String[] retVal;
      
      if (SelectionRowsCount != 0){
        retVal = new String[this.getSelectionRows().length];
        
        for (int i=0; i < SelectionRowsCount; i++){
         int j = this.getSelectionRows()[i];   
         DefaultMutableTreeNode dmt = (DefaultMutableTreeNode) this.getPathForRow(j).getLastPathComponent();                
         UFieldsValues mto = (UFieldsValues) dmt.getUserObject();  
         retVal[i] = (String)mto.getString(index);
        }
      }   
      else 
        retVal = new String[0];
                
      return retVal; 
    }  
    
    /**
     * Получение строковых значений выбранных узлов по имени поля запроса
     * @param fieldName Имя поля запроса
     * @return Возвращает строковых значения поля всех выделенных узлов.
     */
    @Override
    public String[] getTreeString(String fieldName) {
      String[] retVal = new String[0];  
      int index = this.fields.getIndexByColumnName(fieldName);  
      if (index != -1)          
        retVal = this.getTreeString(index); 
      
      return retVal;
    }     
    
    /**
     * Принудительное выделение (выделение первой строки если нет других выделений).
     */
    private void selectFirstRow(){  
      if (    (this.getRowCount() > 0)
           && (this.getSelectionRows() != null)  
           && (this.getSelectionRows().length == 0)){
        if (this.getRowCount() == 1)  
          this.addSelectionRow(0);       
        else        
          this.addSelectionRow((rootShow && (rootShow && !rootSelect))?1:0);       
      }   
    }
    
    /**
     * Получение составной строки текстов всех выбранных узлов
     * @param strDelimiter Разделитель
     * @return Возвращает строку, составленную из текстов всех выделенных узлов.
     */
    @Override
    public String getTreeStringWithDelimiter(String strDelimiter) {
      int SelectionRowsCount = this.getSelectionRows().length;
      String retVal = "";
      
      if (SelectionRowsCount != 0){
        for (int i=0; i < SelectionRowsCount; i++){
         int j = this.getSelectionRows()[i];   
         DefaultMutableTreeNode dmt = (DefaultMutableTreeNode) this.getPathForRow(j).getLastPathComponent();                
         UFieldsValues mto = (UFieldsValues) dmt.getUserObject();  
         
         if (i == SelectionRowsCount-1) 
               retVal = retVal + (String)mto.toString() ;
          else retVal = retVal + (String)mto.toString() + strDelimiter;            
        }
      } 
      return retVal; 
    }     
    /**
     * Инициализация основных параметров и событий дерева
     * @throws ExceptionSQLTextErr 
     */
    private void init() throws ExceptionSQLTextErr{

        if (this.defIcon != null) {
           NodeIconsRender tmpRender = new NodeIconsRender();
           this.setCellRenderer(tmpRender);
        }

        this.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent event) {
                TreeSelectionEvent(event);
            }
        });
        
        this.addTreeExpansionListener(new TreeExpansionListener() {
            @Override
            public void treeExpanded(TreeExpansionEvent event){
               try {
                      TreeExpandedEvent(event);}
               catch (Throwable e) {}
                       
            }
            @Override
            public void treeCollapsed(TreeExpansionEvent event) {
//                throw new UnsupportedOperationException("Not supported yet.");
            }
          }); 
        
        this.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent event) {
                   TreeMouseClicked(event); 
                }  
             });
     
        this.addOnTreeRefreshListner(new UTreeRefreshListner() {
            @Override
            public void actionPerformed(UTreeRefreshEvent myEvent) {
                try {
                    refresh();
                } catch (ExceptionSQLTextErr ex) {
//                    ex.printStackTrace();
                }
            }
        });  
        
        if (treeAbstractAction != null) {
          treePopupMenu = new JPopupMenu();
          for (int i = 0; i < treeAbstractAction.length; i++)
            treePopupMenu.add(treeAbstractAction[i]) ;           
        }
// *****************************************************************************        
// проверка на наличие в полях запроса Идентификатора, Текста, и Инф. о потомках        
        boolean notExistId     = true;
        boolean notExistText   = true;
        boolean notExistParent = true;
        for (int i = 0; i < this.fields.getLength(); i++){
          if (this.fields.isId(i))     notExistId     = false; 
          if (this.fields.isText(i))   notExistText   = false; 
          if (this.fields.isParent(i)) notExistParent = false; 
        }
        
        UExceptions ww;
        if ((notExistId) || (notExistText) || (notExistParent))
          ww = new UExceptionSQLTextErr("UTreeSimple", "errInitTreeFields", "UTreeSimple", this.exceptionMode);
// *****************************************************************************        
    }
    /**
     * Определение компонентов, которые необходимо обновлять при двойном клике
     * @param dependetObjects Массив компонентов
     */
    @Override
    public void setObjectsRefreshOnDoubleClick(Object[] dependetObjects) {
        this.objectsRefreshOnDoubleClick = dependetObjects;
    }
    /**
     * Определение компонентов, которые необходимо обновлять при выделении узла
     * @param dependetObjects Массив компонентов
     */
    @Override
    public void setObjectsRefreshOnSelection(Object[] dependetObjects) {
        this.objectsRefreshOnSelection = dependetObjects;
    }

    @Override
    public Object[] getObjectsRefreshOnSelection() {
        return this.objectsRefreshOnSelection;
    }
    
    /**
     * Прорисовка иконок дерева
     */
    private class NodeIconsRender extends DefaultTreeCellRenderer {
      @Override
      public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component retValue;
        retValue = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        Icon tmpIcon = null;
        DefaultMutableTreeNode dmt = (DefaultMutableTreeNode) value;
        UFieldsValues mto = (UFieldsValues) dmt.getUserObject();        
        if (iconIndex != -1) {            
          String icon = mto.getString(iconIndex);  
            
          if (icon != null) 
            tmpIcon = iconMap.get(icon);
        }

        if (tmpIcon == null) 
            if (!hasFocus){
              if (isCutted(mto))
                  tmpIcon = defCutIcon;
              else
                  tmpIcon = defIcon;           
            } else {
              if (isCutted(mto))
                  tmpIcon = defCutSelIcon;
              else
                  tmpIcon = defSelIcon;  
            }
        
        setIcon(tmpIcon);
        return retValue;
      }
    }
    
    private boolean isCutted(UFieldsValues mto){
        boolean result = false;
        if (cuttedNode != null) {
            for (UFieldsValues ufvCut : cuttedNode) {
                if (mto.compareTreeObjects(ufvCut)) {
                    result = true;
                }
            }
        }
        return result;        
    }
    
    /**
     * Добавление в дерево корневых узлов
     * @throws ExceptionSQLTextErr 
     */ 
    protected void TreeFillRoot() throws ExceptionSQLTextErr {
      UFieldsValues mto = new UFieldsValues(this.fields);
      if (this.rootsID != null)
        for (int i = 0; i < this.rootsID.length/2; i++) { 
          int j =  this.fields.getIndexByColumnName((String)this.rootsID[i*2]);
          if (j != -1) mto.SetValue(j, this.rootsID[i*2+1]);
        }
      mto.SetText(rootText);
        
//      UFieldsValues mto = new UFieldsValues(this.rootText, rootsID, this.treeFields);  
      DefaultMutableTreeNode root   = new DefaultMutableTreeNode(mto, true);
      uTreeModel = new DefaultTreeModel(root, true);
      this.setModel(uTreeModel);
      this.setRootVisible(this.rootShow); 
      this.setShowsRootHandles(true);  
      this.getSelectionModel().setSelectionMode(this.treeSelectionMode);

      String sqlText = this.sqlTextRoot;
      
      addChildToNode(sqlText, root, mto.getРaramsFromSql(sqlText, false));
    }
    /**
     * Обработчик события раскрытия узла дерева
     * @param evt Событие раскрытия узла дерева
     * @throws ExceptionSQLTextErr 
     */
    private void TreeExpandedEvent(TreeExpansionEvent evt) throws ExceptionSQLTextErr{
      DefaultMutableTreeNode parent = (DefaultMutableTreeNode) evt.getPath().getLastPathComponent();
    
      if ((parent.getAllowsChildren() == true)
             && (parent.getChildCount() == 0))
        TreeExpanding(parent);

}    
    /**
     * Процедура раскрытия узла дерева
     * @param parent Узел который необходимо раскрыть
     * @throws ExceptionSQLTextErr 
     */
    protected void TreeExpanding(DefaultMutableTreeNode parent) throws ExceptionSQLTextErr {
      UFieldsValues mto = (UFieldsValues) parent.getUserObject();        
      String sqlText = this.sqlTextNodes;

      addChildToNode(sqlText, parent, mto.getРaramsFromSql(sqlText));
    }      
    
    /**
     * Процедура добавления узлов в дерево
     * @param sqlText Запрос для получения данных
     * @param parent Родительский класс
     * @param sqlParams
     * @throws ExceptionSQLTextErr 
     */
    protected void addChildToNode(String sqlText, DefaultMutableTreeNode parent, Object[] sqlParams) throws ExceptionSQLTextErr{

     try (ResultSet rs = Database.getResultSet(sqlText, sqlParams)){
           while (rs.next()) {
             UFieldsValues mto = createUTreeObjectFromSQL(rs);           
             DefaultMutableTreeNode child   = new DefaultMutableTreeNode(mto, mto.getNodeHasChild());
             uTreeModel  =(DefaultTreeModel) this.getModel();
             uTreeModel.insertNodeInto(child, parent, parent.getChildCount());     
             
             if (expandAll)
                this.expandPath(new TreePath(child.getPath()));

           }
      } catch (Exception ex) {
         UExceptions ww = new UExceptionSQLTextErr("UTreeSimple" , "errSqlNode", "UTreeSimple", this.exceptionMode);
      }        
    }
    /**
     * Процедура построения ветки иерархии
     * @param in_mto UFieldsValues на основе которого происходит построение ветки иерархии
     * @return Успешность построения всей ветки иерархии
     * @throws ExceptionSQLTextErr 
     */
    protected boolean createTreeBrunch(UFieldsValues in_mto) throws ExceptionSQLTextErr {
      String sqlText = this.sqlTextBrunch;
      boolean createTreeBrunch = false;
      int[] nodes = null;
      int lastNode = -1;
      try (ResultSet rs = Database.getResultSet(sqlText, in_mto.getРaramsFromSql(sqlText, false))){
           while (rs.next()) {
             if (rs.getRow() == 1)
               createTreeBrunch = true;                 
               
             UFieldsValues mto = createUTreeObjectFromSQL(rs);
             nodes =  findNodeByDate(mto);
             if ((nodes == null) || (nodes.length == 0)){
               createTreeBrunch = false;  
             }
             else{
               for (int i=0; i < nodes.length; i++){
                if (nodes[i] != -1){   
                  this.expandRow(nodes[i]); 
                   if (nodes[i] > lastNode) 
                     lastNode = nodes[i];
                } 
               }
             }
           }
       } catch (Exception ex) {
            createTreeBrunch = false;
            UExceptions ww = new UExceptionSQLTextErr("UTreeSimple", "errSqlTreeErarh", "UTreeSimple", this.exceptionMode);
       }        

      if (lastNode >= (rootShow?1:0)) { // исключается выделение корня
        if ((nodes != null) && (nodes.length >0)){
         this.addSelectionRows(nodes);   
         this.scrollRowToVisible(lastNode);
        }
        else{
         this.addSelectionRow(0);   
         this.scrollRowToVisible(0);
            
        }
      }  
      
      return createTreeBrunch;
    }   
    /**
     * Процедура создания UFieldsValues на основе строки запроса
     * @param rs строка запроса
     * @return UFieldsValues
     */
    protected UFieldsValues createUTreeObjectFromSQL(ResultSet rs) throws ExceptionSQLTextErr {
      UFieldsValues mto = new UFieldsValues(this.fields);
      try {
        for (int i = 0; i < this.fields.getLength(); i++) { 
          switch (fields.getValtype(i)){
            case UFields.t_Integer:{
               mto.SetValue(i, rs.getInt(fields.getColumnName(i)));
               break;}  
            case UFields.t_String:{
               mto.SetValue(i, rs.getString(fields.getColumnName(i)));
               break;}  
            case UFields.t_Date:{
               mto.SetValue(i, rs.getDate(fields.getColumnName(i)));
               break;} 
          }
        }
      } 
      catch (Exception ex) {
            UExceptions ww = new UExceptionSQLTextErr("UTreeSimple", "errConvertFields", "UTreeSimple", this.exceptionMode);
      } 
    return mto;   
   }
    /**
     * Процедура поиска узла по UFieldsValues
     * @param in_mto Искомый UFieldsValues
     * @return Номер стоки дерева
     */
   protected int[] findNodeByDate(UFieldsValues in_mto) {
      List <Integer> selectedRows = new ArrayList<>();       
      int  tRowCount = this.getRowCount();
      int i = 0;
//      boolean notFound = true;
      DefaultMutableTreeNode dmt;           
      
      if (tRowCount != 0 )
        while (i < tRowCount) {
          dmt = (DefaultMutableTreeNode) this.getPathForRow(i).getLastPathComponent();   
          if (in_mto.compareTreeObjects ((UFieldsValues) dmt.getUserObject()))
             selectedRows.add(i);
          i++;
        } 
      
      return arrayOfObjects.toIntArray(selectedRows);
    }
    /**
    * Обработчик события нажатия кнопки мышки
    * @param event Событие нажатия кнопки мышки
    */
   private void TreeMouseClicked(MouseEvent event) {
       int row = this.getClosestRowForLocation(event.getX(), event.getY());
       
       if (treePopupMenu != null) //                    Rigth mouse click  
         if (SwingUtilities.isRightMouseButton(event)) {  
             if (!event.isControlDown())      
                    this.selectionModel.clearSelection(); 
             this.addSelectionRow(row); 
             treePopupMenu.show(this, event.getX(), event.getY()); 
         } 

       if (event.getClickCount() > 1) { //  создание события двойного клика для потомков данного класса   
         refreshObjectOnDoubleClick();
//       System.out.println(row+" "+event.getClickCount());
//         if (!event.isControlDown()){      
//            if (!(   (this.selectionModel.getSelectionCount() == 1) 
//                  && (this.selectionModel.getSelectionRows()[0] == row))){
//              this.selectionModel.clearSelection(); 
//              this.addSelectionRow(row);  
//            }
//         }
         UTreeUnivEvent doe = new UTreeUnivEvent(this, this.getSelectionPaths());
         fireOnDoubleClick(doe);  
       }    
     }  
    /**
    * Обработчик события выделения узла
    * @param evt Событие выделения узла
    */
    private void TreeSelectionEvent(TreeSelectionEvent evt){
      refreshObjectOnSelection();
    }      
    
    /**
     * Принудительное обновление зависимых объектов при выделении
     */    
    @Override
    public void refreshObjectOnSelection(){
      if (   (this.getSelectionRows() != null)
          && (this.getSelectionCount() != 0)){
          
// обновление зависимых объектов
         if (this.objectsRefreshOnSelection  != null){
           for (Object obj: this.objectsRefreshOnSelection){ 
              refreshDependetObjects(obj);   
           }              
         } 
        if (this.objectsRefreshOnSelectionUsingAllSelectionData != null){
           for (Object obj: this.objectsRefreshOnSelectionUsingAllSelectionData){ 
              refreshDependetObjectsUsingAllSelectionData(obj);   
           }   
        }          
         
//  создание события выделения для потомков данного класса     
        UTreeUnivEvent doe = new UTreeUnivEvent(this, this.getSelectionPaths());
        fireOnSelect(doe);     
      } 
    }      
    
    /**
     * Принудительное обновление зависимых объектов по двойному клику
     */
    @Override
   public void refreshObjectOnDoubleClick() {
// обновление зависимых объектов
       if (this.objectsRefreshOnDoubleClick != null)
           for (int i = 0; i < this.objectsRefreshOnDoubleClick.length; i++) 
              refreshDependetObjects(this.objectsRefreshOnDoubleClick[i]); 
   }     
    
    /**
     * Процедура принудительного обновления объектов и интерфейсом InterfaceRefresh
     * @param obj зависимый компонент
     */  
     @Override
    public void refreshDependetObjects(Object obj){
      if (obj != null) {   
        
        if (obj.getClass() == JTextField.class)        
           ((JTextField) obj).setText(this.getTreeStringWithDelimiter("; "));
        if (obj.getClass() == JTextArea.class)        
           ((JTextArea) obj).setText(this.getTreeStringWithDelimiter("\r\n"));
        
        if (  (this instanceof InterfaceRefresh)
            &&( obj instanceof InterfaceRefresh)){
          if (this.getValue() == null){
            UFieldsValues tmpVF = new UFieldsValues(fields);   
            tmpVF.refreshDependetObject(obj);  
          }
          else {    
            if ((this.getValue() != null) && (this.getValue().length > 0))   
              this.getValue()[0].refreshDependetObject(obj); 
          }  
        }         
      }         
    }

    private List<UTreeDoubleClickListener> listenersUTreeDoubleClick = new ArrayList<>();
    public void addOnDoubleClickListener(UTreeDoubleClickListener al){
        listenersUTreeDoubleClick.add(al);
    }
    public void removeOnDoubleClickListener(UTreeDoubleClickListener al){
        listenersUTreeDoubleClick.remove(al);
    }
    private void fireOnDoubleClick(UTreeUnivEvent doe){
        for (UTreeDoubleClickListener listener : listenersUTreeDoubleClick) {
            listener.actionPerformed(doe);
        }
    }     

    private List<UTreeSelectListener> listenersUTreeSelect = new ArrayList<>();
    public void addOnSelectListener(UTreeSelectListener al){
        listenersUTreeSelect.add(al);
    }
    public void removeOnSelectListener(UTreeSelectListener al){
        listenersUTreeSelect.remove(al);
    }
    private void fireOnSelect(UTreeUnivEvent doe){
        for (UTreeSelectListener listener : listenersUTreeSelect) {
            listener.actionPerformed(doe);
        }
    }

    public String getSchema() {
        return schema;
    }

    @Override
    public void setSchema(String schema) {
        this.schema = schema;
    }

    public Object[] getDefRefreshData() {
        return defRefreshData;
    }

    @Override
    public void setDefRefreshData(Object[] defRefreshData) {
        this.defRefreshData = defRefreshData;
    }

    public void setTreePopupMenu(JPopupMenu treePopupMenu) {
        this.treePopupMenu = treePopupMenu;
    }
    
      public void setStrInnerFilter(String strInnerFilter) {
        this.strInnerFilter = strInnerFilter;
    }
    
      public String getStrInnerFilter() {
        return this.strInnerFilter;
        
    }

    protected String coverSqlTextByStatusFilter(String sqlText){
       String result = sqlText; 
       int index = this.fields.getIndexByColumnName("STATUS"); 
       
       if ((index >= 0) && (this.strInnerFilter != null) && (!this.strInnerFilter.equals(""))) {
         result = "SELECT * FROM (" + sqlText + ") A WHERE " + this.strInnerFilter;  
       }
       return result; 
    }  
     
    public String getSQLTextRoot(){
        return sqlTextRoot;
    }
    
     public String getSQLTextNodes() {
         return sqlTextNodes;
     }
     
     public String getSQLTextBrunch() {
         return sqlTextBrunch;
     }   
    

    public boolean isExpandAll() {
        return expandAll;
    }

    @Override
    public void setExpandAll(boolean expandAll) {
        this.expandAll = expandAll;
    }
    
  
    private List<UTreeRefreshListner> listenerTreeRefreshListner =new ArrayList<>();
    public void addOnTreeRefreshListner(UTreeRefreshListner al){
        listenerTreeRefreshListner.add(al);
    }
    public void removeOnTreeRefreshListner(UTreeRefreshListner al){
        listenerTreeRefreshListner.remove(al);
    }    
    public void fireOnTreeRefreshListner(UTreeRefreshEvent dde){
        for (UTreeRefreshListner listener : listenerTreeRefreshListner) {
            listener.actionPerformed(dde);
        }
    }    
    
    @Override
    public void setRootSelect(boolean rootSelect){
        this.rootSelect = rootSelect;
    }

    @Override
    public int getFrm_id() {
        return frm_id;
    }

    @Override
    public void setFrm_id(int frm_id) {
        this.frm_id = frm_id;
    }    

    @Override
    public JComponent[] getFocusedComponents(){
      return new JComponent[]{this};  
    }      

    @Override
    public int getFcom_id() {
        return fcom_id;
    }

    @Override
    public int getFct_id() {
        return fct_id;
    }    
    
    @Override
    public void setId(int fct_id, int fcom_id) {
        this.fcom_id = fcom_id;
        this.fct_id = fct_id;
    }    
    
    @Override
    public Object[] getObjectsRefreshOnSelectionUsingAllSelectionData() {
        return objectsRefreshOnSelectionUsingAllSelectionData;
    }

    @Override
    public void setObjectsRefreshOnSelectionUsingAllSelectionData(Object[] objectsRefreshOnSelectionUsingAllSelectionData) {
        this.objectsRefreshOnSelectionUsingAllSelectionData = objectsRefreshOnSelectionUsingAllSelectionData;
    }
    
    @Override
    public void refreshUsingAllSelectionData(UFieldsValues[] fvs)  {
    }    
    
    public void refreshObjectsOnSelectionUsingAllSelectionData() {
        if (this.objectsRefreshOnSelectionUsingAllSelectionData != null) {
            for (Object obj : this.objectsRefreshOnSelectionUsingAllSelectionData) {
                refreshDependetObjectsUsingAllSelectionData(obj);
            }
        }
    }      
    
    @Override
    public void refreshDependetObjectsUsingAllSelectionData(Object obj) {
        if ((obj != null) && (this instanceof InterfaceRefresh) && (obj instanceof InterfaceRefresh)) {
          UFieldsValues[] fvs = null;  
          if (this.getValue() == null) {
            fvs = new UFieldsValues[]{new UFieldsValues(this.fields)};
          } else {
             if ((this.getValue() != null) && (this.getValue().length > 0)) {
               fvs = this.getValue();
             }
          }
          ((InterfaceRefresh)obj).refreshUsingAllSelectionData(fvs);
        }
    }   
    
 } 
