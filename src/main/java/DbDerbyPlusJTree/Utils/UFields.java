package DbDerbyPlusJTree.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Класс для хранения информация о типе поля и его роли (ID, Text, Parent и т.д.)
 * @author gsedelnikov
 */
  public class UFields extends Object implements Serializable{ 
//  константы типов полей    
    public static final int t_Integer   = 0; // Тип поля - число (Integer)
    public static final int t_String    = 1; // Тип поля - Строка
    public static final int t_Date      = 2; // Тип поля - Дата
    public static final int t_Object    = 3; // Тип поля - Все что угодно

//  константы ролей полей    
    public static final int r_Id          = 0;   // (!Обязательно для запроса) Поле является идентификатором (необходим при построении иерархии)
    public static final int r_Text        = 1;   // (!Обязательно для запроса) Поле является текстом для отображения в дереве
    public static final int r_Parent      = 2;   // (!Обязательно для запроса) Поле содержит информацию о наличии потомков
    public static final int r_Integer     = 3;   // Произвольное числовое поле 
    public static final int r_String      = 4;   // Произвольное строковое поле 
    public static final int r_Icon        = 5;   // Поле содержит информацию о иконке у узла
    public static final int r_IntToEdid   = 6;   // Поле содержит информацию 
    public static final int r_Date        = 7;   // Произвольное поле типа дата
    public static final int r_RowEditable = 8;   // Индикатор возможности редактирования строки (Для таблицы только)
    public static final int r_EditValue   = 9;   // Отредактированное значение поля r_Text
    public static final int r_IntToSave   = 10;   // Произвольное числовое поле сохраняемое в БД
    public static final int r_Object      = 11;   // Произвольный объект

    
    private UFieldType objectFields[];      

    /**
     * Конструктор объекта для хранения информация о типе поля и его роли
     * @param in_fields Массив ролей полей (r_Id, r_Text, r_Parent, r_Integer, r_String, r_Icon)
     */    
    public UFields(Object[] in_fields){
      if (in_fields != null) {  
        
        if (in_fields.length %2!=0){ //Заглушка: Добавить норм. обработку
            System.out.println("UFields: Нечётное количество параметров");
            throw new IllegalArgumentException("Нечётное количество параметров");  
        }    

        this.objectFields   = new UFieldType[in_fields.length / 2];  
        
        for (int i = 0; i < in_fields.length / 2; i++)
            this.objectFields[i] = new UFieldType(i, (String)in_fields[2*i], (Integer)in_fields[2*i+1]); 
      }   
    }
    
    public void add(String columnName, Integer role){
       UFieldType uft = new UFieldType(objectFields.length, columnName, role);
        
       List <UFieldType> qq = new ArrayList<>();
       qq.addAll(Arrays.asList(objectFields));
       qq.add(uft);
       
       objectFields = qq.toArray(new UFieldType[0]); 
    }
    
    
    public List<Object> getFieldsArray(){
       List<Object> result = new ArrayList<>(); 
       if (objectFields != null)
         for (UFieldType fv: objectFields){
           result.add(fv.columnName);
           result.add(fv.role);
         }
//      return result.toArray();      
      return result;      
    }     
    
    /**
     * Метод получения количества полей в запросе
     * @return Количество полей в запросе
     */
    public int getLength(){
      if (this.objectFields != null)  
           return this.objectFields.length;
      else return 0;      
    }  
    /**
     * Метод получения подтверждения, что поле является идентификатором
     * @param index Номер поля
     * @return Если поле является идентификатором - true
     */
    public boolean isId(int index) {
      if (this.objectFields != null)  
           return this.objectFields[index].isId;
      else return false;
    }
    
    public boolean isIntToEdit(int index) {
      if (this.objectFields != null)  
           return (this.objectFields[index].role == r_IntToEdid);
      else return false;
    }    
    
    /**
     * Метод получения подтверждения, что поле является текстом узла
     * @param index Номер поля
     * @return Если поле является текстом узла - true
     */
    public boolean isText(int index) {
      if (this.objectFields != null)  
           return this.objectFields[index].isText;
      else return false;      
    }
    
    /**
     * Метод получения подтверждения, что поле содержит дату
     * @param index Номер поля
     * @return Если поле содержит дату - true
     */
    public boolean isDate(int index) {
      if (this.objectFields != null)  
           return this.objectFields[index].isDate;
      else return false;      
    }
    
    /**
     * Метод получения подтверждения, что поле содержит информацию о наличии потомков
     * @param index Номер поля
     * @return Если поле содержит информацию о наличии потомков - true
     */
    public boolean isParent(int index) {
      if (this.objectFields != null)  
           return this.objectFields[index].isParent;
      else return false;
    }
    /**
     * Метод получения подтверждения, что поле содержит информацию о номере иконки у узла
     * @param index Номер поля
     * @return Если поле содержит информацию о номере иконки у узла - true
     */
    public boolean isIcon(int index) {
      if (this.objectFields != null)  
           return this.objectFields[index].isIcon;
      else return false;
    }
     /**
     * Метод получения типа поля 
     * @param index Номер поля
     * @return числовое - t_Integer; строковое - t_String
     */
     public int getValtype(int index) {
      if (this.objectFields != null)  
           return this.objectFields[index].valtype;
      else return -1;
     } 
     
     /**
      * Метод получения Наименования столбца в запросе
      * @param index Номер поля
      * @return Наименование столбца в запросе
      */
     public String getColumnName(int index) {
      if (this.objectFields != null)  
           return this.objectFields[index].columnName;
      else return null;
     }  
     
    /**
      * Метод получения номера столбца по наименованию
      * @param columnName
      * @return 
      */
     public int getIndexByColumnName(String columnName) {
      int result = -1;
      int i = 0;
      
      if (this.objectFields != null) { 
         while ((i < this.objectFields.length) && (result == -1)) {
//           if (this.objectFields[i].columnName.toUpperCase().equals(columnName.toUpperCase()))
           if (this.objectFields[i].columnName.equals(columnName))
               result = i;
         i++;  
        }
      }     
      
      return result;
     }

    public boolean isInteger(int index) {
        if (this.objectFields != null)  
           return this.objectFields[index].valtype == t_Integer;
      else return false;
    }
     
    public boolean isIntegerToSave(int index) {
        if (this.objectFields != null)  
           return this.objectFields[index].role == r_IntToSave;
      else return false;
    }    
    
   public String getRowEditableFieldCode(){
     String result = null; 
     int i = 0;
     boolean notFound = true;
     if (objectFields != null){
       while (notFound && (i < objectFields.length)){
         if (objectFields[i].isRowEditable) { 
          result = objectFields[i].columnName;  
          notFound = false;
         }  
         i++;
       }  
     }   
     return result;
   }       
    
    public boolean isTextEditValue(int index) {
      if (this.objectFields != null)  
        return this.objectFields[index].isTextEditValue;
      else 
        return false;
    }   
   
    
    
    /**
     * Процедура определения типов полей и их ролей
     */     
    private class UFieldType extends Object implements Serializable{
        public String  columnName = "ID";
        public int     valtype  = t_Integer;
        public int     role     = r_String;
        public boolean isId     = false;       
        public boolean isParent = false;     
        public boolean isText   = false;  
        public boolean isIcon   = false;  
        public boolean isDate   = false;  
        public boolean isRowEditable = false;  
        public boolean isTextEditValue = false;  
        
        
      
        public UFieldType(Integer index, String columnName, Integer role){
          if ((columnName != null) && (!columnName.equals("")))  
            this.columnName = columnName;  
          else
            this.columnName = this.columnName + index;  

          this.role = role;  
  	  switch (role) {
            case r_Id: { 
              this.valtype  = t_String;
              this.isId     = true;       
              this.isParent = false;            
              this.isText   = false;              
              this.isDate   = false; 
              break;} 
            case r_Text: { 
              this.valtype  = t_String;
              this.isId     = false;       
              this.isParent = false;            
              this.isText   = true;              
              this.isDate   = false;              
              break;} 
            case r_Parent: { 
              this.valtype  = t_Integer;
              this.isId     = false;       
              this.isParent = true;            
              this.isText   = false;               
              this.isDate   = false;             
              break;}
            case r_Integer: { 
              this.valtype  = t_Integer;
              this.isId     = false;       
              this.isParent = false;            
              this.isText   = false;              
              this.isDate   = false;              
              break;} 
            case r_String: { 
              this.valtype  = t_String;
              this.isId     = false;       
              this.isParent = false;            
              this.isText   = false;              
              this.isDate   = false;              
              break;}              
            case r_Icon: { 
              this.valtype  = t_String;
              this.isId     = false;       
              this.isParent = false;            
              this.isText   = false; 
              this.isIcon   = true;               
              this.isDate   = false;
              break;}   
            case r_IntToEdid: { 
              this.valtype  = t_Integer;
              this.isId     = false;       
              this.isParent = false;            
              this.isText   = false; 
              this.isIcon   = false;               
              this.isDate   = false;
              break;
            }                  
            case r_Date: { 
              this.valtype  = t_Date;
              this.isId     = false;       
              this.isParent = false;            
              this.isText   = false;              
              this.isDate   = true;              
              break;
            }         
            case r_RowEditable: { 
              this.valtype  = t_Integer;
              this.isId     = false;       
              this.isParent = false;            
              this.isText   = false;              
              this.isDate   = false;              
              this.isRowEditable = true;              
              break;
            }            
            case r_EditValue: { 
              this.valtype  = t_Object;
              this.isTextEditValue = true;              
              break;
            }     
            case r_IntToSave: { 
              this.valtype  = t_Integer;
              this.isId     = false;       
              this.isParent = false;            
              this.isText   = false;              
              this.isDate   = false;              
              break;
            }             
          }
        }      
    }     
  }   

