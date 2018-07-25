package UTree.UEvents;

import java.util.EventObject;
import javax.swing.tree.TreePath;

/**
 *
 * @author gsedelnikov
 */
public class UTreeUnivEvent extends EventObject {
       private TreePath[] nodes;
       private Object        objSource;
       
       public UTreeUnivEvent(Object objSource, TreePath[] nodes) {
         super(objSource);
         this.objSource = objSource;
         this.nodes = nodes;
       }
        
        public Object getSource() {
            return this.objSource;
        }
        public TreePath[] getNodeTreePaths() {
            return this.nodes;
        }    
        public TreePath getNodeTreePath(int index) {
          if ((this.nodes != null) && (index < this.nodes.length) )
            return this.nodes[index];
          else 
            return null;  
        }            
}
