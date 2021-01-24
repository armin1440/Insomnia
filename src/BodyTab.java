import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.*;

/**
 * Class BodyTab
 * Inherits from JPanel.
 * Makes a panel for the message body and header tab in the center part.
 * @author Armin Rezaee
 * @version 1.0
 */
public class BodyTab extends JPanel {

        private String keyOrHeader;
        private String valueOrNewValue;
        private ArrayList<KeyAndValue> fields;

        /**
         * Constructs a panel with one key and value.
         * @param bodyOrHeader the type of key and value , to distinguish between header tab
         *                     and message body tab.
         */
        public BodyTab(String bodyOrHeader){
                super();
                fields = new ArrayList<>();

                setBackground(Color.DARK_GRAY);
                BoxLayout boxLayout = new BoxLayout( this , BoxLayout.Y_AXIS );

                setLayout(boxLayout);

                if (bodyOrHeader.equals("Body")){
                        keyOrHeader = " New Name";
                        valueOrNewValue = " New Value";
                }else{
                        keyOrHeader = " header";
                        valueOrNewValue = " value";
                }

                KeyAndValue init = new KeyAndValue();
                add(init );
                fields.add(init);
        }

        /**
         * Adds a key and a value.
         */
        public void addField(){
                KeyAndValue newField = new KeyAndValue();
                add(newField );
                fields.add(newField);
                updateUI();
        }

        public void addField(String key , String value){
                KeyAndValue newField = new KeyAndValue();
                add(newField );
                fields.add(newField);
                newField.setCheckBox(true);
                newField.setKey(key);
                newField.setValue(value);
                updateUI();
        }

        /**
         * Removes the given key and the value.
         * @param toBeDeleted the key and the value that is going to be deleted.
         */
        public void removeKeyAndValue(KeyAndValue toBeDeleted){

                if(fields.size() == 1){
                        JOptionPane.showMessageDialog(this , "The last one can not be deleted" , "Error" , JOptionPane.PLAIN_MESSAGE);
                }
                else {
                        Iterator<KeyAndValue> iterator = fields.iterator();
                        while (iterator.hasNext()) {
                                KeyAndValue it = iterator.next();
                                if (it.equals(toBeDeleted)) {
                                        iterator.remove();
                                        System.out.println("removed from fields");
                                        updateUI();
                                }
                        }
                        remove(toBeDeleted);
                }
        }

        public ArrayList<KeyAndValue> getFields() {
                return fields;
        }

        /**
         * Class KeyAndValue
         * It is an inner class because it needs to have access to the methods and fields of
         * the class BodyTab.
         * It makes a key and a value with a checkbox and a delete button.
         * @author Armin Rezaee
         * @version 1.0
         */
        public class KeyAndValue extends JPanel {

                private JTextField key;
                private JTextField value;
                private JCheckBox checkBox;
                private JButton delete;
                private boolean madeNew = false;
                private final int height = 30;
                private final int width = 380;

                /**
                 * Constructs a key and a value.
                 */
                public KeyAndValue(){
                        setLayout(new FlowLayout());
                        setPreferredSize(new Dimension(width,height));
                        setBackground(Color.DARK_GRAY);
                        setOpaque(true);

                        int fieldWidth = (int)(width*0.7);
                        int fieldHeight = height;

                        value = new JTextField(valueOrNewValue);
                        value.setPreferredSize(new Dimension((int)(fieldWidth*0.4) ,fieldHeight) );
                        value.setBorder(BorderFactory.createEtchedBorder());
                        value.setFont(new Font("Calibri" , Font.PLAIN , 17));
                        value.setBackground(Color.DARK_GRAY);
                        value.setForeground(Color.WHITE);
                        value.setOpaque(true);
                        value.addFocusListener(new FocusListener() {
                                @Override
                                public void focusGained(FocusEvent e) {
                                        if ( value.getText().equals(valueOrNewValue) )
                                                value.setText("");
                                        if ( key.getText().equals(keyOrHeader) && !madeNew ) {
                                                addField();
                                                madeNew = true;
                                        }
                                }

                                @Override
                                public void focusLost(FocusEvent e) {

                                }
                        });

                        key = new JTextField(keyOrHeader);
                        key.setPreferredSize(new Dimension((int)(fieldWidth*0.4) ,fieldHeight));
                        key.setBorder(BorderFactory.createEtchedBorder());
                        key.setFont(new Font("Calibri" , Font.PLAIN , 17));
                        key.setBackground(Color.DARK_GRAY);
                        key.setForeground(Color.WHITE);
                        key.setOpaque(true);
                        key.addFocusListener(new FocusListener() {
                                @Override
                                public void focusGained(FocusEvent e) {
                                        if ( key.getText().equals(keyOrHeader))
                                                key.setText("");
                                        if (value.getText().equals(valueOrNewValue) && !madeNew) {
                                                madeNew = true;
                                                addField();
                                        }
                                }

                                @Override
                                public void focusLost(FocusEvent e) {

                                }
                        });

                        JPanel fields = new JPanel(new GridLayout(1,2));
                        fields.setPreferredSize(new Dimension(fieldWidth,fieldHeight));
                        fields.add(key);
                        fields.add(value);

                        int buttonsHeight = height;
                        int buttonsWidth = (int)(width*0.3);

                        checkBox = new JCheckBox();
                        checkBox.setPreferredSize(new Dimension( (int)(buttonsWidth*0.2) , buttonsHeight));
                        checkBox.setBackground(Color.DARK_GRAY);
                        checkBox.setOpaque(true);

                        delete = new JButton("X");
                        delete.setPreferredSize(new Dimension( (int)(buttonsWidth*0.4) , (int)(buttonsHeight*0.7) ));
                        delete.setBackground(Color.DARK_GRAY);
                        delete.setOpaque(true);
                        delete.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                        removeKeyAndValue(KeyAndValue.this);
                                }
                        });

                        JPanel buttons = new JPanel(new FlowLayout());
                        buttons.setPreferredSize(new Dimension(buttonsWidth,buttonsHeight));
                        buttons.setBackground(Color.DARK_GRAY);
                        buttons.add(checkBox, FlowLayout.LEFT);
                        buttons.add(delete , FlowLayout.CENTER);

                        add(fields , FlowLayout.LEFT);
                        add(buttons , FlowLayout.CENTER);
                }

                /**
                 * Returns the key field
                 * @return the key field
                 */
                public JTextField getKey(){
                        return key;
                }

                /**
                 * Returns the value field
                 * @return the value field
                 */
                public JTextField getValue() {
                        return value;
                }

                /**
                 * Returns the checkbox of the field
                 * @return the checkbox of the field
                 */
                public JCheckBox getCheckBox() {
                        return checkBox;
                }

                /**
                 * Sets the value of the checkbox
                 * @param selected to be set value of the checkbox
                 */
                public void setCheckBox(boolean selected) {
                        this.checkBox.setSelected(selected);
                }

                /**
                 * Sets the key of the field.
                 * @param key to be set key
                 */
                public void setKey(String key){
                        this.key.setText(key);
                }

                /**
                 * Sets the key of the field.
                 * @param key to be set key
                 */
                public void setValue(String value){
                        this.value.setText(value);
                }
        }
}
