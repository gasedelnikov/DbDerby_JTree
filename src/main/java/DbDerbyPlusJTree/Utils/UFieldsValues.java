package DbDerbyPlusJTree.Utils;

import DbDerbyPlusJTree.UTree.UExceptions.ExceptionSQLTextErr;
import DbDerbyPlusJTree.UTree.UInterfaces.InterfaceRefresh;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
//import java.util.Date;

/**
 * Класс объектов для хранения информации о узле дерева, включая информацию о типе поля запроса
 * @author gsedelnikov
 */
public class UFieldsValues extends Object implements Serializable {
// value        - значения полей запросов 
// fields       - информация о типе поля и его роли (ID, Text, Parent и т.д.)
// nodeHasChild - для удобства, информация имеет ли данный узел потомков    

    public static final boolean nodeHasChildDef = true;
    public static final Object nullId = -99;
    protected Object[] value;
    protected UFields fields;
    protected boolean nodeHasChild;

    /**
     * Конструктор без присвоения значений, на основе информация о типах полей
     * @param in_fields Информация о типах полей
     */
    public UFieldsValues(UFields in_fields) {
        this(in_fields, UFieldsValues.nodeHasChildDef);
    }
    
    public UFieldsValues(UFields in_fields, boolean nodeHasChildDef) {
        this.nodeHasChild = nodeHasChildDef;
        this.value = new Object[in_fields.getLength()];
        this.fields = in_fields;

        for (int i = 0; i < in_fields.getLength(); i++) {
            if (fields.isId(i)) {
                SetValue(i, nullId);
            }
        }       
    }    

    public UFieldsValues(Object defValue, UFields in_fields) {
        this.nodeHasChild = UFieldsValues.nodeHasChildDef;        
        this.value = new Object[in_fields.getLength()];
        this.fields = in_fields;

        for (int i = 0; i < in_fields.getLength(); i++) {
            if (fields.isId(i)) {
                SetValue(i, defValue);
            }
        }
    }    
    
    /**
     * Конструктор 
     */
    public UFieldsValues(UFields in_fields, Object[] inData) {
      if (in_fields != null){  
        if (inData.length % 2 != 0){ //Заглушка: Добавить норм. обработку
            throw new IllegalArgumentException("Нечётное количество параметров");   //TODO LANG TODO EXEP  
        }
        this.value = new Object[in_fields.getLength()];
        this.fields = in_fields;

        if (inData != null) {
            for (int i = 0; i < inData.length / 2; i++) {
                int tmpVal = this.fields.getIndexByColumnName((String) inData[i * 2]);
                if (tmpVal != -1) {
                    SetValue(tmpVal, inData[i * 2 + 1]);
                }
            }
        }
      }  
    }
    
    /**
     * Метод получения количества полей в запросе
     * @return Количество полей в запросе
     */
    public int getLength() {
        if (this.fields != null) {
            return this.fields.getLength();
        } else {
            return 0;
        }
    }

    /**
     * Метод определения значений полей для узла
     * @param index Номер поля (начиная с нулевого)
     * @param value Значение поля
     */
    public void SetValue(int index, Object value) {
        if (index < this.value.length) {
            if ((fields.isId(index)) && (value == null)) {
                this.value[index] = nullId;
            } else {
                this.value[index] = value;
            }

            if (fields.isParent(index)) {
              if ((this.value[index] != null) && (this.value[index] instanceof Integer))  
                this.nodeHasChild = (((Integer) this.value[index]).equals(1));
              else
                this.nodeHasChild = false;
            }
        }
    }

    /**
     * Метод определения значений полей для узла
     * @param field поле
     * @param value Значение поля
     */    
    public void SetValue(String field, Object value) {
       int index = getTreeFields().getIndexByColumnName(field);
       if (index > -1) 
         SetValue(index, value);  
    }    
    
