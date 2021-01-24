import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Class ResponseInterface
 * Inherits from JPanel.
 * Makes an interface for the response part containing two parts.
 */
public class ResponseInterface extends JPanel {

    private JPanel upperPart;
    private JTabbedPane tabs;
    private JLabel status;
    private JLabel data;
    private JButton copy;
    private JComboBox messageBody;
    private JPanel body;
    private JPanel header;
    private JTextArea response;
    private JPanel responseHeaders;
    private Controller controller;
    private JPanel pic;

    /**
     * Constructs a response interface.
     */
    public ResponseInterface(){
        super();
        pic = new JPanel();
        pic.setBackground(Color.DARK_GRAY);
        pic.setOpaque(true);

        initializePanel();

        makeUpperPart();

        makeTabs();

        add(upperPart , BorderLayout.NORTH);
        add(tabs);
    }

    /**
     * Sets the properties of the main panel.
     */
    private void initializePanel(){
        setBorder( BorderFactory.createLineBorder(Color.GRAY,1) );
        setPreferredSize(new Dimension(400,500));
        setBackground(Color.DARK_GRAY);
        setLayout(new BorderLayout());
        setOpaque(true);
    }

    /**
     * Makes the upper part.
     * There are 3 labels to show status , data and time.
     */
    private void makeUpperPart(){
        upperPart = new JPanel();
        upperPart.setPreferredSize(new Dimension(380,70));
        upperPart.setBackground(Color.WHITE);
        upperPart.setOpaque(true);
        upperPart.setLayout(new FlowLayout());

        status = new JLabel("");
        status.setPreferredSize(new Dimension(120,40));
        status.setBackground(Color.RED);
        status.setForeground(Color.WHITE);
        status.setHorizontalAlignment(SwingConstants.CENTER);
        status.setOpaque(true);

        JLabel time = new JLabel("N/A");
        time.setPreferredSize(new Dimension(70,40));
        time.setBackground(Color.lightGray);
        time.setForeground(Color.black);
        time.setHorizontalAlignment(SwingConstants.CENTER);
        time.setOpaque(true);

        data = new JLabel("");
        data.setPreferredSize(new Dimension(70 ,40));
        data.setBackground(Color.lightGray);
        data.setForeground(Color.black);
        data.setHorizontalAlignment(SwingConstants.CENTER);
        data.setOpaque(true);

        JPanel labels = new JPanel(new BorderLayout(5 , 1));
        labels.setBackground(Color.white);
        labels.setOpaque(true);
        labels.setPreferredSize(new Dimension(170,50));
        labels.add(status , BorderLayout.CENTER);
//        labels.add(time);
        labels.add(data , BorderLayout.EAST);

        upperPart.add(labels);
    }

