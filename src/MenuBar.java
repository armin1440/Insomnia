import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Class MenuBar
 * Inherits from JMenuBar.
 * Includes a menu bar with 3 menus : application , view and help.
 * @author Armin Rezaee
 * @version 1.0
 */
public class MenuBar extends JMenuBar {

    private Controller controller;
    private JMenu application;
    private JMenu view;
    private JMenu help;

    /**
     * Constructs an object.
     */
    public MenuBar(){
        super();

        //setPreferredSize(new Dimension(200,40));

        makeApplication();
        makeView();
        makeHelp();

        add(application);
        add(view);
        add(help);
    }

    /**
     * Makes the application menu with two options : options and exit
     */
    private void makeApplication(){
        application = new JMenu("Application");
        application.setMnemonic('a');
        JMenuItem exit = new JMenuItem("Exit");
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E , InputEvent.CTRL_MASK));
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.save();
                if ( controller.isHide() ){
                    controller.hide();
                    System.out.println("Being hidden");
                }else {
                    System.out.println("Exit without being hidden");
                    System.exit(4);
                }
            }
        });
        JMenuItem options = new JMenuItem("Options");
        options.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O , InputEvent.CTRL_MASK));
        options.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                makeCheckBoxes();
            }
        });

        application.add(options);
        application.add(exit);
    }

    /**
     * Makes the view menu with one option : Toggle full screen.
     */
    private void makeView(){
        view = new JMenu(("View"));
        view.setMnemonic('v');
        JMenuItem toggleFullScreen = new JMenuItem("Toggle Full Screen");
        toggleFullScreen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( controller.isFullScreen() )
                    controller.setFullScreen(false);
                else
                    controller.setFullScreen(true);
            }
        });
        toggleFullScreen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T , InputEvent.CTRL_MASK));
        view.add(toggleFullScreen);
    }

    /**
     * Makes the help menu with two options : about and help.
     */
    private void makeHelp(){
        help = new JMenu("Help");
        help.setMnemonic('h');
        JMenuItem about = new JMenuItem("About");
        about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B , InputEvent.CTRL_MASK));
        about.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String aboutMe = "Name : Armin Rezaee\nStudent ID : 9723035\nEmail : arezaee79@gmail.com";
                JOptionPane.showMessageDialog(about , aboutMe , "About" , JOptionPane.PLAIN_MESSAGE );
            }
        });
        JMenuItem helpItem = new JMenuItem("Help");
        helpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J , InputEvent.CTRL_MASK));
        helpItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String h = Manager.help();
                JOptionPane.showMessageDialog(help , h , "Help" , JOptionPane.PLAIN_MESSAGE);
            }
        });
        help.add(about);
        help.add(helpItem);
    }

    /**
     * Makes a frame for checkboxes (hide and follow redirect) to show when "options" item
     * is chosen.
     */
    public void makeCheckBoxes(){

        JFrame optionChoices = new JFrame("options");
        optionChoices.setLocation(900,100);
        optionChoices.setSize(200,100);
        JPanel panel = new JPanel(new GridLayout(3,1));
        panel.setSize(new Dimension(200,200));

        JCheckBox redirect = new JCheckBox("follow redirect");
        redirect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.setRedirect( redirect.isSelected() );
            }
        });

        JCheckBox hide = new JCheckBox("Hide");
        hide.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.setHide( hide.isSelected() );
            }
        });

        int choices = controller.checkValuesOfOptions();

        switch (choices){
            case 12:
                redirect.setSelected(true);
                hide.setSelected(false);
                break;
            case 21:
                redirect.setSelected(false);
                hide.setSelected(true);
                break;
            case 22:
                redirect.setSelected(true);
                hide.setSelected(true);
                break;
            case 11:
            default:
                redirect.setSelected(false);
                hide.setSelected(false);
        }

        panel.add(redirect);
        panel.add(hide);
        optionChoices.add(panel);
        optionChoices.setVisible(true);
    }

    /**
     * Sets the controller of the class
     * @param controller to be set controller.
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }
}
