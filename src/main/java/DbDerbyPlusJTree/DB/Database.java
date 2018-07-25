package DbDerbyPlusJTree.DB;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Static class for working with BD
 * @author GSedelnikov
 */
public class Database {
    private static InterfaceDB db;

    public static InterfaceDB getDb() {
        return db;
    }

    public static void setDb(InterfaceDB interfaceDB) {
        db = interfaceDB;
    }

    public static void executeBlock(String sql) throws SQLException {
        db.executeBlock(sql);
    }

    public static ResultSet getResultSet(String sql) throws SQLException {
        return db.getResultSet(sql);
    }

    public static ResultSet getResultSet(String sql, Object[] sqlParams) throws SQLException {
        return db.getResultSet(sql, sqlParams);
    }    
    
}