    /**
     * Makes the tabs.
     * The tabs are message body tab and header tab.
     */
    private void makeTabs(){
        tabs = new JTabbedPane();
        tabs.setPreferredSize(new Dimension(380,400));
        tabs.setBackground(Color.DARK_GRAY);
        tabs.setOpaque(true);

        //Header
        header = new JPanel();
        header.setLayout(new BorderLayout());
        header.setBackground(Color.DARK_GRAY);
        header.setOpaque(true);

        copy = new JButton("Copy To ClipBoard");
        copy.setPreferredSize(new Dimension(60,30));
        copy.setBackground(Color.DARK_GRAY);
        copy.setForeground(Color.WHITE);
        copy.setBorder(BorderFactory.createLineBorder(Color.gray , 1));
        copy.setOpaque(true);
        copy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringBuilder string = new StringBuilder();
                for ( Component component : responseHeaders.getComponents() ){
                    JPanel row = (JPanel)component;
                    for ( Component rowComponent : row.getComponents() ){
                        JLabel label = (JLabel)rowComponent;
                        string.append(label.getText() + "\t");
                    }
                    string.append("\n");
                }
                System.out.println(string.toString());

                String stringToCopy = string.toString();
                StringSelection stringSelection = new StringSelection(stringToCopy);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
            }
        });

        responseHeaders = new JPanel();
        BoxLayout boxLayout = new BoxLayout(responseHeaders , BoxLayout.Y_AXIS  );
        responseHeaders.setLayout(boxLayout);

        header.add(responseHeaders , BorderLayout.NORTH);
        header.add(copy , BorderLayout.SOUTH);

        //body
        body = new JPanel(new BorderLayout());
        body.setBackground(Color.DARK_GRAY);
        body.setOpaque(true);

        response = new JTextArea("");
        response.setBackground(Color.darkGray);
        response.setForeground(Color.white);
        response.setEditable(false);
        response.setBorder(BorderFactory.createEmptyBorder());
        response.setOpaque(true);

        String[] bodies = new String[2];
        bodies[0] = "Raw";
        bodies[1] = "Preview";
        messageBody = new JComboBox(bodies);
        messageBody.setPreferredSize(new Dimension(80,40));
        messageBody.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) (messageBody.getSelectedItem());
                tabs.setTitleAt(0, selected);

                if (selected == null) {
                    pic.removeAll();
                    body.remove(pic);
                } else {
                    if (selected.equals("Preview")) {
                        body.remove(response);
                        showImage();
                    } else {
                        body.remove(pic);
                        pic.removeAll();
                        controller.showResponse();
                        body.add(response, BorderLayout.CENTER);
                    }
                    body.updateUI();
                }
            }
        });

        body.add(messageBody , BorderLayout.NORTH);
        body.add(response , BorderLayout.CENTER);

        JScrollPane bodyScroller = new JScrollPane(body );
        bodyScroller.setBorder(BorderFactory.createEmptyBorder());
        JScrollPane headerScroller = new JScrollPane(header);
        headerScroller.setBorder(BorderFactory.createEmptyBorder());

        tabs.addTab("Body" , bodyScroller);
        tabs.addTab("Header" , headerScroller);
    }

    /**
     * This method makes some key and values for the header tab.
     * @return the panel made that has examples in it.
     */
    private JPanel makeExamples(){
        //these are examples
        JPanel example = new JPanel();
        BoxLayout layout = new BoxLayout(example , BoxLayout.Y_AXIS);
        example.setLayout(layout);

        example.add(makeRow( "Server" , "nginx"));
        example.add(makeRow( "Date" , "Mon , 11 May 01:03 AM"));
        example.add(makeRow("Content-Type" , "text/html"));
        example.add(makeRow("Content-Length" , "13367"));
        example.add(makeRow("Connection" , "keep-alive"));
        example.add(makeRow("Last-Modified" , "May , 11 May 01:07"));
        return example;
    }

    /**
     * Makes a key and a value which are not editable.
     * @param leftString the text of the key.
     * @param rightString the text of the value.
     * @return the key and the value made.
     */
    private JPanel makeRow(String leftString , String rightString){
        JPanel row = new JPanel(new GridLayout(1,2));
        row.setPreferredSize(new Dimension(200,40));
        row.setBackground(Color.darkGray);
        row.setForeground(Color.white);
        row.setOpaque(true);

        JLabel left = new JLabel(leftString);
        left.setBorder(BorderFactory.createLineBorder(Color.gray , 1));
        left.setBackground(Color.darkGray);
        left.setForeground(Color.white);
        left.setOpaque(true);
        left.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel right = new JLabel(rightString);
        right.setBorder(BorderFactory.createLineBorder(Color.gray , 1));
        right.setBackground(Color.darkGray);
        right.setForeground(Color.white);
        right.setOpaque(true);
        right.setHorizontalAlignment(SwingConstants.CENTER);

        row.add(left);
        row.add(right);

        return row;
    }

    /**
     * Returns the lable which shows downloaded data
     * @return the lable which shows downloaded data
     */
    public JLabel getData() {
        return data;
    }

    /**
     * Returns the lable which shows status
     * @return the lable which shows status
     */
    public JLabel getStatus() {
        return status;
    }

    /**
     * Returns the response header panel
     * @return the response header panel
     */
    public JPanel getHeader() {
        return header;
    }

    /**
     * Returns the response body panel
     * @return the response body panel
     */
    public JPanel getBody() {
        return body;
    }

    /**
     * Sets the response.
     * @param response to be set response
     */
    public void setResponse(String response){
        this.response.setText(response);
        updateUI();
    }

    /**
     * Adds a response header to the header tab.
     * @param key key of the response
     * @param value value of the response
     */
    public void addResponseHeader(String key , String value){
        JPanel responseHeader = makeRow(key , value);
        this.responseHeaders.add(responseHeader);
    }

    /**
     * Sets the controller of this panel
     * @param controller to be set controller.
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Shows the image saved in the body panel ( preview tab )
     */
    public void showImage(){
        String picName = "Name of picture";
        if ( controller.getUrl().contains("http") ){
            picName = controller.getUrl().substring(7).replace("/","_");
        }else {
            picName = controller.getUrl().replace("/","_");
        }
        try {
            BufferedImage myPicture = ImageIO.read(new File("./pic" + "/" + picName +".png"));
            JLabel picLabel = new JLabel(new ImageIcon(myPicture));
            pic.add(picLabel);
            body.add(pic , BorderLayout.CENTER);
        }
        catch (IOException e){
            System.out.println("Image not found");
            pic.removeAll();
            pic.updateUI();
//            e.printStackTrace();
        }
    }

    /**
     * Removes response headers.
     */
    public void removeResponseHeaders(){
        responseHeaders.removeAll();
    }

}
