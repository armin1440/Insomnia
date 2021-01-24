import com.sun.org.apache.regexp.internal.RE;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class Controller
 * Does operations in this program.
 *
 * @author Armin Rezaee
 * @version 1.0
 */
public class Controller {

    private Model model;
    private InsomniaUi insomniaUi;
    private Input input;
    private Manager manager;

    /**
     * Constructs an object with given things.
     *
     * @param model      the model which has the data of the program.
     * @param insomniaUi the ui of the program.
     */
    public Controller(Model model, InsomniaUi insomniaUi, Manager manager) {
        this.model = model;
        this.insomniaUi = insomniaUi;
        String[] initInput = "-h http://apapi.haditabatabaei.ir/tests/get/html".split(" ");
        input = new Input(initInput, manager);
        this.manager = manager;
        loadRequests();
    }

    /**
     * Hides the ui in the system tray after exiting.
     */
    public void hide() {
        insomniaUi.hide();
    }

    /**
     * Adds a request to the ui requests part.
     *
     * @param req the title of the request.
     */
    public void addRequest(String req) {
        insomniaUi.addRequest(req);
    }

    /**
     * Saves setting in a file.
     */
    public void save() {
        model.save();
    }

    /**
     * Sets the hide status.
     *
     * @param hide the hide status.
     */
    public void setHide(boolean hide) {
        model.setHide(hide);
        System.out.println("Hide is set to " + hide);
    }

    /**
     * Sets the follow redirect status.
     *
     * @param redirect the follow redirect status.
     */
    public void setRedirect(boolean redirect) {
        model.setRedirect(redirect);
        System.out.println("Redirect is set to " + redirect);
    }

    /**
     * Makes a two digit number and returns it to show the status of hide and follow redirect.
     *
     * @return two digit number to show status.
     */
    public int checkValuesOfOptions() {
        int output = 0;

        if (model.isHide()) {
            output += 20;
        } else {
            output += 10;
        }

        if (model.isRedirect()) {
            output += 2;
        } else {
            output += 1;
        }

        return output;
    }

    /**
     * Returns the hide status
     *
     * @return true if hide is chosen , false if not.
     */
    public boolean isHide() {
        return model.isHide();
    }

    /**
     * Returns the hide status
     *
     * @return true if hide is chosen , false if not.
     */
    public boolean isRedirect() {
        return model.isRedirect();
    }

    /**
     * Sets the values of width and height in model.
     *
     * @param width  to be set width
     * @param height to be set height
     */
    public void setSize(int width, int height) {
        model.setHeight(height);
        model.setWidth(width);
    }

    /**
     * Checks if the frame is fullscreen
     *
     * @return true if fullscreen false if not.
     */
    public boolean isFullScreen() {
        return model.getHeight() == 1048 && model.getWidth() == 1938;
    }

    /**
     * Makes the ui fullscreen.
     *
     * @param full true means fullscreen , false means regular size.
     */
    public void setFullScreen(boolean full) {
        System.out.println("FullScreen " + full);
        insomniaUi.setFullScreen(full);
        if (full) {
            model.setWidth(1938);
            model.setHeight(1048);
        }
    }

    /**
     * Makes a request from the input in the UI.
     * @return a request from the input in the UI.
     */
    public Request makeRequest() {
        String[] command = new String[8];
        SetRequestInterface center = insomniaUi.getCenterPart();

        String method = center.getMethodSelector().getSelectedItem().toString();
        String url = center.getUrl().getText();
        String body = "", header = "";

        BodyTab bodyTab = (BodyTab) center.getBodyTab();
        for (BodyTab.KeyAndValue key : bodyTab.getFields()) {
            if (!(key.getKey().getText().contains("New Name") | key.getValue().getText().contains("New Value")) & key.getCheckBox().isSelected()) {
                body += key.getKey().getText().replace(" ", "") + "=" + key.getValue().getText().replace(" ", "") + "&";
            }
        }

        BodyTab headerTab = (BodyTab) center.getHeaderTab();
        for (BodyTab.KeyAndValue field : headerTab.getFields()) {
            if (!(field.getKey().getText().contains("header") | field.getValue().getText().contains("value")) & field.getCheckBox().isSelected()) {
                header += field.getKey().getText().replace(" ", "") + ":" + field.getValue().getText().replace(" ", "") + ";";
            }
        }

        command[0] = url;
        command[1] = "-M";
        command[2] = method;
        command[3] = "-i";
        command[4] = "-d";
        command[5] = body;
        command[6] = "-H";
        command[7] = header;

        input.setArgs(command);
        Request request = input.makeRequest();
        setControllerOfRequest(request);
        manager.addToRequests(request);
        updateUI();
        return request;
    }

    /**
     * Saves a request with the given name.
     * @param name the name of the request
     */
    public void saveRequest(String name) {
        Request request = makeRequest();
        request.setNameOfReq(name);
        Manager.saveRequest(request, name);
        loadRequests();
    }

    /**
     * Loads all of saved requests and shows them in the UI.
     */
    public void loadRequests() {
        manager.loadRequests();
        ArrayList<Request> requests = manager.getRequests();
        insomniaUi.setRequests(requests);
        insomniaUi.getLeftPart().getRequestPart().updateUI();
    }