    public void SetValue(UFieldsValues value) {
      if ((value != null) && (value.getTreeFields() != null) )
        for (int i = 0; i < value.getTreeFields().getLength(); i++){  
          String field = value.getTreeFields().getColumnName(i);  
          this.SetValue(field, value.getValue(field));
        }    
    }       
    
    
    /**
     * Метод определения текста для узла
     * @param nodeText Значение поля
     */
    public void SetText(String nodeText) {
        for (int i = 0; i < fields.getLength(); i++) {
            if (fields.isText(i)) {
                this.value[i] = nodeText;
            }
        }
    }

    /**
     * Метод получения информации о наличии у узла потомков
     * @return 
     */
    public boolean getNodeHasChild() {
        return this.nodeHasChild;
    }
    
    /**
     * Метод получения числовых значений узла
     * @param field Код поля
     * @return Числовое значение узла (если тип поля не числовой возвращает null)
     */
    public Integer getInteger(String field) {
        int index = this.fields.getIndexByColumnName(field);
        return getInteger(index);
    }

    public int getID(String field) {
        int index = this.fields.getIndexByColumnName(field);
        Integer result = getInteger(index);
        return (result == null)?-1:result.intValue();
    }    

    
    public Integer getIntegerWithIsNullId(String field) {
        int index = this.fields.getIndexByColumnName(field);
        return getIntegerWithIsNullId(index);
    }    
    
    public Integer getIntegerWithIsNullId(int index) {
        Integer result = null;

        if ((index != -1) && (index < this.value.length)) {        
          if (!isNullId(index)){
            result = getInteger(index);  
          }  
        }
        return result;
    }
    
    
    /**
     * Метод получения числовых значений узла
     * @param index Номер поля
     * @return Числовое значение узла (если тип поля не числовой возвращает null)
     */
    public Integer getInteger(int index) {
        Integer result = null;

        if ((index != -1)
                && (index < this.value.length)) {
//          && (this.fields.getValtype(index) == UFields.t_Integer)

            Object object = this.value[index];
            if (object instanceof BigDecimal) {
                result = Integer.valueOf(((BigDecimal) this.value[index]).intValue());
            } 
            else {
               if (object instanceof Integer) {
                result = (Integer) this.value[index];
               }
               else 
                if (object instanceof String)  {  
                  result = Integer.valueOf(object.toString());      
                }
                else 
                  if (object instanceof Double) {
                    result = ((Double)object).intValue();      
                  }
            }   
          }

        return result;
    }

    public Double getDouble(String field) {
        int index = this.fields.getIndexByColumnName(field);
        return getDouble(index);
    }    
    
    public Double getDouble(int index) {
        Double result = null;

        if ((index != -1) && (index < this.value.length)) {
            Object object = this.value[index];
            if (object instanceof Double) {
                result =((Double)this.value[index]);
            }
            else
              if (object instanceof BigDecimal) {
                  result =((BigDecimal)this.value[index]).doubleValue();
              } 
              else {
                 if (object instanceof Integer) {
                   result = ((Integer) this.value[index]).doubleValue();
                 }
                 else 
                   if (object instanceof String)    
                     result = Double.valueOf(object.toString());      
             }   
        }

        return result;
    }    
    
    
    /**
     * Метод получения свойст полей узла дерева
     * @return свойства полей узла дерева 
     */
    public UFields getTreeFields() {
        return this.fields;
    }

    /**
     * Метод получения значения  в формате строки
     * @param field Код поля
     * @return Значение узла в формате строки
     */    
    public String getString(String field) {
        int index = this.fields.getIndexByColumnName(field);
        return getString(index);
    }    
    
    /**
     * Метод получения значения узла в формате строки
     * @param index Номер поля
     * @return Значение узла в формате строки
     */
    public String getString(int index) {
        String result = null;
        if ((index != -1) && (index < this.value.length)) {
            Object object = this.value[index];
            if (object instanceof String) {
                result = (String) object;
            } else 
               if (object instanceof BigDecimal) {
                result = ((BigDecimal) this.value[index]).toString(); //String.valueOf(((BigDecimal)this.value[index]).intValue());
               }
               else 
                 if (object instanceof Integer) {
                   result = ((Integer) this.value[index]).toString();
                 }
                 else 
                   if (object instanceof java.util.Date) {
                     result = (this.value[index]).toString();
                   }
            
        }
        return result;
    }

