import java.awt.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.util.Scanner;

public class Model {

    private InsomniaUi insomniaUi;
    private boolean redirect;
    private boolean hide;
    private int width;
    private int height;

    public Model(){

        Path path = Paths.get("./data/setting.txt");

        if( Files.exists(path) ) {
            try (FileReader loader = new FileReader("./data/setting.txt")) {
                Scanner scanner = new Scanner(loader);
                String input;
                while(scanner.hasNext()){
                    input = scanner.next();

                    if (input.equals("redirect")){
                        if ( scanner.next().equals("true") ) {
                            System.out.println("redirect true");
                            redirect = true;
                        }
                        else{
                            System.out.println("redirect false");
                            redirect = false;
                        }
                    }

                    if (input.equals("hide")){
                        System.out.print("hide");
                        if ( scanner.next().equals("true") ) {
                            System.out.println(" true");
                            hide = true;
                        }
                        else{
                            System.out.println(" false");
                            hide = false;
                        }
                    }
                    if (input.equals("width")){
                        System.out.print("width ");
                        width = scanner.nextInt();
                        System.out.println(width);
                    }
                    if(input.equals("height")){
                        System.out.print("height ");
                        height = scanner.nextInt();
                        System.out.println(height);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            redirect = false;
            hide = false;
            width = 1078;
            height = 565;
        }

    }

    public void save(){

        try(FileWriter fileWriter = new FileWriter("./data/setting.txt")){
            PrintWriter writer = new PrintWriter(fileWriter);
            writer.println("redirect " + redirect);
            writer.println("hide " + hide);
            width = insomniaUi.getWidth();
            height = insomniaUi.getHeight();
            writer.println("width " + width);
            writer.println("height " + height);
            System.out.println("saved");
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }

    public void setInsomniaUi(InsomniaUi insomniaUi){
        this.insomniaUi =insomniaUi;
    }

    public boolean isHide() {
        return hide;
    }

    public boolean isRedirect() {
        return redirect;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }
}
