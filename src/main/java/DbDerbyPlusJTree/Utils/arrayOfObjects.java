package DbDerbyPlusJTree.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gsedelnikov
 */
public class arrayOfObjects {
    
   public static Object[] split(Object[] objsMain, Object[] objsAdds){
     List<Object> result = new ArrayList<>();
     
     if ((objsMain == null) || ((objsMain.length %2 != 0))){
       if ((objsAdds != null) && ((objsAdds.length %2 == 0)))  
         result.addAll(Arrays.asList(objsAdds));
     }  
     else {
       result.addAll(Arrays.asList(objsMain));
       if ((objsAdds != null) && ((objsAdds.length %2 == 0))) {

         for (int i=0; i < objsAdds.length / 2; i++){
           boolean notFound = true;  
           int j = 0;
           while ((notFound) && (j < objsMain.length / 2)){
             if (((String)objsAdds[2*i]).equals((String)objsMain[2*j])) 
               notFound = false;
             else
               j++;  
           }
        
           if (notFound) {
             result.add(objsAdds[2*i]); 
             result.add(objsAdds[2*i + 1]); 
           }
         }             
       } 
     }
      return result.toArray(); 
   }  
    
   public static UFieldsValues split(UFieldsValues objsMain, UFieldsValues objsAdds){
      UFieldsValues result = null;

     if ((objsMain == null) || (objsAdds == null) ){
       if (objsMain == null)
         result = objsAdds;
       else 
         result = objsMain;
     }  
     else {
       result = new UFieldsValues(objsMain.getTreeFields());  
       
       for (int i = 0; i < objsMain.getLength(); i++){
         String columnName = objsMain.getTreeFields().getColumnName(i);  
         if (objsMain.getValue(i) != null) {
//            if (objsMain.getValue(i) != null)
              result.SetValue(i, objsMain.getValue(i)); 
         }   
         else {   
           if (objsAdds.getValue(i) != null)
              result.SetValue(i, objsAdds.getValue(columnName)); 
         } 
       }   
     }
     
      return result; 
     
     
//     if ((objsMain == null) || ((objsMain.getLength() != 0))){
//       if ((objsAdds != null) && ((objsAdds.getLength() == 0)))  
//         result.addAll(Arrays.asList(objsAdds));
//     }  
//     else {
//       result.addAll(Arrays.asList(objsMain));
//       if ((objsAdds != null)) {
//
//         for (int i=0; i < objsAdds.getLength(); i++){
//           boolean notFound = true;  
//           int j = 0;
//           while ((notFound) && (j < objsMain.getLength())){
//             if ((objsAdds.getTreeFields().getColumnName(i)).equals(objsMain.getTreeFields().getColumnName(j))) 
//               notFound = false;
//             else
//               j++;  
//           }
//        
//           if (notFound) {
//             result.add(objsAdds[2*i]); 
//             result.add(objsAdds[2*i + 1]); 
//           }
//         }             
//       } 
//     }
   }  
    
    public static Object[] getРaramsFromSql(String sqlText, Object[] inData){
      ArrayList qq = new ArrayList();  
      qq.clear();

      for (int i = 0; i < inData.length / 2; i++) 
        if ((sqlText.toUpperCase().contains(":" + ((String)inData[i * 2]).toUpperCase()))){
          qq.add(inData[i * 2]);
          qq.add(inData[i * 2 + 1]);
        }
                
      return qq.toArray();
    }   
   
    
    public static Object[] getРaramsFromSql(String sqlText, UFieldsValues mto){
      ArrayList qq = new ArrayList();  
      qq.clear();
      UFields fields = mto.getTreeFields();
      for (int i = 0; i < fields.getLength(); i++) 
        if ((sqlText.toUpperCase().contains(":" + fields.getColumnName(i).toUpperCase()))){
          qq.add(fields.getColumnName(i));
          qq.add(mto.getValue(i));
        }
                
      return qq.toArray();
    }    
    
    public static String getDelimitedString(Object[] arr, String delimiter1, String delimiter2){
       String result = "";
       String tmpDelimiter1 = (delimiter1 == null)?"":delimiter1;
       String tmpDelimiter2 = (delimiter2 == null)?"":delimiter2;
       if (arr != null) 
         for (int i=0; i < arr.length; i++)
           if (i == 0)  
             result += (arr[i] == null)? "null" : arr[i].toString();
           else {  
             if ((i%2)==0)  
               result += tmpDelimiter2 + ((arr[i] == null)? "null" : arr[i].toString());
             else
               result += tmpDelimiter1 + ((arr[i] == null)? "null" : arr[i].toString());
           }  
       return result; 
    }    
    
