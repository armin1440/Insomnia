import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Class Request History
 * This class is the UI of the left part of the app that stores requests.
 * Inherits from Class Jpanel.
 * @author Armin Rezaee
 * @version 1.0
 */
public class RequestHistoryInterface extends JPanel {

    //The big Insomnia in the blue rectangle
    private JLabel insomniaName;
    //the request part
    private JPanel requestPart;
    private Controller controller;

    /**
     * Constructs an object.
     */
    public RequestHistoryInterface(){
        super();

        initializePanel();

        makeInsomniaLabel();

        makeRequestPart();

        //making the request part scrollable
        JScrollPane scrollingRequestPart = new JScrollPane(requestPart);
        scrollingRequestPart.setBorder(BorderFactory.createEmptyBorder());
        scrollingRequestPart.setPreferredSize(new Dimension(200 , 400));

        /*addRequest("My Request");
        addRequest("Your Request");*/

        add(insomniaName,BorderLayout.NORTH);
        add(scrollingRequestPart,BorderLayout.CENTER);
    }

    /**
     * Makes the part where the requests are shown.
     */
    private void makeRequestPart(){
        requestPart = new JPanel();
        BoxLayout layout = new BoxLayout(requestPart , BoxLayout.PAGE_AXIS);
        requestPart.setLayout(layout);
        requestPart.setBackground(Color.DARK_GRAY);
        requestPart.setPreferredSize(new Dimension(180,400));

        //Manager.loadRequests2();
        //requestPart.setBorder(BorderFactory.createEmptyBorder());
    }

    /**
     * Sets the properties of the main panel.
     */
    private void initializePanel(){
        setBorder( BorderFactory.createLineBorder(Color.GRAY,1) );
        setLayout(new BorderLayout(0,0));
        setPreferredSize(new Dimension(200,500));
    }

    /**
     * Makes the blue label in top left corner of the ui.
     */
    private void makeInsomniaLabel(){
        insomniaName = new JLabel();
        insomniaName.setText("  Insomnia");
        insomniaName.setFont(new Font("Arial" , Font.BOLD , 22));
        insomniaName.setBackground(Color.BLUE);
        insomniaName.setForeground(Color.white);
        insomniaName.setOpaque(true);
        insomniaName.setPreferredSize(new Dimension(200,70));
    }

    /**
     * Creates and adds a request.
     * @param requestTitle title of the request that is going to be added.
     */
    public void addRequest(String requestTitle ){
        JLabel request = new JLabel(requestTitle);
        request.setFont(new Font("Calibri",Font.PLAIN,16));
        request.setPreferredSize(new Dimension(180 , 50));
        request.setBackground(Color.DARK_GRAY);
        request.setForeground(Color.white);
        request.setBorder( BorderFactory.createEmptyBorder(5,3,5,3));
        request.setOpaque(true);
        request.setFocusable(true);
        request.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                request.setBackground(Color.GRAY);
                request.setOpaque(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                request.setBackground(Color.darkGray);
                request.setOpaque(true);
            }

        });
        request.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                controller.loadRequest(request.getText());
            }
        });
        requestPart.add(request);
        requestPart.updateUI();
    }

    /**
     * Sets the controller of this part.
     * @param controller to be set controller.
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Deletes all requests in the request part.
     */
    public void deleteAllRequests(){
        requestPart.removeAll();
    }

    /**
     * Returns the request part
     * @return the request part
     */
    public JPanel getRequestPart() {
        return requestPart;
    }

}
