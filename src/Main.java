import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.Scanner;
import java.awt.datatransfer.*;
import java.awt.Toolkit;

public class Main {

    public static void main(String[] args){

        try{
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        Manager manager = new Manager();
        Model model = new Model();
        InsomniaUi ui = new InsomniaUi();
        Controller controller = new Controller(model , ui , manager);
        model.setInsomniaUi(ui);
        ui.setController(controller);
        ui.setSize(model.getWidth() , model.getHeight());
        ui.display();

    }
}