    public static String getDelimitedString(Object[] arr, String delimiter){
       String result = "";
       String tmpDelimiter = (delimiter == null)?"":delimiter;
       if (arr != null) 
         for (int i=0; i < arr.length; i++)
           if (i == 0)  
             result += arr[i];
           else           
             result += tmpDelimiter + arr[i].toString();
       return result; 
    }
    
    public static String[] getArrayFromString(String inStr, String delimiter){
        
       List <String> result = new ArrayList<>();
       if ((inStr != null) && (delimiter != null))
         while (inStr.indexOf(delimiter) != -1){
            int pos = inStr.indexOf(delimiter);
            String toAdd = inStr.substring(0, pos).trim();
            if (!toAdd.equals(""))
              result.add(toAdd.trim());
            inStr =  inStr.substring(pos+1);
         }
       
       if ((inStr != null) && (!inStr.equals("")))
           result.add(inStr.trim());       
       
       if (result.isEmpty())
         return null;
       else
         return result.toArray(new String[0]);
    }
   
    
  /*получение данных из массива типа Object[]*/
   public static Object getValueFromObject(Object[] inData, String colName) {
    Object Result_value = null;
    if (inData != null)
      for (int i = 0; i<=inData.length-1; i++){
          if ((i%2 == 0)&&(inData[i].toString().equals(colName)))
          {
             Result_value = inData[i+1];
             break;
          }
      }
    return Result_value;
  }   
   
   public static boolean checkValueFromObject(Object[] inData, String colName) {
     boolean result = false;
     if (inData != null)
      for (int i = 0; i<=inData.length-1; i++){
          if ((i%2 == 0)&&(inData[i].toString().equals(colName))){
             result = true;
             break;
          }
      }
    return result;
  }    
   
    /*присвоение данных из массива типа Object[]*/
   public static Object[] setValueToObject(Object[] inData, String colName, Object data) {
        Object[] res_value = null;
        if (inData != null) {
            res_value = new Object[inData.length]; //inData;
            for (int i = 0; i <= inData.length - 1; i++) {
                if ((i % 2 == 0) && (inData[i].toString().equals(colName))) {
                    res_value[i] = colName;
                    res_value[i + 1] = data;
                    i++;                    
                } else {
                    res_value[i] = inData[i];
                }
            }
        }
        return res_value;
    }
    
   public static Object[] delValueToObject(Object[] inData, String colName) {
        List <Object> result = new ArrayList<>();
        if (inData != null) {
            for (int i = 0; i < inData.length / 2; i++) {
                if (!inData[2*i].toString().equals(colName)) {
                    result.add(inData[2*i]);
                    result.add(inData[2*i+1]);
                }
            }
        }
        return result.toArray();
    }   
   
   
   public static Object[] getArrayFromUFieldsValues(UFieldsValues[] inValues, String field){
       List <Object> result = new ArrayList<>();
       if ((inValues != null) && (field != null))
            for (UFieldsValues value : inValues) {
                if (value.getValue(field) != null){
                    result.add(value.getValue(field));
                }
            }
       if (result.isEmpty())
         return null;
       else
         return result.toArray(new Object[0]);
   } 
  
//  public static Object[] getArrayFromUFieldsValues(UFieldsValues[] inValues, String field, String filterField, Object filterValue){
//       List <Object> result = new ArrayList<>();
//       if ((inValues != null) && (field != null))
//            for (UFieldsValues value : inValues) {
//                if (value.getValue(field) != null){
//                    result.add(value.getValue(field));
//                }
//            }
//       if (result.isEmpty())
//         return null;
//       else
//         return result.toArray(new Object[0]);
//   }   
  
   public static boolean existsInArray(Object[] array, Object value){
      boolean notfound = true;
      int i = 0;
      while ((notfound) && (i < array.length)){
         notfound = !array[i].equals(value); 
         i++; 
      } 
       
      return !notfound;
   }
   
