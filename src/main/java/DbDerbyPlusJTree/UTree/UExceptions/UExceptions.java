
package DbDerbyPlusJTree.UTree.UExceptions;

/**
 * Класс универсальной обработки исключительных ситуаций
 * @author gsedelnikov
 */
public class UExceptions {
    public static final int exceptionModeJustOnScreen  = 0;  // Выдается сообщение на экран, исключительные ситуации не генерируются
    public static final int exceptionModeJustException = 1;  // Исключительные ситуации генерируются, сообщение на экран НЕ выдаются
    public static final int exceptionModeBoth          = 2;  // Выдается сообщение на экран, генерируются исключительные ситуации 
    public static final int exceptionModeDef           = exceptionModeBoth;  // Значение по умолчанию, определенное в проекте.
    
    private boolean genMsgOnScreen  = true;  
    private boolean genExcept       = true;      
    private String exceptionText    = "Empty string";

    /**
     * Конструктор универсальной генерации исключительных ситуаций с режимом обработки исключительных ситуаций по умолчанию для проекта
     * @param mco_code GRP_ID сообщения в таблице MSG
     * @param msg MSG_ID сообщения в таблице MSG
     * @param exceptionPlace Место возникновения ошибки
     */
    public UExceptions(String mco_code, String msg, String exceptionPlace){
       this(mco_code, msg, exceptionPlace, exceptionModeDef);    
    }    
    /**
     * Конструктор универсальной генерации исключительных ситуаций 
     * @param mco_code GRP_ID сообщения в таблице MSG
     * @param mmc_code MSG_ID сообщения в таблице MSG
     * @param exceptionPlace Место возникновения ошибки
     * @param autoException Режим обработки исключительных ситуаций (exceptionModeJustOnScreen, exceptionModeJustException, exceptionModeBoth, exceptionModeDef)
     */
    public UExceptions(String mco_code, String mmc_code, String exceptionPlace, int autoException){
      this.genMsgOnScreen  = (autoException != 1);  
      this.genExcept = (autoException > 0);  

      setExceptionText(mco_code, mmc_code, exceptionPlace);
      
      if (this.genMsgOnScreen)  ShownMsgOnScreen(this.exceptionText);
    }
    /**
     * Процедура генерации сообщения об ошибке
     * @param grp GRP_ID сообщения в таблице MSG
     * @param mmc_code MSG_ID сообщения в таблице MSG
     * @param exceptionPlace Место возникновения ошибки
     */
    private void setExceptionText(String mco_code, String mmc_code, String exceptionPlace){
        if ((exceptionPlace == null) || (exceptionPlace.equals("")))
             this.exceptionText = getMsgText(mco_code, mmc_code);
        else this.exceptionText = getMsgText(mco_code, mmc_code) + "\r\n"  + getMsgText("UExceptions","errPlace") +": " + exceptionPlace;
    }
    /**
     * Метод получения сообщения об ошибке
     * @return Сообщения об ошибке
     */
    public String getExceptionText(){
        return this.exceptionText;
    }
    
    /**
     * Метод получения информации о необходимости генерации исключительной ситуации
     * @return Если необходима генерация исключительной ситуации - true
     */
    public boolean getIsNewExcept(){
      return genExcept; 
    } 
     
    public void ShownMsgOnScreen(String eText) {
      System.out.println("On Screen: "+eText);
    }
    
    private static String getMsgText(String mco_code, String mmc_code){
      return mco_code + mmc_code;
    }     
    
}


