import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

/**
 * Class InsomniaUi
 * Attaches different parts of the ui together and makes the whole ui.
 * @author Armin Rezaee
 * @version 1.0
 */
public class InsomniaUi {

    private Controller controller;
    private JFrame mainFrame;
    private JPanel mainPanel;
    private RequestHistoryInterface leftPart;
    private SetRequestInterface centerPart;
    private ResponseInterface rightPart;
    private MenuBar menuBar;

    /**
     * Constructs the ui using 4 parts. left , right and center part and the menu bar.
     */
    public InsomniaUi(){
        leftPart = new RequestHistoryInterface();
        rightPart = new ResponseInterface();
        centerPart = new SetRequestInterface();
        menuBar = new MenuBar();

        mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout(0,0,0));
        mainPanel.add(leftPart,FlowLayout.LEFT);
        mainPanel.add(centerPart,FlowLayout.CENTER);
        mainPanel.add(rightPart,FlowLayout.RIGHT);

        mainFrame = new JFrame("Insomnia");
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setMinimumSize(new Dimension(618,540));
        mainFrame.setSize( 1078 , 565 );
        mainFrame.setLocation(400,200);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT , leftPart , centerPart);
        splitPane.setDividerSize(1);
        splitPane.setResizeWeight(0.3);

        JSplitPane splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT , splitPane , rightPart);
        splitPane1.setDividerSize(1);
        splitPane1.setResizeWeight(0.6);

        mainFrame.add(menuBar , BorderLayout.NORTH);
        mainFrame.add(splitPane1 , BorderLayout.CENTER);
    }

    /**
     * Hides the ui in the system tray after exiting the program.
     * the hide option in options must be chosen.
     */
    public void hide() {
        mainFrame.setVisible(false);
        Image icon = null;
        try {
            icon = ImageIO.read(new File("./data/icon.jpg"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        TrayIcon trayIcon;
        SystemTray tray;
        tray = SystemTray.getSystemTray();
        PopupMenu menu = new PopupMenu();
        MenuItem show = new MenuItem("Show");
        MenuItem exit = new MenuItem("Exit");
        show.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.setState(Frame.NORMAL);
                mainFrame.setVisible(true);
            }
        });
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.save();
                System.exit(9);
            }
        });
        menu.add(show);
        menu.addSeparator();
        menu.add(exit);
        trayIcon = new TrayIcon(icon , "Insomnia", menu);
        trayIcon.setImageAutoSize(true);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    /**
     * Makes the frame fullscreen or the normal size.
     * @param full if true makes it fullscreen , if not, regular size.
     */
    public void setFullScreen(boolean full ){
        if ( full ) {
            mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            controller.setSize(1938 , 1048);
        }
        else{
            mainFrame.setSize(new Dimension(1078,565));
            mainFrame.setLocation(400,200);
            controller.setSize(1078,565);
        }
    }

    /**
     * Shows the ui.
     */
    public void display(){
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    /**
     * Adds a request to the requests history.
     * @param req the title of the request
     */
    public void addRequest(String req){
        leftPart.addRequest(req);
    }

    /**
     * Sets the controller of this class other classes.
     * @param controller the controller that is going to be set on classes.
     */
    public void setController(Controller controller){
        this.controller = controller;
        menuBar.setController(controller);
        centerPart.setController(controller);
        leftPart.setController(controller);
        rightPart.setController(controller);
    }

    /**
     * Sets the initial size of the ui.
     * @param width width of frame
     * @param height height of frame
     */
    public void setSize(int width , int height){
        if ( width == 1938 && height == 1048)
            setFullScreen(true);
        else {
            mainFrame.setPreferredSize(new Dimension(width, height));
            controller.setSize(mainFrame.getWidth(),mainFrame.getHeight());
        }
    }

    /**
     * Returns the width of the frame.
     * @return the width of the frame
     */
    public int getWidth(){
        return mainFrame.getWidth();
    }

    /**
     * Returns the height of the frame.
     * @return the height of the frame
     */
    public int getHeight(){
        return mainFrame.getHeight();
    }

    public SetRequestInterface getCenterPart() {
        return centerPart;
    }

    public RequestHistoryInterface getLeftPart() {
        return leftPart;
    }

    public void setRequests(ArrayList<Request> requests){
        leftPart.deleteAllRequests();
        for ( Request r : requests){
            addRequest(r.getNameOfReq());
        }
    }

    public ResponseInterface getRightPart() {
        return rightPart;
    }
}
