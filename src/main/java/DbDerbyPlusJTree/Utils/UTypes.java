package DbDerbyPlusJTree.Utils;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author gsedelnikov
 */
public class UTypes {

  
    
    
    public static String getStringFromObject(Object object){
       String result = null;    
       if (object != null){ 
         if  (object instanceof String){ 
           result = (String)object;   
         }  
         else
            if (object instanceof BigDecimal) {
                result = String.valueOf(((BigDecimal) object).intValue());
            }        
       }
       
       return result;
    }

    public static boolean getBooleanFromObject(Object object, boolean defVal){
       boolean result = defVal;   
       Integer tmp = getIntegerFromObject(object);
       if ((tmp != null))
         result = (tmp.intValue() == 1);  
       
       return result;
    }

    public static Integer getIntegerFromArrOfObject(Object[] object){
       Integer result = -1; 
       if ((object != null) && (object.length > 0)){
          result = UTypes.getIntegerFromObject(object[0]); 
       }
       return result;
    }    
    
    public static Long getLongFromArrOfObject(Object[] object){
       Long result = null; 
       if ((object != null) && (object.length > 0)){
          result = UTypes.getLongFromObject(object[0]); 
       }
       return result;
    } 
    
    public static int getIntFromArrOfObject(Object[] object){
        int result = -1;
        Integer resInt = getIntegerFromArrOfObject(object); 
        result = (resInt == null)?-1:resInt.intValue();
        if (object[0] instanceof Long) {
            Long resLong = getLongFromArrOfObject(object); 
            result = (resLong == null)?-1:resLong.intValue();
        }
        return result;
    }
    
    public static int getIntFromObject(Object object){
        int result = -1;
        Integer resInt = getIntegerFromObject(object); 
        result = (resInt == null)?-1:resInt.intValue();
        if (object instanceof Long) {
            Long resLong = getLongFromObject(object); 
            result = (resLong == null)?-1:resLong.intValue();
        }
        return result;
    }
    
    public static Integer getIntegerFromObject(Object object){
       Integer result = null;   
       if (object != null)       
            if (object instanceof BigDecimal) {
                result = Integer.valueOf(((BigDecimal) object).intValue());
            } 
            else {
               if (object instanceof Integer) {
                result = (Integer) object;
               }
               else 
                if (object instanceof String)  {  
                    try {
                        result = Integer.valueOf(object.toString());
                    } catch (NumberFormatException numberFormatException) {
                       result = null; 
                    }
                }
                else 
                  if (object instanceof Double) {
                    result = ((Double)object).intValue();      
                  }
               else
                 if (object instanceof Long) {
                    result = ((Long)object).intValue(); 
                  }
  
            } 
       return result;
    }   
    
    public static Long getLongFromObject(Object object){
       Long result = null;   
       if (object != null)       
            if (object instanceof BigDecimal) {
                result = Long.valueOf(((BigDecimal) object).intValue());
            } 
            else {
               if (object instanceof Long) {
                result = (Long) object;
               }
               else 
                if (object instanceof String)  {  
                    try {
                        result = Long.valueOf(object.toString());
                    } catch (NumberFormatException numberFormatException) {
                       result = null; 
                    }
                }
                else 
                  if (object instanceof Double) {
                    result = ((Double)object).longValue();      
                  }
  
            } 
       return result;
    }  
    
    public static Date getDateFromObject(Object object){
       Date result = null;   
       if (object != null)       
            if (object instanceof Date) {
                result = (Date) object;
            } 
 
       return result;
    }  
    
   public static String combine(String strStart, String str1, String str2, String deliminator){
      String result = ""; 
      
      if (!(str1 == null) && (!"".equals(str1))) 
         result += str1; 
 
      if (!(str2 == null) && (!"".equals(str2)))
        if ("".equals(result))  
          result = str2; 
        else
          result +=  deliminator + str2; 
      
      if (!"".equals(result)) 
        result = strStart + result;
      
       return result;       
   }    
    
   public static boolean objEqualsPlus(Object obj1, Object obj2){
      if ((obj1 == null) && (obj2 == null)) 
        return true;
      else 
        if ((obj1 == null) || (obj2 == null))  
          return false;
        else 
          return  obj1.equals(obj2); 
   }
      
   public static boolean isStringIsNotEmpty(String text){
     return !((text == null) || (text.equals("")));   
   }

}