    /**
     * Метод получения значения узла в неопределенном формате
     * @param index Номер поля
     * @return Значение узла в неопределенном формате
     */
    public Object getValue(int index) {
        if ((index != -1) && (index < this.value.length)) {
            if ((fields.isId(index)) && (isNullId(index))) {
                return null;
            } else {
                return this.value[index];
            }

        } else {
            return null;
        }
    }

    protected boolean isNullId(int index) {
        Object val = this.value[index];
        if(val==null){
            return true;
        }else{
            return val.toString().equalsIgnoreCase(nullId.toString());
        }
    }

//     * Метод получения значения узла в неопределенном формате
    public Object getValue(String field) {
        int index = this.fields.getIndexByColumnName(field);
        return getValue(index);
    }

   /**
    .**Метод проверки наличия и заполненности полей по шаблону
     * @param fieldsToCompare шаблон для сравнения
     * @return 
     */ 
   public boolean checkFullness(UFields fieldsToCompare){
     boolean result = true;      
     int i = 0;
     while ((result) && (i < fieldsToCompare.getLength())){
       if (fieldsToCompare.isId(i)){  
         int index = this.getTreeFields().getIndexByColumnName(fieldsToCompare.getColumnName(i));  
         
         if ((index == -1) || (!this.getTreeFields().isId(index)) || (this.getValue(index) == null))  
           result = false;  
       }  
       i++;
     }
     
     return result;
   } 
   
   public int checkFullness(String...fields){
     boolean fullness = true;      
     int i = 0;
     while ((fullness) && (i < fields.length)){
       int index = this.getTreeFields().getIndexByColumnName(fields[i]);  
       if (index != -1){
         Object obj = this.value[index];
         if (  (obj == null) 
             || (obj == UFieldsValues.nullId)
             || ((obj instanceof String) && (obj.toString().trim().equals("")))  
             )
           fullness = false;    
       } else {
           fullness = false;   
       }
       if (fullness)
         i++;
     }
     if (fullness)
       return -1;
     else         
       return i;
   }    
   
    /**
     * Метод сравнения значений двух объектов (на основе только ID)
     * @param mtoToCompare Объект для сравнения
     * @return Если значения ID одинаковые - True
     */
    public boolean compareTreeObjectsWithMask(UFields mask, UFieldsValues mtoToCompare) {
      if (mtoToCompare != null){  
        boolean eq = true;
        int i = 0;
        while (eq && (i < this.value.length)) {
            String columnName = this.fields.getColumnName(i);
            if ((this.fields.isId(i)) && (mask.getIndexByColumnName(columnName) != -1)) {
                int j = mtoToCompare.getTreeFields().getIndexByColumnName(columnName);
                
                if (!(this.getString(i) == null) ^ (mtoToCompare.getString(j) == null)) {
                    if (!this.getString(i).equals(mtoToCompare.getString(j))) {
                        eq = false;
                    }
                } 
                else {
                    eq = false;
                }
            }
            i++;
        }
        return eq;
      }
      else 
        return false;  
    }   

    public boolean compareTreeObjects(UFieldsValues mtoToCompare, boolean allValues) {
      if (mtoToCompare != null){  
        boolean eq = true;
        int i = 0;
        while (eq && (i < this.value.length)) {
            if ((allValues) || (this.fields.isId(i))) {
                String strToCompare = mtoToCompare.getString(i);
                String strThis      = this.getString(i);
                if (strToCompare == null) strToCompare = ""; 
                if (strThis      == null) strThis = ""; 
                if (!strToCompare.equals(strThis)) 
                   eq = false;
            }
            i++;
        }
        return eq;
      }
      else 
        return false;  
    }

    /**
     * Метод сравнения значений двух объектов (на основе только ID и одинаковых UFields)
     * @param mtoToCompare Объект для сравнения
     * @return Если значения ID одинаковые - True
     */    
    public boolean compareTreeObjects(UFieldsValues mtoToCompare) {
       return compareTreeObjects(mtoToCompare, false);
    }    
    
