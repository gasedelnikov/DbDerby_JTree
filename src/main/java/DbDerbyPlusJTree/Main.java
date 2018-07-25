package DbDerbyPlusJTree;

import DbDerbyPlusJTree.DB.Database;
import DbDerbyPlusJTree.DB.DerbyDBManager;
import DbDerbyPlusJTree.DB.InterfaceDB;
import DbDerbyPlusJTree.Ibcon.DataGen;
import DbDerbyPlusJTree.Ibcon.DbUtils;
import DbDerbyPlusJTree.Ibcon.MainFrame;
import DbDerbyPlusJTree.UTree.UExceptions.ExceptionSQLTextErr;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import org.jdesktop.application.SingleFrameApplication;

/**
 *
 * @author gsedelnikov
 */
public class Main extends SingleFrameApplication {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(Main.class, args); 
    }
    
    @Override
    protected void startup() {
       InterfaceDB db = new DerbyDBManager("DerbyDB", "gasedelnikov", "qwer");
       db.createConnection();
       Database.setDb(db);
       DataGen dataGen = new DataGen();
       
       if (db.isConnected()){
           try {
             DbUtils.createTables();
           } catch (SQLException ex) {
             JOptionPane.showMessageDialog(null, "Произошла ошибка создания таблиц");            
           } 

           MainFrame mainFrame = new MainFrame(this, dataGen);
           try {
                mainFrame.refresh();
           } catch (ExceptionSQLTextErr ex) {
              JOptionPane.showMessageDialog(null, "Произошла ошибка построения дерева\r\n"
                                                 + ex.toString());   
           } catch (SQLException ex) {
               JOptionPane.showMessageDialog(null, "Произошла ошибка работы с таблицами\r\n"
                                                   + ex.toString()); 
           }
           show(mainFrame);
       }
       else{
          JOptionPane.showMessageDialog(null, "Произошла ошибка подключения к БД");   
       }
    }      
}
