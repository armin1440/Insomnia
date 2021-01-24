import java.util.HashMap;

/**
 * Class Input
 * Gets the input from the console and makes a request out of it
 * or does something such as help , ... .
 * @author Armin Rezaee
 * @version 1.0
 */
public class Input {

    private String[] args;
    private String method = "get";
    private String site;
    private boolean showResponseHeaders = false;
    private boolean saveReq = false;
    private boolean saveResp = false;
    private HashMap<String , String> reqHeaders = null;
    private HashMap<String , String> body = null;
    private Manager manager;
    private String nameOfResponseFile = null;


    /**
     * Constructs an object with the input from the console and a manager.
     * @param args input from the command prompt
     * @param manager the manager of the program.
     */
    public Input(String[] args , Manager manager){
        this.manager = manager;
        this.args = args;
        reqHeaders = new HashMap<>();
        body = new HashMap<>();
    }

    /**
     * Makes a request out of the commands or does something such as help , etc.
     * @return a request if made one , else null.
     */
    public Request makeRequest(){
        reqHeaders = new HashMap<>();
        body = new HashMap<>();

        if ( args[0].equals("list") ){
            manager.showRequests();
            return null;
        }
        else if ( args[0].equals("--help") | args[0].equals("-h") ){
            manager.help();
            return null;
        }
        else if ( args[0].equals("fire") ){
            int[] numbers = new int[args.length-1];
            for ( int i = 1 ; i < args.length ; i++ ){
                numbers[i-1] = Integer.parseInt(args[i]);
            }
            manager.sendRequests(numbers);
            return null;
        }
        else {
            site = args[0];
            method = findMethod();
            fillReqHeaders();
            fillBody();
            saveReq = saveRequest();
            saveResp = saveResponse();
            showResponseHeaders = showHeadersInResponse();
            if (!site.contains("http")) {
                site = "http://" + site;
            }
            Request req = new Request(site, method, reqHeaders, body);
            if (saveReq) {
                req.setSaveReq(true);
            }
            if (saveResp) {
                req.setSaveResp(true);
                req.setNameOfResponseFile(nameOfResponseFile);
            }
            if (showResponseHeaders) {
                req.setShowResponseHeaders(true);
            }
            return req;
        }
    }

    /**
     * Finds the method in the input.
     * @return the method , if didn't found returns null.
     */
    public String findMethod(){
        boolean foundMethod = false;

        for ( String input : args){
            if ( foundMethod ){
                if ( !(input.toLowerCase().equals("get") | input.toLowerCase().equals("put") | input.toLowerCase().equals("post") | input.toLowerCase().equals("delete") ) ){
                    System.out.println("Unsupported or wrong method.The default method is GET.\n");
                    return null;
                }
                return input;
            }
            if ( input.equals("--method") | input.equals("-M")){
                foundMethod = true;
            }
        }
        return "get";
    }

    /**
     * Finds the headers.
     */
    public void fillReqHeaders(){
        boolean foundHeader = false;
        String headers = null;
        String[] keyValue , keyValuePairs;

        for ( String input : args){
            if ( foundHeader ){
                headers = input;
                break;
            }
            if (input.equals("-H") | input.equals("--headers") ){
                foundHeader = true;
            }
        }

        if ( headers != null) {
//            headers = headers.replace(" ", "");
            headers = headers.replace("\"", "");

            keyValuePairs = headers.split(";");

            boolean warned = false;
            for (String header : keyValuePairs) {
                keyValue = header.split(":");
                if ( keyValue.length != 2 & !warned){
                    System.out.println("some headers are not in the correct format.The right ones are considered.\n");
                    warned = true;
                }
                else
                    reqHeaders.put(keyValue[0], keyValue[1]);
            }
        }

    }

    /**
     * Finds the message body.
     */
    public void fillBody(){
        boolean foundBody = false;
        String bodies = null;
        String[] keyValuePairs , keyValue;

        for ( String input : args){
            if ( foundBody ){
                bodies = input;
                break;
            }
            if ( input.equals("-d") | input.equals("--data") ){
                foundBody = true;
            }
        }

        if ( bodies != null) {
//            bodies = bodies.replace(" ", "");
            bodies = bodies.replace("\"", "");

            keyValuePairs = bodies.split("&");

            boolean warned = false;
            for ( String body : keyValuePairs){
                keyValue = body.split("=");
                if (keyValue.length != 2  &  !warned){
                    System.out.println("some bodies are not in the correct format.The right ones are considered.\n");
                    warned = true;
                }
                else
                    this.body.put(keyValue[0], keyValue[1]);
            }
        }
    }

    /**
     * Finds the save command.
     * @return true if found save command , else false.
     */
    public boolean saveRequest(){
        for ( String input : args){
            if ( input.equals("-S") | input.equals("--save") )
                return true;
        }
        return false;
    }

    /**
     * Finds the "save response" command.
     * @return true if found , false if not.
     */
    public boolean saveResponse(){
        boolean found = false;
        for ( String input : args){
            if ( found ){
                if ( input.contains("/") | input.contains("-") )
                    nameOfResponseFile = null;
                else
                    nameOfResponseFile = input;
                return true;
            }
            if ( input.equals("-O") | input.equals("--output") ){
                found = true;
            }
        }
        if ( found )
            return true;
        return false;
    }

    /**
     * Finds the "-i" command in the input.
     * @return true if found , false if not.
     */
    public boolean showHeadersInResponse(){
        for ( String input : args){
            if ( input.equals("-i") ){
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the args.
     * @param args
     */
    public void setArgs(String[] args) {
        this.args = args;
    }
}
