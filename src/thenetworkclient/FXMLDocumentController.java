/*
 * Copyright (C) 2014 Frank Steiler <frank@steiler.eu>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package thenetworkclient;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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


public class FXMLDocumentController 
{
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
    
    /**
     * Items presented in the application.
     */
    private static ObservableList items = FXCollections.observableArrayList();
    
    /**
     * The URL of the server.
     */
    private URL url;
    private String url_string = "http://localhost:8080/";
    
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
     * Used to check if any transitions is allready finished.
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
     * Stores the timestamp of the oldest and the newest post.
     */
    long newestPost = 0;
    long oldestPost = 0;
    
    
    /**
     * Initialization of the javaFX application.
     */
    @FXML
    void initialize() 
    {
        assert background != null : "fx:id=\"background\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert email != null : "fx:id=\"email\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert label_top != null : "fx:id=\"label_top\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert list != null : "fx:id=\"list\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert listViewAnchor != null : "fx:id=\"listViewAnchor\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert password != null : "fx:id=\"password\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert message != null : "fx:id=\"message\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert loginBox != null : "fx:id=\"loginBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
       
        try 
        {
            url = new URL(url_string);
        } 
        catch (MalformedURLException e) 
        {
            System.err.println("Bad URL");
        }
        
        initBackgroundTask();
        
        initFading();
        
        list.setItems(items);
    }
    
    /**
     * This function initializes the background task of checking if there are any new posts.
     */
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
     * This function initiates the fading actions and handlers.
     */
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
     * This function handles the press of the login button. If the provided credentials are correct the function displays the latest posts.
     * @param event 
     */
    @FXML
    void handleLoginAction(ActionEvent event) 
    {
        fadeOutLoginBox.play();
        user = email.getText();
        userPassword = password.getText();
        password.setText("");
        
        String[] args = {"userLogin=" + user, "&passwordLogin=" + userPassword};
        servletConnection(args);
    }
    
    /**
     * This function loads all newer posts.
     * @param event 
     */
    @FXML
    void loadNewerPostsButton(ActionEvent event) 
    {
        String[] args = {"userLogin=" + user, "&passwordLogin=" + userPassword, "&newer=" + newestPost};
        servletConnection(args);
    }
    
    /**
     * This function loads a given amount of older posts. 
     * @param event 
     */
    @FXML
    void loadOlderPostsButton(ActionEvent event) 
    {
        String[] args = {"userLogin=" + user, "&passwordLogin=" + userPassword, "&older=" + oldestPost, "&amount=" + 10};
        servletConnection(args);
    }
    
    /**
     * Connects to the servlet and doing a post action using the arguments
     * @param args Array of arguments.
     * @return True if successful, false otherwise.
     */
    boolean servletConnection(String[] args)
    {
        boolean success = true;
        
        try 
        {
            //prepare the XML parser
            SAXParser parser = prepareXMLparser();

            // open a connection of post requests
            URLConnection con = url.openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);

            //let the controller know what type of client is connecting
            con.setRequestProperty("user-agent", "xml-app_TheNetwork");
            try (DataOutputStream out = new DataOutputStream(con.getOutputStream())) 
            {
                for(String str : args)
                {
                    out.writeBytes(str);
                }
                out.flush();
            }

            //get ready to read the servlet's response
            InputStream in = con.getInputStream();
            
            //parse the servlet's XML response
            parser.parse(in, new XMLParser());

            in.close();
        } 
        catch (Exception e) 
        {
            success = false;
        }
        return success;
    }
    
    /**
     * This function performas the logout of the user.
     * @param event 
     */
    @FXML
    void performLogout(ActionEvent event) 
    {
        items.clear();
        user = null;
        userPassword = null;
        fadeOutLogoutBox.play();
        ready = false;
        loggedIn = false;
    }
    
    /**
     * This function executes the movement of the window. Somehow window jumped if both values are even.
     * @param event The mouse event while dragging.
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
     * @param event The mouse event while pressing.
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
     * This function handles the press a button. If this button is the escape button the application exits.
     */
    @FXML
    void pressEscape(KeyEvent event) 
    {
        if(event.getCode()==KeyCode.ESCAPE)
        {
            performExit();
        }
    }
    
    /**
     * This functions handles the press of the exit button. If the button is pressed the application exits.
     * @param event 
     */
    @FXML
    void pressExitButton(ActionEvent event) 
    {
        performExit();
    }
    
    /**
     * This function perfoms the exit of the program.
     */
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
    
    /**
     * This function prepares a XML parser.
     * @return A SAXParser.
     * @throws ParserConfigurationException
     * @throws SAXException 
     */
    private SAXParser prepareXMLparser() throws ParserConfigurationException, SAXException
    {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        return factory.newSAXParser();
    }
    
    /**
     * That is the XML Parser parcing the response of the server.
     */
    class XMLParser extends DefaultHandler
    {
        private boolean errorMessage = false, postID = false, publisherName = false, content =false, postTimeStamp = false;
        XMLPost currentPost;
        
        /**
         * Counter for newer posts.
         */
        int counterForNewer = 0;

        /**
         * Resets counter at the beginning.
         */
        @Override
        public void startDocument()
        {
            counterForNewer = 0;
        }

        /**
         * This funtion is called at the beginning of an element.
         * @param uri
         * @param localName
         * @param qName
         * @param attributes
         * @throws SAXException 
         */
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
    }
}