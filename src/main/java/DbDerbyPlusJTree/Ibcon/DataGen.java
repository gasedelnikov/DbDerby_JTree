package DbDerbyPlusJTree.Ibcon;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author GSedelnikov
 */
public class DataGen {
   private final String wbsRootName = "WBS ";
   private final String actRootName = "ACT ";
   private final double wbsToBeParentRnd = 0.5d;
   private final int maxActQuant = 2000;    
   
   private int maxWbs = 70; 
   private int maxAct = 50; 
   private int maxWbsLev = 3;
   private int curWbsId ; 
   private final List<Integer> wbsLists = new ArrayList<>();    

   public DataGen() {
   }

    /**
     *
     * @param maxWbs Count of table WBS rows
     * @param maxAct Count of table ACT rows
     * @param wbsMaxLev Max Level of WBS  
     */
   public void setParams(int maxWbs, int maxAct, int wbsMaxLev) {
      this.maxWbs = maxWbs;
      this.maxAct = maxAct;
      this.maxWbsLev = wbsMaxLev;
   }

    /**
     * Generate data in tables WBS and ACT
     * @throws SQLException
     */
   public void refresh() throws SQLException {
      wbsLists.clear();
      curWbsId = 1;
      int parentNum = 0;
      while (curWbsId <= maxWbs){
        addWBS(0, 1, wbsRootName, parentNum++);
        
      }        
      addACT();
      for (int i=0; i < maxWbsLev; i++){
        DbUtils.updateWbsQuant();
      }
   }   
   
   private void addWBS(int parent, int lev, String parentName, int row ) throws SQLException{
     if ((curWbsId <= maxWbs) && (lev <= maxWbsLev)){
       curWbsId++;
       row++;
       DbUtils.insertIntoWBS(curWbsId, (parent == 0)?null:parent, getWbsName(lev, parentName, row));
       if (lev == maxWbsLev){
         wbsLists.add(curWbsId);
       }
       addWBS(curWbsId, lev + 1, getWbsName(lev, parentName, row), 0);
       if (lev != 1){
         if (Math.random() > wbsToBeParentRnd){
          addWBS(parent, lev, parentName, row);  
         }
       }
     } 
   }
   
   private String getWbsName(int lev, String parentName, int row){
      return (lev == 1)? parentName + Integer.toString(row) : (parentName+"."+Integer.toString(row)); 
   }
   
   private void addACT() throws SQLException{
     int cntLists = wbsLists.size();
     if (cntLists != 0){
       for (int i=1; i <= maxAct; i++){
         int index =  (int) Math.round(Math.random() * cntLists);
         int quant =  (int) Math.round(Math.random() * maxActQuant);

         if (index >= cntLists){
            index = cntLists -1;
         }
         DbUtils.insertIntoACT(wbsLists.get(index), i, actRootName + i, quant);
       }
     }
   }  

   public int getMaxWbs() {
     return maxWbs;
   }

   public int getMaxAct() {
     return maxAct;
   }

   public int getMaxWbsLev() {
     return maxWbsLev;
   }
 }