    /**
     * Loads a single request and shows it in the UI.
     * @param requestName name of the request.
     */
    public void loadRequest(String requestName) {
        Request request = null;
        boolean found = false;
        for (Request r : manager.getRequests()) {
            if (r.getNameOfReq().equals(requestName)) {
                request = r;
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("Request was not found!");
            return;
        }

        insomniaUi.getCenterPart().getUrl().setText(request.getUrl());
        insomniaUi.getCenterPart().getMethodSelector().setSelectedItem(request.getMethod().toUpperCase());

        insomniaUi.getCenterPart().getBodyTab().removeAll();
        for (String key : request.getBody().keySet()) {
            insomniaUi.getCenterPart().getBodyTab().addField(key, request.getBody().get(key));
        }
        insomniaUi.getCenterPart().getBodyTab().addField();

        insomniaUi.getCenterPart().getHeaderTab().removeAll();
        for (String key : request.getHeaders().keySet()) {
            insomniaUi.getCenterPart().getHeaderTab().addField(key, request.getHeaders().get(key));
        }
        insomniaUi.getCenterPart().getHeaderTab().addField();

        if (request.getResponse() != null) {
            if (!request.getResponse().isEmpty()) {
                insomniaUi.getRightPart().setResponse(request.getResponse());
            }
        }

        if (request.getResponseHeaders() != null) {
            if (!request.getResponseHeaders().isEmpty()) {
                insomniaUi.getRightPart().removeResponseHeaders();
                String data = null;
                String status = null;
                for (String key : request.getResponseHeaders()) {
                    String[] keyValue = key.split(":");
                    if ( key.contains("HTTP") ){
                        status = key;
                    }
                    if ( key.contains("Content-Length") ){
                        data = key.split(":")[1];
                    }
                    if (keyValue.length == 2)
                        insomniaUi.getRightPart().addResponseHeader(keyValue[0], keyValue[1]);
                }
                setResponseLabels(status , data);
            }
        }

    }

    /**
     * Shows response of a request in the response interface.
     */
    public void showResponse() {
        String url = insomniaUi.getCenterPart().getUrl().getText();
        Request request = null;
        for (Request r : manager.getRequests()) {
            if (r.getUrl().equals(url)) {
                request = r;
                break;
            }
        }
        if (request != null) {
            if (request.getResponse() != null)
                insomniaUi.getRightPart().setResponse(request.getResponse());
            if (request.getResponseHeaders() != null) {
                if (!request.getResponseHeaders().isEmpty()) {
                    insomniaUi.getRightPart().removeResponseHeaders();
                    for (String key : request.getResponseHeaders()) {
                        String[] keyValue = key.split(":");
                        if (keyValue.length == 2)
                            insomniaUi.getRightPart().addResponseHeader(keyValue[0], keyValue[1]);
                    }
                }
            }
        }

    }

    /**
     * Returns the url in the url bar in the set request interface.
     * @return the url in the url bar in the set request interface.
     */
    public String getUrl() {
        return insomniaUi.getCenterPart().getUrl().getText();
    }

    /**
     * Shows a response with the response and response headers given.
     * @param response the response of the request.
     * @param responseHeaders the response headers of the request
     */
    public void showResponse(String response, ArrayList<String> responseHeaders) {
        if (response != null) {
            int lineLength = insomniaUi.getRightPart().getWidth()/7;
            String responseInLines = "";
            for ( int i = 0 ; i <= response.length()/lineLength ; i++ ){
                if ( (i+1)*lineLength > response.length() ) {
                    responseInLines += response.substring(i * lineLength, response.length() );
                    break;
                }else
                    responseInLines += response.substring(i*lineLength , (i+1)*lineLength) + "\n";
            }
            insomniaUi.getRightPart().setResponse(responseInLines);
        }
        if (responseHeaders != null) {
            insomniaUi.getRightPart().removeResponseHeaders();
            for (String resp : responseHeaders) {
                String[] keyValue = resp.split(":");
                if (keyValue.length == 2)
                    insomniaUi.getRightPart().addResponseHeader(keyValue[0], keyValue[1]);
            }
        }
    }

    /**
     * Sets controller field of the request.
     * @param request
     */
    public void setControllerOfRequest(Request request) {
        request.setController(this);
    }

    /**
     * Sets the status and data labels in the response interface.
     * @param responseCode the status
     * @param data the data downloaded
     */
    public void setResponseLabels(String responseCode , String data){
        String[] responseCodeParts = responseCode.split(" ");
        insomniaUi.getRightPart().getData().setText(data);

        String status = "";
        for ( String response : responseCodeParts){
            if ( !response.contains("HTTP"))
                status += response + " ";
        }
        insomniaUi.getRightPart().getStatus().setText(status);
        insomniaUi.getRightPart().getStatus().updateUI();
        insomniaUi.getRightPart().getData().updateUI();
    }

    /**
     * updates every component in the UI.
     */
    public void updateUI(){
        insomniaUi.getLeftPart().updateUI();
        insomniaUi.getLeftPart().getRequestPart().updateUI();

        insomniaUi.getCenterPart().updateUI();
        insomniaUi.getCenterPart().getUrl().updateUI();
        insomniaUi.getCenterPart().getMethodSelector().updateUI();
        insomniaUi.getCenterPart().getBodyTab().updateUI();
        insomniaUi.getCenterPart().getHeaderTab().updateUI();

        insomniaUi.getRightPart().updateUI();
        insomniaUi.getRightPart().getData().updateUI();
        insomniaUi.getRightPart().getStatus().updateUI();
        insomniaUi.getRightPart().getBody().updateUI();
        insomniaUi.getRightPart().getHeader().updateUI();
    }

    /**
     * Clears the response interface.
     */
    public void clearResponse(){
        insomniaUi.getRightPart().setResponse("");
        insomniaUi.getRightPart().removeResponseHeaders();
        updateUI();
    }
}
