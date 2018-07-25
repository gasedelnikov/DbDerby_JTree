
package DbDerbyPlusJTree.UTree.UEvents;

import java.util.EventObject;

/**
 *
 * @author gsedelnikov
 */
public class UTreeRefreshEvent  extends EventObject {
       public static final int refreshByRefresh      = 0; 
        
       private Object objSource;
       private int reason = -1;
       
       public UTreeRefreshEvent(Object objSource, int reason) {
         super(objSource);
         this.objSource = objSource;
         this.reason = reason;

       }
        public void setReason(int reason) {
            this.reason = reason;
        }
        public int getReason() {
            return reason;
        }          
        public Object getSource() {
            return objSource;
        }
       
    
}
