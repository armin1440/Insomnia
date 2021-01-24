import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Date;

/**
 * Class SetRequestInterface
 * Inherits from JPanel.
 * Includes a request part (you enter url ... to make a request) and
 * another part to get message body and header values.
 */
public class SetRequestInterface extends JPanel {

    //the panel used for entering URLs.
    private JPanel requestGetter;
    private JTabbedPane tabs;
    private BodyTab bodyTab;
    private BodyTab headerTab;
    private JComboBox methodSelector;
    private JButton saveButton;
    private JButton sendButton;
    private JTextField url;
    private Controller controller;

    /**
     * Constructs an object.
     */
    public  SetRequestInterface(){
        super();

        initializePanel();

        makeRequestGetter();

        makeTabs();

        add(requestGetter , BorderLayout.NORTH);
        add(tabs , BorderLayout.CENTER);
    }

    /**
     * Sets the controller of the class.
     * @param controller to be set controller
     */
    public void setController(Controller controller){
        this.controller = controller;
    }

    /**
     * Makes the tabs. message body and header.
     */
    private void makeTabs(){
        tabs = new JTabbedPane();
        tabs.setPreferredSize(new Dimension(380,400));
        tabs.setBackground(Color.DARK_GRAY);
        tabs.setOpaque(true);

        bodyTab = new BodyTab("Body");

        JScrollPane scrollingBodyTab = new JScrollPane(bodyTab);
        scrollingBodyTab.setBorder(BorderFactory.createEmptyBorder());
        scrollingBodyTab.setPreferredSize(new Dimension(380,400));

        headerTab = new BodyTab("Header");
        JScrollPane scrollingHeaderTab = new JScrollPane(headerTab);
        scrollingHeaderTab.setBorder(BorderFactory.createEmptyBorder());
        scrollingHeaderTab.setPreferredSize(new Dimension(380 , 400));

        tabs.addTab("Body" , scrollingBodyTab );
        tabs.addTab("Header" , scrollingHeaderTab );
    }

    /**
     * Sets the properties of the main panel.
     */
    private void initializePanel(){
        setLayout(new BorderLayout(0,0));
        setBorder( BorderFactory.createLineBorder(Color.GRAY,1) );
        setPreferredSize(new Dimension(460,500));
        setBackground(Color.DARK_GRAY);
        setOpaque(true);
    }

    /**
     * Makes the upper part where you set your request.
     */
    private void makeRequestGetter(){
        requestGetter = new JPanel();
        requestGetter.setPreferredSize(new Dimension(380,70));
        requestGetter.setLayout(new FlowLayout(0,0,0));
        requestGetter.setBackground(Color.WHITE);
        requestGetter.setOpaque(true);

        String[] methods = new String[5];
        methods[0] = "GET";
        methods[1] = "PUT";
        methods[2] = "POST";
        methods[3] = "PATCH";
        methods[4] = "DELETE";
        methodSelector = new JComboBox<>(methods);
        methodSelector.setBackground(Color.WHITE);
        methodSelector.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY , 0));
        methodSelector.setPreferredSize(new Dimension(70,70));

        url = new JTextField();
        url.setFont(new Font("Calibri" , Font.PLAIN , 15));
        url.setPreferredSize(new Dimension(267,70));
        url.setText("https://api.myproduct.com/v1/users");
        url.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (url.getText().equals("https://api.myproduct.com/v1/users"))
                    url.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });

        sendButton = new JButton("Send");
        sendButton.setBackground(Color.WHITE);
        sendButton.setPreferredSize(new Dimension(60,70));
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (url.getText().isEmpty()){
                    String message = "You have not typed any url to send";
                    JOptionPane.showMessageDialog(sendButton , message , "Error" , JOptionPane.PLAIN_MESSAGE);
                }
                else{
                    controller.clearResponse();
                    Request request = controller.makeRequest();
                    request.setSaveReq(false);
                    request.execute();
                }
            }
        });

        saveButton = new JButton("Save");
        saveButton.setBackground(Color.WHITE);
        saveButton.setPreferredSize(new Dimension(60,70));
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog("Enter a name for your request ");
                if (name.isEmpty()){
                    Date date = new Date();
                    name = date.toString();
                    name = name.replace(":" , ".");
                    name = "output_" + name;
                }
                System.out.println("The name of the request is " + name);
                controller.saveRequest(name);
            }
        });

        JPanel buttons = new JPanel(new GridLayout(1,2));
        buttons.setPreferredSize(new Dimension(120,70));
        buttons.setBackground(Color.WHITE);
        buttons.setOpaque(true);
        buttons.add(sendButton);
        buttons.add(saveButton);

        requestGetter.add(methodSelector,FlowLayout.LEFT);
        requestGetter.add(url , FlowLayout.CENTER);
        requestGetter.add(buttons , FlowLayout.RIGHT);
    }

    /**
     * Returns body tab.
     * @return body tab.
     */
    public BodyTab getBodyTab() {
        return bodyTab;
    }

    /**
     * Returns header tab.
     * @return header tab.
     */
    public BodyTab getHeaderTab() {
        return headerTab;
    }

    /**
     * Returns the url bar
     * @return the url bar
     */
    public JTextField getUrl() {
        return url;
    }

    /**
     * Returns the combo box containing methods
     * @return the combo box containing methods
     */
    public JComboBox getMethodSelector() {
        return methodSelector;
    }
}