    @Override
    public String toString() {
        String returnString = "Null Object";

        if (this != null) {
            returnString = "";
            for (int i = 0; i < this.value.length; i++) {
                if (this.fields.isText(i)) {
                  if (this.value[i] != null)  
                    returnString +=  this.value[i];
                }
            }
        }

        return returnString;
    }
    
    public String toStringAll() {
        String returnString = "Null Object";

        if (this != null) {
            returnString = "";
            for (int i = 0; i < this.value.length; i++) {
              if (this.value[i] != null)  
                  returnString +=  this.fields.getColumnName(i) + "='"+ this.value[i] + "'; ";
            }
        }

        return returnString;
    }    
    
    public boolean emptyText(){
        String text = toString();
        return ((text == null) || (text.equals("null")) || (text.equals("")) || (text.equals("Null Object")));
    }

    public Object[] createRefreshArrayByMaskID(UFields maskFields, boolean idOnly) {
        ArrayList mask = new ArrayList();
        int thisLength = this.getLength();

        if ((thisLength != 0) && (maskFields.getLength() != 0)) {
            int j = -1;
            for (int i = 0; i < thisLength; i++) {
//                if ((this.getValue(i) != null)) {
                    String fieldName = this.fields.getColumnName(i);
                    j = maskFields.getIndexByColumnName(fieldName);
                    if (j != -1) {
                      if ((!idOnly) || ((idOnly) && (maskFields.isId(j)))) { 
                        mask.add(this.fields.getColumnName(i));
                        Object val = this.getValue(i);
                        if (val != null)
                          mask.add(val);
                        else   
                          mask.add(UFieldsValues.nullId);
                      }  
                    }
  //              }
            }
        }
        return mask.toArray();
    }    
    
    public Object[] createRefreshArrayByMask(UFields maskFields) {
       return createRefreshArrayByMaskID(maskFields, false);
    }

    public Object[] createRefreshArrayByMask() {
        ArrayList mask = new ArrayList();
        int thisLength = this.value.length;

        if ((thisLength != 0) && (this.fields.getLength() != 0)) {
            for (int i = 0; i < thisLength; i++) {
                mask.add(this.fields.getColumnName(i));
                mask.add(this.value[i]);
            }
        }

        return mask.toArray();
    }

    public void refreshDependetObject(Object target) {
        if (target instanceof InterfaceRefresh) {
            UFields objFields = ((InterfaceRefresh) target).getFields();
            Object[] fiedlsArray = this.createRefreshArrayByMask(objFields);

            try {
                ((InterfaceRefresh) target).refresh(fiedlsArray);  // обновлении компонентов
            } catch (ExceptionSQLTextErr e) {
                System.out.println("Ошибка обновления зависимого объекта: " + e.toString());
            }
        }
    }
    
    public Object[] toArray(){
      int fLength = fields.getLength();    
      ArrayList<Object> array = new ArrayList<>(); 
    
      for (int i = 0; i < fLength; i++){
        array.add(fields.getColumnName(i));  
        array.add(value[i]);  
      }
             
      return array.toArray();   
    }
    
    public Object[] toArrayID(){
      int fLength = fields.getLength();    
      ArrayList<Object> array = new ArrayList<>(); 
    
      for (int i = 0; i < fLength; i++){
        if (fields.isId(i)) {  
          array.add(fields.getColumnName(i));  
          array.add(value[i]);  
        }
      }
             
      return array.toArray();   
    }

    public boolean isNullValue(int index) {
        Object val = getValue(index);
        if(val==null){
            return true;
        }else{
            return "".equalsIgnoreCase(val.toString());
        }
        
    }

