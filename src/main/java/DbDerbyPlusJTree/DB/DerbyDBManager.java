package DbDerbyPlusJTree.DB;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author GSedelnikov
 */
public class DerbyDBManager implements InterfaceDB{
    private static final String connectionDriver = "org.apache.derby.jdbc.EmbeddedDriver" ;
    private static final String connectionUrl    = "jdbc:derby:" ;
//    private static final String connectionUrl    = "jdbc:derby://localhost:1527/" ;
    private static Connection   connection = null ; 
    private  String dbName = null;
    private  String dbUser = null;
    private  String dbPas = null;

    public DerbyDBManager(String dbName, String dbUser, String dbPas) {
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPas  = dbPas;
    }

    @Override
    public boolean isConnected(){
       return (connection != null); 
    }
    
    @Override
    public boolean createConnection() {
       if(!createConnection(false)) { // Пытаемся найти БД
          createConnection(true);     // Создаем новую БД
       }
       return isConnected();
    }
 
    private boolean createConnection(boolean createNewBD) {
      try {
        Class.forName(connectionDriver);
//        System.out.println("getConnection: " + connectionUrl + dbName);
        String url = connectionUrl + dbName + ((createNewBD)?";create=true":"");
        connection = DriverManager.getConnection(url, dbUser, dbPas);
      } catch(ClassNotFoundException | SQLException e) {
        // ничего не делаем
      }
      return (connection != null);
    }
    
    /**
     * Запрос на обновление базы данных (INSERT, UPDATE, CREATE TABLE и т.п.)
     * @param sql
     * @throws SQLException
     */
    @Override
    public void executeBlock(String sql) throws SQLException {
      try (Statement stmt = connection.createStatement()) {
        stmt.executeUpdate(sql) ;
      }
    } 

    /**
     * Запрос на выборку данных из базы
     * @param sql
     * @return
     * @throws SQLException
     */       
    @Override
    public ResultSet getResultSet(String sql) throws SQLException {
      Statement stmt = connection.createStatement() ;
      ResultSet result = stmt.executeQuery(sql) ;
      return result;
    }
   
    /**
     * Запрос на выборку данных из базы с параметрами (PreparedStatement)
     * @param sql
     * @param params
     * @return
     * @throws SQLException
     */  
    @Override
    public ResultSet getResultSet(String sql, Object... params) throws  SQLException{
        if(params==null){
            params=new Object[0];
        }
        if (params.length %2!=0)
            throw new IllegalArgumentException("нечётное количество параметров params");
        
 //   PreparedStatement в Derby работает не так как надо => костыль
 //    подставляем значения вместо параметров
        for (int i = 0; i < params.length / 2; i++) {
          sql = sql.replaceAll(":"+params[i*2].toString(), params[i*2+1].toString());
        }
        
      Statement stmt = connection.createStatement() ;
      ResultSet result = stmt.executeQuery(sql) ;
      return result;
    }
   
}