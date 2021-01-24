import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Class Request
 * Includes a request.
 * @author Armin Rezaee
 * @version 1.0
 */
public class Request extends SwingWorker<Void , Void> implements Serializable {

    private static final long serialVersionUID = 6529685098267757690L;
    private String method = "get";
    private String url = null;
    private HashMap<String , String> headers = null;
    private HashMap<String , String> body = null;
    private transient HttpURLConnection connection = null;
    private boolean saveReq = false;
    private boolean saveResp = false;
    private boolean showResponseHeaders = false;
    private String nameOfResponseFile = null;
    private String response = null;
    private ArrayList<String> responseHeaders = null;
    private String nameOfReq = null;
    private transient Controller controller;

    /**
     * Constructs an object.
     * @param url the url of the website
     * @param method the method of the request (default = GET)
     * @param headers the headers of the request
     * @param body the body of the request.
     */
    public Request( String url, String method , HashMap<String ,String> headers , HashMap<String , String>body ) {
        this.method = method==null ? null : method.toLowerCase();
        this.headers = headers;
        this.body = body;
        this.url = url;
        try {
            connection = (HttpURLConnection) (new URL(url)).openConnection();
            connection.setInstanceFollowRedirects(false);
            for ( String key : headers.keySet() ){
                connection.setRequestProperty(key , headers.get(key));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Sends a request and shows its response.
     */
    public void sendRequest(){
        if ( saveReq ){
            Manager.saveRequest(this , null);
        }
        try {
            if ( method == null){
                getHttp();
            }else {
                if (method.equals("get")) {
                    getHttp();
                }
                if (method.equals("post")) {
                    post();
                }
                if (method.equals("put")) {
                    put();
                }
                if (method.equals("delete")) {
                    delete();
                }
            }
        }
        catch (FileNotFoundException e){
            //do nothing
        }
        catch (IOException e){
            if ( e instanceof UnknownHostException){
                System.out.println("Could not resolve host : " + url );
            }
//            e.printStackTrace();
        }

        if ( showResponseHeaders ){
            showResponseHeaders();
        }
        if ( saveResp ){
            saveResponse();
        }

    }

    /**
     * Get method. It gets the response and prints it. if it's an image , saves it in folder pic in the root.
     * @throws IOException Exceptions are handled in sendRequest method.
     */
    public void getHttp() throws IOException {

        InputStream inputStream = connection.getInputStream();
        Scanner scanner = new Scanner(inputStream);
        if ( hasImage() ){
            try ( FileOutputStream outputStream = new FileOutputStream("./pic/" + url.substring(7).replace("/","_") + ".png")){
                byte[] buffer = new byte[4096];
                while (inputStream.available() > 0) {
                    inputStream.read(buffer);
                    outputStream.write(buffer);
                }
                System.out.println("****** The image is saved to folder pic in the root");
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        else
            while(scanner.hasNextLine())
                response += scanner.nextLine();
        System.out.println(response);

    }

    /**
     * Posts something and shows its response.
     * @throws IOException Exceptions are handled in sendRequest method.
     */
    public void post() throws IOException {
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        String boundary = System.currentTimeMillis() + "--";
        if ( !headers.keySet().contains("Content-Type")) {
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            formData(boundary);
        }
        else
            urlEncoded();
    }

    /**
     * Deletes something and shows its response.
     * @throws IOException Exceptions are handled in sendRequest method.
     */
    public void delete() throws IOException {
        connection.setRequestMethod("DELETE");
        InputStream inputStream = connection.getInputStream();
        Scanner s = new Scanner(inputStream);
        while (s.hasNextLine()){
            response += s.nextLine();
        }
        System.out.println(response);
    }

    /**
     * Puts something and shows its response.
     * @throws IOException Exceptions are handled in sendRequest method.
     */
    public void put() throws IOException {
        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);
        if ( !headers.keySet().contains("Content-Type")) {
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + "--" + System.currentTimeMillis());
            formData("--" + System.currentTimeMillis());
        }
        else
            urlEncoded();
    }

    /**
     * This method sends a request with content type url encoded and shows its
     * response from the server.
     */
    public void urlEncoded() throws IOException{
        BufferedOutputStream request = new BufferedOutputStream(connection.getOutputStream());

        boolean first = true;
        for ( String key : body.keySet() ) {
            if ( !first ){
                request.write("&".getBytes());
            }
            request.write((URLEncoder.encode(key,"UTF-8")+"="+URLEncoder.encode(body.get(key) , "UTF-8" )).getBytes());
            first = false;
        }
        request.flush();
        request.close();

        BufferedInputStream bufferedInputStream = new BufferedInputStream(connection.getInputStream());
        Scanner scanner = new Scanner(bufferedInputStream);
        response = "";
        while (scanner.hasNextLine())
            response += scanner.nextLine();

        System.out.println(response);
    }

    /**
     * Shows header fields of the response.
     */
    public void showResponseHeaders(){
        responseHeaders = new ArrayList<>();
        String header;
        int counter = 0;
        Map<String , List<String>> headerFields = connection.getHeaderFields();
        for ( String key : headerFields.keySet()){
            header = "";
            if ( key != null ) {
                header += key + ":";
                System.out.print(key + ": ");
            }
            for (String value : headerFields.get(key) ){
                header += value + " ";
                System.out.print(value + " ");
            }
            System.out.println();
            responseHeaders.add(header);
        }
        System.out.println();
    }

    /**
     * Makes the formdata request body and sends it.
     * @param body body of the request
     * @param boundary boundary of the request body
     * @param bufferedOutputStream output stream of the connection.
     * @throws IOException Exceptions are handled in sendRequest method.
     */
    public void bufferOutFormData(HashMap<String, String> body, String boundary, BufferedOutputStream bufferedOutputStream) throws IOException {

            for (String key : body.keySet()) {
                bufferedOutputStream.write(("--" + boundary + "\r\n").getBytes());
            /*if (key.contains("file")) {
                bufferedOutputStream.write(("Content-Disposition: form-data; filename=\"" + (new File(body.get(key))).getName() + "\"\r\nContent-Type: Auto\r\n\r\n").getBytes());
                try {
                    BufferedInputStream tempBufferedInputStream = new BufferedInputStream(new FileInputStream(new File(body.get(key))));
//                    byte[] filesBytes = tempBufferedInputStream.readAllBytes();
                    byte[] filesBytes = new byte[4096];
                    int i = 0 ;
                    while ( tempBufferedInputStream.available() > 0 ){
                        i = tempBufferedInputStream.read(filesBytes);
                        bufferedOutputStream.write(filesBytes);
                    }
                    bufferedOutputStream.write("\r\n".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else*/
                //{
                    bufferedOutputStream.write(("Content-Disposition: form-data; name=\"" + key + "\"\r\n\r\n").getBytes());
                    bufferedOutputStream.write((body.get(key) + "\r\n").getBytes());
                //}
            }
            bufferedOutputStream.write(("--" + boundary + "--\r\n").getBytes());
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
    }

    /**
     * Sends a request with formdata body and shows its response.
     * @param boundary boundary of the formdata body.
     * @throws IOException Exceptions are handled in sendRequest method.
     */
    public void formData(String boundary) throws IOException {

//            URL url = new URL("http://apapi.haditabatabaei.ir/tests/post/formdata");
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        BufferedOutputStream request = new BufferedOutputStream(connection.getOutputStream());
        bufferOutFormData(body , boundary, request);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(connection.getInputStream());
        Scanner scanner = new Scanner(bufferedInputStream);
        response = "";
        while (scanner.hasNextLine())
            response += scanner.nextLine();
//            System.out.println(new String(bufferedInputStream.readAllBytes()));

        System.out.println(response);

    }

    /**
     * Checks if there is an image in the response.
     * @return true of there is an image , false if not.
     */
    public boolean hasImage(){
        Map<String , List<String>> headerFields = connection.getHeaderFields();
        for ( String key : headerFields.keySet()){
            if ( key != null ) {
                if ( key.equals("Content-Type") ){
                    for ( String value : headerFields.get(key) ){
                        if ( value.contains("image"))
                            return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Sets saveReq to save the request or not.
     * @param saveReq if true the request will be saved , if not , the request won't be saved.
     */
    public void setSaveReq(boolean saveReq) {
        this.saveReq = saveReq;
    }

    /**
     * Sets saveResp to save the request or not.
     * @param saveReq if true the response will be saved , if not , the response won't be saved.
     */
    public void setSaveResp(boolean saveResp) {
        this.saveResp = saveResp;
    }

    /**
     * Sets showResponseHeaders to show headers of the response or not.
     * @param showResponseHeaders if true the headers will be shown , if not they are not.
     */
    public void setShowResponseHeaders(boolean showResponseHeaders) {
        this.showResponseHeaders = showResponseHeaders;
    }

    /**
     * Returns the url.
     * @return the url
     */
    public String getUrl(){
        return url;
    }

    /**
     * Returns the method.
     * @return the method
     */
    public String getMethod() {
        return method;
    }

    /**
     * Returns the headers of the request.
     * @return the headers of the request.
     */
    public HashMap<String, String> getHeaders() {
        return headers;
    }

    /**
     * Saves the response in folder responses in the root.
     */
    public void saveResponse(){
        if ( nameOfResponseFile == null)
            nameOfResponseFile = "output_["+ (new Date()).toString().replace(":" , "_") + "].txt" ;
        if ( response != null) {
            try (FileOutputStream outputStream = new FileOutputStream("./responses/" + nameOfResponseFile)) {
                outputStream.write(response.getBytes());
            } catch (IOException e) {
            e.printStackTrace();
            }
        }
        else{
            System.out.println("No Responses from the server");
        }
    }

    /**
     * Sets the name of the response file.
     * @param nameOfResponseFile the name of the response file.
     */
    public void setNameOfResponseFile(String nameOfResponseFile) {
        this.nameOfResponseFile = nameOfResponseFile;
    }

    /**
     * Makes the connection to the server.
     */
    public void connect(){
        try {
            connection = (HttpURLConnection) (new URL(url)).openConnection();
            connection.setInstanceFollowRedirects(false);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Sets the name of the request.
     * @param nameOfReq the name of the request.
     */
    public void setNameOfReq(String nameOfReq) {
        this.nameOfReq = nameOfReq;
    }

    /**
     * Returns the name of the request.
     * @return the name of the request.
     */
    public String getNameOfReq() {
        return nameOfReq;
    }

    /**
     * Returns the body of the request.
     * @return the body of the request.
     */
    public HashMap<String, String> getBody() {
        return body;
    }

    /**
     * Returns the response of the request.
     * @return the response of the request.
     */
    public String getResponse() {
        return response;
    }

    /**
     * Returns the response headers of the request.
     * @return the response headers of the request.
     */
    public ArrayList<String> getResponseHeaders() {
        return responseHeaders;
    }

    /**
     * Sends a request in the background using multithreading.
     * @return the method done is executed after this.
     */
    public Void doInBackground(){
        sendRequest();
        return null;
    }

    /**
     * Sets the response fields after sending the request.
     */
    protected void done(){
        controller.showResponse(response , responseHeaders);
        String data = null;
        String status = null;

        for ( String header : responseHeaders){
            if ( header.contains("Content-Length") ){
                String[] keyValue = header.split(":");
                data = keyValue[1];
            }
            if ( header.contains("HTTP") ){
                status = header;
            }
        }
        controller.setResponseLabels(status , data + " B" );
        controller.updateUI();
    }

    /**
     * Sets the controller of the request.
     * @param controller the to be set controller.
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }
}
