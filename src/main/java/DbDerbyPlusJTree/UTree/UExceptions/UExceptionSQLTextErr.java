
package DbDerbyPlusJTree.UTree.UExceptions;

/**
 *
 * @author gsedelnikov
 */


  public class UExceptionSQLTextErr extends UExceptions  {
    public  UExceptionSQLTextErr(String mco_code, String mmc_code, String exceptionPlace) throws ExceptionSQLTextErr {
       this(mco_code, mmc_code, exceptionPlace, UExceptions.exceptionModeDef);     
    }  
    public  UExceptionSQLTextErr(String mco_code, String mmc_code, String exceptionPlace, int autoException) throws ExceptionSQLTextErr {
       super(mco_code, mmc_code, exceptionPlace, autoException);
       if (super.getIsNewExcept())
           throw new ExceptionSQLTextErr(super.getExceptionText());
    }
  }