   /**
    * 
    * @param array массив типа имя поля-значение
    * @param field имя искомого поля
    * @return true если в массиве array есть поле field cо значением, отличным от null
    */
   public static boolean existsFieldInArray(Object[] array, String field){
       if(array==null){
           return false;
       }
       for (int i = 0; i < array.length; i=i+2) {
           if(field.equalsIgnoreCase((String)array[i])&&
                   (!UFieldsValues.nullId.equals(array[i+1]))){
               return true;
           }
       }
       return false;
   }

    /**
     * добавляет данные из дополниткльного массива в в исходный, переписывая совпадающие поля при необходимости
     * @param source исходный массив
     * @param addend дополнительный массив
     * @return 
     */
    public static Object[] merge(Object[] source, Object[] addend) {
        if (source == null) {
            return addend;
        }
        if (addend == null) {
            return source;
        }
        ArrayList result = new ArrayList();
        for (int i = 0; i < source.length / 2; i++) {
            if (!existsFieldInArray(addend, (String) source[2 * i])) {
                result.add(source[2 * i]);
                result.add(source[2 * i + 1]);
            }
        }
        for (int i = 0; i < addend.length / 2; i++) {
            if (!UFieldsValues.nullId.equals(addend[2 * i + 1])) {
                result.add(addend[2 * i]);
                result.add(addend[2 * i + 1]);
            }
        }
        return result.toArray();
    }

   public static final int regimeInStringReplace      = 1;
   public static final int regimeInStringReplaceOrAdd = 2;
   public static String replaceOrAddInString(String str, String[] arrData, int regime, String deliminator){
      String result = str;
      
      if ((arrData != null) && (arrData.length > 0)){
        if ((arrData.length % 2 != 0)) //Заглушка: Добавить норм. обработку{
            throw new IllegalArgumentException("Нечётное количество параметров");   //TODO LANG TODO EXEP      
        
         for (int i = 0; i < arrData.length / 2; i++) {
           switch (regime){
              case regimeInStringReplace:{
                 result = result.replaceAll(arrData[2*i], arrData[2*i+1]); 
                 break; 
              } 
              case regimeInStringReplaceOrAdd:{
                 if (result.contains(arrData[2*i])) 
                   result = result.replaceAll(arrData[2*i], arrData[2*i+1]); 
                 else
                   result = UTypes.combine("", arrData[2*i+1], result, deliminator); 
                 break; 
              }                   
           }  
         }       
      }           
      return result;  
   }    
    
    public static boolean compareWithNull(Object ob1, Object ob2) {
        if ((ob1 == null) && (ob2 == null)) {
            return true;
        }
        if ((ob1 == null) || (ob2 == null)) {
            return false;
        }
        return ob1.toString().equals(ob2.toString());
    }   
   
    public static int[] toIntArray(Integer[] arrInt)  {
      int[] result = null;  
      if (arrInt != null){  
        result = new int[arrInt.length];
        for (int i=0; i < arrInt.length; i++)  
            result[i] = arrInt[i].intValue();
      }  
        return result;
    }
 
    public static int[] toIntArray(List<Integer> list)  {
      int[] result = null;  
      if (list != null){  
        result = new int[list.size()];
        for (int i=0; i < list.size(); i++)  
            result[i] = list.get(i).intValue();
      }  
        return result;
    }    
    
    public static Object[] addToArray(Object[] array, Object addend) {
        array = array == null ? new Object[0] : array;
        Object[] new_array = new Object[array.length + 1];
        System.arraycopy(array, 0, new_array, 0, array.length);
        new_array[new_array.length - 1] = addend;
        return new_array;
    }
    
    public static Object[] addToBeginArray(Object[] array, Object addend) {
        array = array == null ? new Object[0] : array;
        Object[] new_array = new Object[array.length + 1];
        new_array[0] = addend;
        System.arraycopy(array, 0, new_array, 1, array.length);
        return new_array;
    }
    
    public static int getIntValueFromObject(Object[] inData, String colName) {
      Object obj = getValueFromObject(inData, colName);
      int result = UTypes.getIntFromObject(obj);
      return result;
    }    
    
    public static Integer getIntegerValueFromObject(Object[] inData, String colName) {
      Object obj = getValueFromObject(inData, colName);
      Integer result = UTypes.getIntegerFromObject(obj);
      return result;
    }      
    
}