    public Object[] getРaramsFromSql(String sqlText, boolean putNull){
      ArrayList qq = new ArrayList();  
      for (int i = 0; i < this.fields.getLength(); i++) 
        if (  ((sqlText.toUpperCase() + " ").contains(":" + fields.getColumnName(i).toUpperCase()+" "))
            || (sqlText.toUpperCase().contains(":" + fields.getColumnName(i).toUpperCase()+")"))   
            || (sqlText.toUpperCase().contains(":" + fields.getColumnName(i).toUpperCase()+","))   
            ){            
          qq.add(fields.getColumnName(i));
          if (putNull)
            qq.add(this.getValue(i));
          else    
            qq.add(this.value[i]);
        }
                
      return qq.toArray();
    }      
    
    public Object[] getРaramsFromSql(String sqlText){
        return getРaramsFromSql(sqlText, true);
    }    

    public Object getTextEditValue() {
        Object result = this.toString();
        if (this != null) {
            for (int i = 0; i < this.value.length; i++) {
                if (this.fields.isTextEditValue(i)) {
                  if (this.value[i] != null)  
                    result = this.value[i];
                }
            }
        }
        return result;
    }
    
    public void setTextEditValue(Object value) {
        if (this != null) 
          for (int i = 0; i < this.value.length; i++) 
            if (this.fields.isTextEditValue(i)) 
               if (value == null) 
                 this.value[i] = this.toString();
               else 
                 this.value[i] = value;
    }    
    
//    public void setValuesFromSQL(String query, Object... params)  {
//        try (ResultSetGlob rs = Database.createResultSet(query, params)) {
//          rs.last();
//          if (rs.getRow() > 0){
//            rs.first();            
//            setValuesFromResultSet(rs);
//          } 
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//   }     
//    
//    public void setValuesFromResultSet(ResultSetGlob rs)  {
//      try {
//        for (int i = 0; i < this.fields.getLength(); i++)  
//           SetValue(i, rs.getObject(fields.getColumnName(i)));
//      } 
//      catch (Exception ex) {
//         ex.printStackTrace(); 
//      } 
//   }    
//
//    public static List<UFieldsValues> getUFieldsValuesListFromSQL(UFields fields, String query, Object...params)  {
//        List<UFieldsValues> fvs = new ArrayList<>();
//        try (ResultSetGlob rs = Database.createResultSet(query, params)) {
//          while (rs.next()){
//            UFieldsValues fv = new UFieldsValues(fields);
//            fv.setValuesFromResultSet(rs);
//            fvs.add(fv);
//          }  
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        
//        return fvs;
//   }     
//    
    public static Object getUniformFieldsValue(UFieldsValues[] fvs, String field, Object difValue)  {
        boolean eqvals = ((fvs != null) && (fvs.length > 0));
        Object result = eqvals ? fvs[0].getValue(field) : null;
        int i = 1;
        
        while ((eqvals) && (i < fvs.length)){
           Object tmpVal = fvs[i].getValue(field);
           eqvals = UTypes.objEqualsPlus(result, tmpVal);
           i++;
        }
        
        if (eqvals)       
          return result;
        else
          return difValue;
   }    

    public static String getValuesInString(UFieldsValues[] fvs, String field)  {
      String result = "";
      if ((fvs != null) && (fvs.length > 0)){
        List<String> list = new ArrayList<>();
        for (UFieldsValues fv:fvs){
          list.add(fv.getString(field));
        }
        result = arrayOfObjects.getDelimitedString(list.toArray(), ",");
      }
      return result;
    }    
    
    public void setUniformFieldsValues(UFieldsValues[] fvs, UFieldsValues difValues)  {
       for (int i=0; i< fields.getLength(); i++){
         String field = fields.getColumnName(i);  
         Object tmpValue =  UFieldsValues.getUniformFieldsValue(fvs, field, difValues.getValue(field));
         SetValue(field, tmpValue);  
       }
   }       
    
    public UFieldsValues getCopy(){
       return new UFieldsValues(fields, this.createRefreshArrayByMask());
    } 
    
    public void addFields(String field, int role, Object value){
      this.fields.add(field, role);
      Object[] newValue = new Object[this.value.length + 1];
      System.arraycopy(this.value, 0, newValue, 0, this.value.length);      
      this.value = newValue;
      this.SetValue(field, value);
    }
    
}
