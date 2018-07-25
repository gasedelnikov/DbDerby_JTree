package DbDerbyPlusJTree.Ibcon;

import DbDerbyPlusJTree.UTree.UExceptions.ExceptionSQLTextErr;
import DbDerbyPlusJTree.UTree.UTreeSimple;
import DbDerbyPlusJTree.Utils.NumberEditTextField;
import DbDerbyPlusJTree.Utils.UFields;
import DbDerbyPlusJTree.Utils.UTypes;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.jdesktop.application.Application;
import org.jdesktop.application.FrameView;

/**
 *
 * @author GSedelnikov
 */
public class MainFrame extends FrameView{
    private final DataGen dataGen;
    
    private UTreeSimple tvwMain;
    private JCheckBox cbxExpandAll;
    private NumberEditTextField ntfMaxWbs;
    private NumberEditTextField ntfMaxAct;
    private NumberEditTextField ntfMaxWbsLev;
    
    public MainFrame(Application aplctn, DataGen dataGen) {
       super(aplctn);
       this.dataGen = dataGen; 
       init();
    }
    
    /**
     * Refresh MainFrame
     * @throws ExceptionSQLTextErr
     * @throws java.sql.SQLException
     */
    public void refresh() throws ExceptionSQLTextErr, SQLException {
       dataGen.refresh();
       tvwMain.setExpandAll(cbxExpandAll.isSelected());
       tvwMain.refresh();
    }    
    
    private void init(){
      this.getFrame().setTitle("Дерево");
      this.getFrame().setLayout(new BorderLayout(0, 0)); 
      this.getFrame().setPreferredSize(new Dimension(800, 300));
      this.getFrame().setMinimumSize(new Dimension(800, 300));    
      
      JPanel panMain   = new JPanel(new BorderLayout(0, 0) ); 
      JPanel panTop    = new JPanel(); 
      JPanel panBottom = new JPanel(new BorderLayout(0, 0) ); 
      
      panTop.setLayout(new BoxLayout(panTop, BoxLayout.X_AXIS));
       
      cbxExpandAll = new JCheckBox("Открывать все узлы");
      panTop.setBorder(BorderFactory.createLineBorder(Color.BLACK));      
      ntfMaxWbs = new NumberEditTextField();
      ntfMaxWbs.setValue(dataGen.getMaxWbs());
      ntfMaxWbs.setMinValue(1);
      ntfMaxAct = new NumberEditTextField();
      ntfMaxAct.setValue(dataGen.getMaxAct());     
      ntfMaxWbsLev = new NumberEditTextField();
      ntfMaxWbsLev.setValue(dataGen.getMaxWbsLev()); 
      ntfMaxWbsLev.setMinValue(1);
      
      JButton btnGen = new JButton(new AbstractAction("Генерировать") {
        @Override
        public void actionPerformed(ActionEvent e) {
          generate();
        }
      });
      JButton btnClose = new JButton(new AbstractAction("Закрыть") {
        @Override
        public void actionPerformed(ActionEvent e) {
          closeForm();
        }
      });      
      
      createTree();
      JScrollPane scpMain = new JScrollPane();
      scpMain.setViewportView(tvwMain);
      
      this.getFrame().add(panMain);
      panMain.add(panTop, BorderLayout.NORTH);
      panMain.add(panBottom, BorderLayout.SOUTH);
      panMain.add(scpMain, BorderLayout.CENTER);
      panTop.add(btnGen);
      panTop.add(cbxExpandAll);

      panTop.add(cteateTitledPanel(ntfMaxWbs, "Кол. строк в WBS"));
      panTop.add(cteateTitledPanel(ntfMaxAct, "Кол. строк в Activities"));
      panTop.add(cteateTitledPanel(ntfMaxWbsLev, "Уровень вложенности в WBS"));
      panBottom.add(btnClose);
    }

    private JPanel cteateTitledPanel(JComponent comp, String title){
      JPanel result = new JPanel(new BorderLayout(0, 0));
      result.add(comp, BorderLayout.CENTER);
      result.setBorder(BorderFactory.createTitledBorder(title));
      return result;
    }
    
    private void createTree(){
      String sqlTextRoot  = 
           "SELECT WBS_ID"
              + ", ACT_ID"
              + ", NAME||'('||trim(cast (QUANT  as CHAR(8))) ||')' AS NAME"
              + ", WBS_WBS_ID"
              + ", 1 as PARENT"
          + " FROM TREE "
          + "WHERE WBS_WBS_ID IS NULL";
      String sqlTextNodes = 
          "SELECT  WBS_ID"
              + ", ACT_ID"
              + ", NAME||'('||trim(cast (QUANT  as CHAR(8))) ||')' AS NAME"
              + ", QUANT"
              + ", WBS_WBS_ID"
              + ", CASE WHEN ACT_ID = -1 THEN 1 ELSE 0 END as PARENT"
         + " FROM TREE"
         + " WHERE WBS_WBS_ID = :WBS_ID";
      String sqlTextBrunch = null;
      Object[] in_fields = new Object[]{"WBS_ID", UFields.r_Id
                                      , "ACT_ID", UFields.r_Integer
                                      , "NAME"  , UFields.r_Text
                                      , "PARENT", UFields.r_Parent};
      tvwMain = new UTreeSimple(sqlTextRoot, sqlTextNodes, sqlTextBrunch, in_fields, null);
    }  
   
    private boolean checkVal(int val, int minVal){
       return  (val >= minVal);
    }
    
    private void generate(){
      int maxWbs = UTypes.getIntFromObject(ntfMaxWbs.getValue());
      int maxAct = UTypes.getIntFromObject(ntfMaxAct.getValue());
      int maxWbsLev = UTypes.getIntFromObject(ntfMaxWbsLev.getValue());  
      
      if ((checkVal(maxWbs, 1)) && (checkVal(maxWbsLev, 1))){
        dataGen.setParams(maxWbs, maxAct, maxWbsLev);
        try {
           DbUtils.clearWbsAct();
           refresh();
        } catch (ExceptionSQLTextErr ex) {
            JOptionPane.showMessageDialog(null, "Произошла ошибка построения дерева\r\n"
                                               + ex.toString());   
         } catch (SQLException ex) {
             JOptionPane.showMessageDialog(null, "Произошла ошибка работы с таблицами\r\n"
                                                 + ex.toString()); 
         }
      }
      else{
         JOptionPane.showMessageDialog(null
                                     , "Введите корректные данные!\r\n"+
                                       "'Кол. строк в WBS' и 'Кол. строк в Activities',"+
                                       " должны быть больше нуля." 
         );
      }
    }
    
    public void closeForm(){
      this.getFrame().dispose();
    }
}
