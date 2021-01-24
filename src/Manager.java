import javax.swing.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Class Manger
 * Includes requests. loads and saves them.
 * prints a list of requests and shows help.
 * @version 1.0
 * @author Armin Rezaee
 */
public class Manager {

    private ArrayList<Request> requests;
    private static final String requestsFolder = "./requests";

    /**
     * Constructs an object and load requests.
     */
    public Manager(){
        requests = new ArrayList<>();
        loadRequests();
    }

    /**
     * loads requests from the specified folder.
     */
    public void loadRequests(){
        requests.clear();
        File folder = new File(requestsFolder);
        File[] listOfFiles = folder.listFiles();
        if ( listOfFiles != null) {
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    try (FileInputStream inputStream = new FileInputStream(requestsFolder + "/" + listOfFiles[i].getName())) {
                        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                        Request request = (Request) objectInputStream.readObject();
                        if ( !requests.contains(request) )
                            requests.add(request);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static ArrayList<Request> loadRequests2(){
        File folder = new File(requestsFolder);
        ArrayList<Request> listOfReqeusts = new ArrayList<>();
        File[] listOfFiles = folder.listFiles();
        if ( listOfFiles != null) {
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    try (FileInputStream inputStream = new FileInputStream(requestsFolder + "/" + listOfFiles[i].getName())) {
                        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                        Request request = (Request) objectInputStream.readObject();
                        listOfReqeusts.add(request);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return listOfReqeusts;
    }

    /**
     * Saves a request to the specified folder.
     * @param request a request that is going to be saved
     */
    public static void saveRequest(Request request , String name){
        if ( name == null ) {
            name = request.getUrl().substring(7);
            name = name.replace("/", "_");
        }else if ( name.isEmpty() ){
            name = request.getUrl().substring(7);
            name = name.replace("/", "_");
        }
        request.setNameOfReq(name);
        File file = new File(requestsFolder + "/" + name +".bin");
//        if ( file.exists() ){
//            String message = "This file exists. Choose another name and try again";
//            JOptionPane.showMessageDialog(null , message , "ERROR" , JOptionPane.PLAIN_MESSAGE );
//            return;
//        }

        try ( FileOutputStream fileOutputStream = new FileOutputStream(requestsFolder + "/" + name +".bin")){
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(request);
            fileOutputStream.close();
            objectOutputStream.close();
//            System.out.println("the request saved to folder requests in the root");
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Shows a list of requests.
     * This method is called when a user enters "list" command.
     */
    public void showRequests(){
        int counter = 1;
        if ( requests.isEmpty()){
            System.out.println("There are no saved requests");
        }
        for (Request request : requests ) {
            System.out.print(counter +".  url: " + request.getUrl() + " | method: " + request.getMethod() );
            if ( !request.getHeaders().isEmpty() ){
                System.out.print(" | headers: ");
            }
            for ( String header: request.getHeaders().keySet() ){
                System.out.print( header + "=" + request.getHeaders().get(header) + " ; " );
            }
            System.out.println();
            counter++;
        }
    }

    /**
     * Shows help about this program and its commands.
     */
    public static String help(){
        System.out.println("This application helps you make a request and send it to a website and see its response");
        System.out.println("These are the commands of the app :");
        System.out.println("you have to type a website as the first argument");
        System.out.println("-h , --help  :  show the commands ");
        System.out.println("-O --output <name> :  save response; you can choose a name for the response file");
        System.out.println("-d --data <key=value&key1=value2> :  make a formdata body; It has to be in the format of the example");
        System.out.println("-H --headers <key:value;key1:value1> : set headers; It has to be in the format of the example");
        System.out.println("-M --method <method>  :  set a method  (GET,POST,DELETE,PUT)");
        System.out.println("-S --save  :  save the request");
        System.out.println("-i  :  show response headers");
        System.out.println("list  :  show the list of the saved requests");
        System.out.println("fire num1 num2 ...  :  send the chosen requests respectively");
        String help = "This application helps you make a request and send it to a website and see its response\n" +
                "These are the commands of the app :\n" +
                "help option (CTRL+J) :  show the commands \n" + "about option (CTRL+B) : information about me\n" +
                "toggle full screen (CTRL+T) : makes the program fullscreen\n" + "Options menu (CTRL+O) : you can" +
                " set hide property to true so it will be hidden in the system tray after you exit\n" + "Exit (CTRL+E) :" +
                " exits from the app\n" + "You can enter a website in the bar and save it as a request by clicking save button" +
                " and you can choose the method of the request and then send it\n" + "You can also set headers in the Header tab"
                + " and body in Body tab\n" + "You will see its response on the right hand side. You can see the headers of " +
                "the response in the Header tab";
        return help;
    }

    /**
     * Sends some requests consecutively.
     * @param indices the indices of requests which are going to be sent.
     */
    public void sendRequests(int[] indices){
        loadRequests();
        if (requests.size() == 0){
            System.out.println("There are no saved requests");
            return;
        }

        for ( int index : indices){
            System.out.println("request number : " + index);
            if ( index > requests.size() ){
                System.out.println("There is no request with index " + index);
            }
            else {
                Request r = requests.get(index - 1);
                r.setSaveResp(false);
                r.setSaveReq(false);
                r.connect();
                r.sendRequest();
            }
        }
    }

    /**
     * Returns the requests.
     * @return the requests
     */
    public ArrayList<Request> getRequests() {
        return requests;
    }

    /**
     * Adds a request to the requests or updates it.
     * @param request to be added request.
     */
    public void addToRequests(Request request){
        for ( Request r : requests){
            if ( r.getUrl().equals(request.getUrl()) ){
                r = request;
                return;
            }
        }
        requests.add(request);
    }
}
