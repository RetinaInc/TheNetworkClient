package thenetworkclient;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;


public class FXMLDocumentController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane background;

    @FXML
    private TextField email;

    @FXML
    private Label label_top;

    @FXML
    private ListView<?> list;

    @FXML
    private AnchorPane listViewAnchor;

    @FXML
    private PasswordField password;
    
    @FXML
    private Label message;
    
    @FXML
    private HBox loginBox;
    
    @FXML
    private HBox logoutBox;
    
    @FXML
    private Button loadNewerPostsButton;    
    
    @FXML
    private Button loadOlderPostsButton;  
    
    private static ObservableList items = FXCollections.observableArrayList();
    
    private static final int ROW_HEIGHT = 24;
    
    /**
     * Handling of fade ins & outs of top bar.
     */
    private FadeTransition fadeOutLoginBox;
    private FadeTransition fadeInLoginBox;
    private FadeTransition fadeOutLogoutBox;
    private FadeTransition fadeInLogoutBox;
    private FadeTransition messageFadeIn;
    private FadeTransition messageFadeOut;
    
    /**
     * Used to check if the transitions are allready finished.
     */
    private boolean ready = false;

    /**
     * Variables needed to move the frame.
     */
    private Stage stage;
    private double xOffset;
    private double yOffset;
    
    /**
     * Application needs to save password and user to authenticate for each command.
     */
    private String user;
    private String userPassword;
    boolean loggedIn = false;
    
    /**
     * Stores the oldest and the newest post.
     */
    long newestPost = 0;
    long oldestPost = 0;

    @FXML
    void performLogout(ActionEvent event) {
        items.clear();
        user = null;
        userPassword = null;
        fadeOutLogoutBox.play();
        ready = false;
        loggedIn = false;
    }
    
    public void initFading()
    {
        double duration = 1000;
        fadeOutLoginBox = new FadeTransition(new Duration(duration));
        fadeOutLoginBox.setNode(loginBox);
        fadeOutLoginBox.setFromValue(1.0);
        fadeOutLoginBox.setToValue(0.0);
        fadeOutLoginBox.setCycleCount(1);
        fadeOutLoginBox.setAutoReverse(false);
        
        fadeInLoginBox = new FadeTransition(new Duration(duration));
        fadeInLoginBox.setNode(loginBox);
        fadeInLoginBox.setFromValue(0.0);
        fadeInLoginBox.setToValue(1.0);
        fadeInLoginBox.setCycleCount(1);
        fadeInLoginBox.setAutoReverse(false);
        
        fadeOutLogoutBox = new FadeTransition(new Duration(duration));
        fadeOutLogoutBox.setNode(logoutBox);
        fadeOutLogoutBox.setFromValue(1.0);
        fadeOutLogoutBox.setToValue(0.0);
        fadeOutLogoutBox.setCycleCount(1);
        fadeOutLogoutBox.setAutoReverse(false);
        
        fadeInLogoutBox = new FadeTransition(new Duration(duration));
        fadeInLogoutBox.setNode(logoutBox);
        fadeInLogoutBox.setFromValue(0.0);
        fadeInLogoutBox.setToValue(1.0);
        fadeInLogoutBox.setCycleCount(1);
        fadeInLogoutBox.setAutoReverse(false);
        
        messageFadeIn = new FadeTransition(new Duration(3000));
        messageFadeIn.setNode(message);
        messageFadeIn.setFromValue(0.0);
        messageFadeIn.setToValue(1.0);
        messageFadeIn.setCycleCount(1);
        messageFadeIn.setAutoReverse(false);
        
        messageFadeOut = new FadeTransition(new Duration(3000));
        messageFadeOut.setNode(message);
        messageFadeOut.setFromValue(1.0);
        messageFadeOut.setToValue(0.0);
        messageFadeOut.setCycleCount(1);
        messageFadeOut.setAutoReverse(false);
        
        fadeInLogoutBox.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                logoutBox.setDisable(false);
            }
        });
        
        fadeOutLogoutBox.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                fadeInLoginBox.play();
                logoutBox.setDisable(true);
            }
        });
        
        fadeInLoginBox.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                loginBox.setDisable(false);
            }
        });
        
        fadeOutLoginBox.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(ready)
                {
                    if(loggedIn)
                    {
                        fadeInLogoutBox.play();
                        ready = false;
                    }
                    else
                    {
                        messageFadeIn.play();
                        ready = false;
                    }
                }
                else
                {
                    ready = true;
                }
                loginBox.setDisable(true);
            }
        });
        
        messageFadeIn.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                messageFadeOut.play();
            }
        });
        
        messageFadeOut.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                fadeInLoginBox.play();
            }
        });

    }
    
    /**
     * This function executes the movement of the window. Somehow window jumped if both values are even.
     * @param event 
     */
    @FXML
    public void backgroundMouseDragged(MouseEvent event)
    {
        double xCoord = event.getScreenX()-xOffset;
        double yCoord = event.getScreenY()-yOffset;
        if(xCoord % 2 != 0 || yCoord % 2 != 0)
        {
            stage.setX(xCoord);
            stage.setY(yCoord);
        }
    }

    /**
     * Saves the offset of the mouse and gets the window if it is the first time the window gets moved.
     * @param event 
     */
    @FXML
    void backgroundMousePressed(MouseEvent event) 
    {
        if(stage == null)
        {
            stage = (Stage) background.getScene().getWindow();
        }
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }
    
    
    /**
     * These functions handle the exit of the application.
     */
    
    @FXML
    void pressEscape(KeyEvent event) 
    {
        if(event.getCode()==KeyCode.ESCAPE)
        {
            performExit();
        }
    }
    
    @FXML
    void pressExitButton(ActionEvent event) 
    {
        performExit();
    }
    
    void performExit()
    {
        FadeTransition exit = new FadeTransition(new Duration(1500));
        exit.setNode(background);
        exit.setFromValue(1.0);
        exit.setToValue(0.0);
        exit.setCycleCount(1);
        exit.setAutoReverse(false);
        exit.play();
        exit.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(1);
            }
        });
    }
    
    @FXML
    void handleLoginAction(ActionEvent event) 
    {
        fadeOutLoginBox.play();
        try
        {
            //prepare the XML parser
            SAXParser parser = prepareXMLparser();

            //the controller servlet's URL
            URL url = new URL("http://localhost:8080/");

            // open a connection of post requests
            URLConnection con = url.openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);

            //let the controller know what type of client is connecting
            con.setRequestProperty("user-agent", "xml-app_TheNetwork");

            //send the request parameters as post data
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            user = email.getText();
            userPassword = password.getText();
            password.setText("");
            out.writeBytes("userLogin=" + user);
            out.writeBytes("&passwordLogin=" + userPassword);
            out.flush();
            out.close();

            //get ready to read the servlet's response
            InputStream in = con.getInputStream();
            
            //String result = getStringFromInputStream(in);
 
            //System.out.println(result);
            //parse the servlet's XML response
            parser.parse(in, new XMLParser());

            in.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @FXML
    void loadNewerPostsButton(ActionEvent event) 
    {
        try
        {
            //prepare the XML parser
            SAXParser parser = prepareXMLparser();

            //the controller servlet's URL
            URL url = new URL("http://localhost:8080/");

            // open a connection of post requests
            URLConnection con = url.openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);

            //let the controller know what type of client is connecting
            con.setRequestProperty("user-agent", "xml-app_TheNetwork");

            //send the request parameters as post data
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes("userLogin=" + user);
            out.writeBytes("&passwordLogin=" + userPassword);
            out.writeBytes("&newer=" + newestPost);
            out.flush();
            out.close();

            //get ready to read the servlet's response
            InputStream in = con.getInputStream();
            
            //String result = getStringFromInputStream(in);
 
            //System.out.println(result);
            //parse the servlet's XML response
            parser.parse(in, new XMLParser());

            in.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @FXML
    void loadOlderPostsButton(ActionEvent event) 
    {
        try
        {
            //prepare the XML parser
            SAXParser parser = prepareXMLparser();

            //the controller servlet's URL
            URL url = new URL("http://localhost:8080/");

            // open a connection of post requests
            URLConnection con = url.openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);

            //let the controller know what type of client is connecting
            con.setRequestProperty("user-agent", "xml-app_TheNetwork");

            //send the request parameters as post data
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes("userLogin=" + user);
            out.writeBytes("&passwordLogin=" + userPassword);
            out.writeBytes("&older=" + oldestPost);
            out.writeBytes("&amount=" + 10);
            out.flush();
            out.close();

            //get ready to read the servlet's response
            InputStream in = con.getInputStream();
            
            //String result = getStringFromInputStream(in);
            //System.out.println(result);
            
            //parse the servlet's XML response
            parser.parse(in, new XMLParser());

            in.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private SAXParser prepareXMLparser()
            throws ParserConfigurationException, SAXException
    {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        return factory.newSAXParser();
    }

    @FXML
    void initialize() {
        assert background != null : "fx:id=\"background\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert email != null : "fx:id=\"email\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert label_top != null : "fx:id=\"label_top\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert list != null : "fx:id=\"list\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert listViewAnchor != null : "fx:id=\"listViewAnchor\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert password != null : "fx:id=\"password\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert message != null : "fx:id=\"message\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert loginBox != null : "fx:id=\"loginBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
       
        initBackgroundTask();
        
        initFading();
        
        list.setItems(items);
    }
    
    void initBackgroundTask()
    {
        Timeline backgroundCheckNewPosts = new Timeline(new KeyFrame(Duration.seconds(10), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if(loggedIn)
                {
                    loadNewerPostsButton(event);
                }
            }
        }));
        backgroundCheckNewPosts.setCycleCount(Timeline.INDEFINITE);
        backgroundCheckNewPosts.play();
    }

    
    
    /**
     * That is the XML Parser parcing the response of the server.
     */
    class XMLParser extends DefaultHandler
    {
        private boolean errorMessage = false, postID = false, publisherName = false, content =false, postTimeStamp = false;
        XMLPost currentPost;
        
        int counterForNewer = 0;


        @Override
        public void startDocument() throws SAXException
        {
            counterForNewer = 0;
        }

        @Override
        public void endDocument() throws SAXException
        {
            
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
        {
            if(qName.equalsIgnoreCase("errorMessage"))
            {
                errorMessage = true;
            }
            else if(qName.equalsIgnoreCase("postID"))
            {
                postID = true;
            }
            else if(qName.equalsIgnoreCase("publisherName"))
            {
                publisherName = true;
            }
            else if(qName.equalsIgnoreCase("content"))
            {
                content = true;
            }
            else if(qName.equalsIgnoreCase("postTimeStamp"))
            {
                postTimeStamp = true;
            }
            else if(qName.equalsIgnoreCase("post"))
            {
                if(!loggedIn)
                {
                    loggedIn = true;
                    if(ready)
                    {
                        fadeInLogoutBox.play();
                        ready = false;
                    }
                    else
                    {
                        ready = true;
                    }
                }
                currentPost = new XMLPost();
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException
        {
            errorMessage = false;
            postID = false;
            publisherName = false;
            content = false;
            postTimeStamp = false;
            if(qName.equalsIgnoreCase("post"))
            {
                if(currentPost.getTimestamp().getTime() > oldestPost)
                {
                    items.add(counterForNewer, currentPost.toString());
                    counterForNewer++;
                }
                else
                {
                    items.add(currentPost.toString());
                }
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException
        {

            String s = (new String(ch, start, length)).trim();

            if(errorMessage)
            {
                message.setText(s);
                if(!ready)
                {
                    ready = true;
                    if(loggedIn)
                    {
                        ready = false;
                    }
                }
                else
                {
                    messageFadeIn.play();
                    ready = false;
                }
            }
            else if(postID)
            {
                currentPost.setPostID(Integer.valueOf(s));
            }
            else if(publisherName)
            {
                currentPost.setPublishingUser(s);
            }
            else if(content)
            {
                currentPost.setContent(s);
            }
            else if(postTimeStamp)
            {
                long post = Long.parseLong(s);
                if(newestPost < post || newestPost == 0)
                {
                    newestPost = post;
                }
                if(oldestPost > post || oldestPost == 0)
                {
                    oldestPost = post;
                }
                currentPost.setTimestamp(new Timestamp(post));
            }
        }

        @Override
        public void error(SAXParseException spe) throws SAXException
        {
            //displayTA.append("[XML parse error: " + spe.getMessage() + "]\n");
        }
    }
    
    private static String getStringFromInputStream(InputStream is) 
    {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

                br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                        sb.append(line);
                }

        } catch (IOException e) {
                e.printStackTrace();
        } finally {
                if (br != null) {
                        try {
                                br.close();
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                }
        }

        return sb.toString();

    }
}



