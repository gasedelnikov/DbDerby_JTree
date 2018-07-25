package DbDerbyPlusJTree.DB;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author GSedelnikov
 */
public interface InterfaceDB {
    
   public boolean isConnected();
   public boolean createConnection();
   public void executeBlock(String sql) throws SQLException ;
   public ResultSet getResultSet(String sql) throws SQLException;
   public ResultSet getResultSet(String sql, Object[] sqlParams) throws SQLException;

}
