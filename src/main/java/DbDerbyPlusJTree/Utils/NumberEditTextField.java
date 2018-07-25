package DbDerbyPlusJTree.Utils;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 *
 * @author gsedelnikov
 */
public class NumberEditTextField extends JTextField {
    private boolean allowFractionalPart = false;
    private boolean allowNegativePart   = false;
    
    private int maxValue = Integer.MAX_VALUE;
    private int minValue = Integer.MIN_VALUE;
    
    private static final String minus = "-";

    public NumberEditTextField() {
        super();
        
        this.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if (allowNegativePart) {
                    if (getText().equals(minus)) {
                        setText("");
                    }
                }
            }

        });                
    }

    public boolean isAllowFractionalPart() {
        return allowFractionalPart;
    }

    public void setAllowFractionalPart(boolean allowFractionalPart) {
        this.allowFractionalPart = allowFractionalPart;
    }
    
    public void setAllowNegativePart(boolean allowNegativePart) {
        this.allowNegativePart = allowNegativePart;
    }    

    public Number getValue() {
        Number res = null;
        try {
            if(allowFractionalPart){
                res=Double.valueOf(this.getText());
            }else{
                res = Long.valueOf(this.getText());
            }
        } catch (NumberFormatException e) {
        }
        return res;
    }

    public void setValue(Long value) {
        if(value==null){
            this.setText("");
        }else{
            if(value.doubleValue()-value.longValue()!=0){
                this.setText(value.toString());
            }else{
                this.setText(Long.toString(value.longValue()));
            }
        }
    }    
    
    public void setValue(Number value) {
        if(value==null){
            this.setText("");
        }else{
            if(value.doubleValue()-value.longValue()!=0){
                this.setText(value.toString());
            }else{
                this.setText(Long.toString(value.longValue()));
            }
        }
    }

    @Override
    protected Document createDefaultModel() {
        return new NumberTextDocument();
    }

    class NumberTextDocument extends PlainDocument {

        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            String insertStr = str.trim();
            String currentText = getText(0, getLength());
            String beforeOffset = currentText.substring(0, offs);
            String afterOffset = currentText.substring(offs, currentText.length());
            String proposedResult = beforeOffset + insertStr + afterOffset;
            boolean borderCorrect = true;

            if (proposedResult.length() == 0) {
                super.insertString(offs, str, a);//.insertString(offs, str, a);
            } else {
                try {
                    if ((allowNegativePart) && (insertStr.equals(minus)) && (beforeOffset.equals(""))) {
                    } else {
                        if ((insertStr.equals(minus)) && (beforeOffset.equals(""))) {
                          if ((allowNegativePart)){  
                            super.insertString(offs, insertStr, a);
                          }  
                           return;
                        } else {
                            if (allowFractionalPart) {
                                Double val = Double.valueOf(proposedResult);
                                if ((val > maxValue) || (val < minValue))
                                  borderCorrect = false;  
                            } else {
                              Long val = Long.valueOf(proposedResult);
                                if ((val > maxValue) || (val < minValue))
                                  borderCorrect = false;  
                            }
                        }
                    }
                    if (borderCorrect)
                      super.insertString(offs, insertStr, a);
                } catch (NumberFormatException e) {
                }
            }
        }

        @Override
        public void remove(int offs, int len) throws BadLocationException {
            String currentText = getText(0, getLength());
            String beforeOffset = currentText.substring(0, offs);
            String afterOffset = currentText.substring(len + offs, currentText.length());
            String proposedResult = beforeOffset + afterOffset;

            if (proposedResult.length() == 0) {
                super.remove(offs, len);
            } else {
                try {
                    if ((allowNegativePart) && (proposedResult.equals(minus))) {
                    } else {
                        if (proposedResult.equals(minus)) {
                            super.remove(offs, len);
                            return;
                        } else {
                            if (allowFractionalPart) {
                                Double.valueOf(proposedResult);
                            } else {
                                Long.valueOf(proposedResult);
                            }
                        }
                    }
                    super.remove(offs, len);
                } catch (NumberFormatException e) {
                }
            }
        }
        
    }         

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) { 
        this.maxValue = maxValue;
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    
}