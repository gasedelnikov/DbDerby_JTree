package DbDerbyPlusJTree.Ibcon;

import DbDerbyPlusJTree.DB.Database;
import java.sql.SQLException;

/**
 *
 * @author GSedelnikov
 */
public class DbUtils {

    /**
     * Create Tables WBS, ACT and VIEW TREE
     * @throws SQLException
     */
    public static void createTables() throws SQLException{
     try {
         Database.executeBlock("DROP VIEW TREE");   
     } catch (SQLException ex) {
     }   
     try {  
         Database.executeBlock("DROP TABLE ACT");  
     } catch (SQLException ex) {
     }  
     try {
         Database.executeBlock("DROP TABLE WBS");    
     } catch (SQLException ex) {
     }       

     Database.executeBlock("CREATE TABLE WBS( WBS_ID INTEGER NOT NULL"
                                         + ", NAME VARCHAR(255)"
                                         + ", WBS_WBS_ID INTEGER"
                                         + ", QUANT INTEGER)");  
     Database.executeBlock("CREATE UNIQUE INDEX PK_WBS ON WBS (WBS_ID ASC)");  

     Database.executeBlock("CREATE TABLE ACT(WBS_ID INTEGER"
                                         + ", ACT_ID INTEGER NOT NULL"
                                         + ", NAME VARCHAR(255)"
                                         + ", QUANT INTEGER)");  
     Database.executeBlock("CREATE UNIQUE INDEX PK_ACT ON ACT (WBS_ID ASC, ACT_ID ASC)");

     Database.executeBlock("CREATE VIEW TREE (WBS_ID, ACT_ID, NAME, QUANT, WBS_WBS_ID) AS"+
                          " SELECT WBS_ID, -1 AS ACT_ID, NAME, QUANT, WBS_WBS_ID"+
                            " FROM WBS" +
                           " UNION ALL"+
                          " SELECT WBS_ID, ACT_ID, NAME, QUANT, WBS_ID AS WBS_WBS_ID"+
                            " FROM ACT");             
       
  }

    /**
     * Update Wbs.Quant from children
     * @throws SQLException
     */
    public static void updateWbsQuant() throws SQLException {
      Database.executeBlock(
          "UPDATE WBS A"+
             " SET QUANT = (SELECT SUM(B.QUANT) " +
                             " FROM TREE B"+
                            " WHERE B.WBS_WBS_ID = A.WBS_ID)" +
         " WHERE QUANT = 0"  +
          " AND EXISTS("    +
                  "(SELECT 0 " +
                    " FROM TREE B"+
                   " WHERE B.WBS_WBS_ID = A.WBS_ID))"                      
      );
    }

    public static void clearWbsAct() throws SQLException {
       Database.executeBlock("DELETE FROM ACT");   
       Database.executeBlock("DELETE FROM WBS");  
    }
   
    /**
     * Insert data Into WBS
     * @param wbs_id
     * @param wbs_wbs_id
     * @param name
     * @throws SQLException
     */
    public static void insertIntoWBS(int wbs_id, Integer wbs_wbs_id, String name) throws SQLException {
//        System.out.println(" wbs_id: " + wbs_id + " parent: " + wbs_wbs_id + " name: " + name);
      Database.executeBlock("INSERT INTO WBS (WBS_ID, WBS_WBS_ID, NAME, QUANT)" +
                           " VALUES("+wbs_id + "," + wbs_wbs_id + ",'" + name + "', 0)"   
      );
    }

    /**
     * Insert data Into ACT
     * @param wbs_id
     * @param act_id
     * @param name
     * @param quant
     * @throws SQLException
     */
    public static void insertIntoACT(int wbs_id, int act_id, String name, int quant) throws SQLException{
//       System.out.println(" wbs_id: " + wbs_id + " act_id: " + act_id + " name: " + name + " quant: " + quant);  
      Database.executeBlock("INSERT INTO ACT (WBS_ID, ACT_ID, NAME, QUANT)" +
                           " VALUES("+wbs_id + "," + act_id + ",'" + name + "'," + quant +")"   
      );
    }      
}
