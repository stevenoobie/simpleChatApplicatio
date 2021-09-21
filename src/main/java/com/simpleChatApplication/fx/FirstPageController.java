package com.simpleChatApplication.fx;


import com.simpleChatApplication.model.Message;
import com.simpleChatApplication.model.User;
import com.simpleChatApplication.service.ConversationService;
import com.simpleChatApplication.service.UserService;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FirstPageController {

    @FXML
    Label pathLabel;
    @FXML
    ListView listView;

    @Autowired
    UserService userService;
    @Autowired
    ConversationService conversationService;

    private WebSocketStompClient stompClient1, stompClient2;
    private StompSession.Subscription subscription1, subscription2;
    private List<User> usersList;

    private Stage stage;
    public static ConfigurableApplicationContext applicationContext;
    private User user;

    private String jwt;

    public void setUser(User user) {
        this.user = user;

        getAllUsers();
        initializeSubscription();

    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    private void initializeSubscription() {
        System.out.println("Initializing subscription");
        stompClient1 = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient1.setMessageConverter(new MappingJackson2MessageConverter());
        String url1 = "ws://localhost:8080/chatapp";

        WebSocketHttpHeaders httpHeaders = new WebSocketHttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + jwt);

        stompClient1.connect(url1, httpHeaders, new StompSessionHandlerAdapter() {
            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                System.out.println(exception.toString());
                System.out.println(session.getSessionId());

            }

            @Override
            public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                System.out.println("Exception: " + exception.toString());
                System.out.println("Header: " + headers);
            }

            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                subscription1 = session.subscribe("/topic/users", new StompFrameHandler() {

                    @Override
                    public Type getPayloadType(StompHeaders stompHeaders) {
                        System.out.println("Header: " + stompHeaders);
                        return User.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders stompHeaders, Object o) {
                        System.out.println("Header: " + stompHeaders);
                        User tempUser = (User) o;
                        usersList.add(tempUser);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                String userString = tempUser.toString();
                                listView.getItems().add(userString);
                                listView.scrollTo(userString);
                            }
                        });
                    }
                });
            }
        });

        String url2="ws://localhost:8081/chatapp";
        stompClient2=new WebSocketStompClient(new StandardWebSocketClient());
        stompClient2.setMessageConverter(new MappingJackson2MessageConverter());
        stompClient2.connect(url2,httpHeaders, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                subscription2=session.subscribe("/topic/users", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders stompHeaders) {
                        return User.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders stompHeaders, Object o) {
                        User tempUser=(User)o;
                        usersList.add(tempUser);
                        Platform.runLater(new Runnable(){
                            @Override
                            public void run() {
                                String userString=tempUser.toString();
                                listView.getItems().add(userString);
                                listView.scrollTo(userString);
                            }
                        });
                    }
                });
            }
        });


    }

    private void getAllUsers() {


        usersList = userService.findAll();
        int userIndex = 0;

        for (User user : usersList) {

            if (this.user.getId() != user.getId()) {
                listView.getItems().add(user.toString());
            } else {
                userIndex = listView.getItems().size();
            }
        }
        usersList.remove(userIndex);

        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {

                User selectedUser = usersList.get(listView.getSelectionModel().getSelectedIndex());
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/chat-page.fxml"));
                loader.setControllerFactory(applicationContext::getBean);
                Parent root = null;
                try {
                    root = loader.load();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
                ChatPageController chatController = (ChatPageController) loader.getController();
                chatController.setJwt(jwt);
                chatController.setSender(user);
                chatController.setReceiver(selectedUser);
                Scene scene = new Scene(root);
                stage = (Stage) pathLabel.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
                stopSubscription();

            }
        });

    }
    public void createReport() throws FileNotFoundException, JRException {
        Map<String,Object>parameters=new HashMap<>();
        Long userCount=userService.getUsersCount();
        Long conversationCount=conversationService.getConversationsCount();
        System.out.println(userCount);
        System.out.println(conversationCount);
        List<User>users=new ArrayList<>();
        users.add(user);
        parameters.put("users",userCount);
        parameters.put("conversations",conversationCount);
        String path=System.getProperty("user.dir")+"\\src\\main\\resources\\reports";
        File file= ResourceUtils.getFile("classpath:jrxml\\dashboard.jrxml");
        JasperReport jasperReport= JasperCompileManager.compileReport(file.getAbsolutePath());
        JRBeanCollectionDataSource dataSource=new JRBeanCollectionDataSource(users);
        JasperPrint jasperPrint= JasperFillManager.fillReport(jasperReport,parameters,dataSource);
        JasperExportManager.exportReportToPdfFile(jasperPrint,path+"\\dashboard.pdf");
        pathLabel.setText("Report created!");

    }
    public void logout() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/login.fxml"));
        loader.setControllerFactory(applicationContext::getBean);
        LoginController loginController = (LoginController) loader.getController();
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage = (Stage) pathLabel.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
        stopSubscription();
    }

    private void stopSubscription() {
        subscription1.unsubscribe();
        subscription2.unsubscribe();
        stompClient1.stop();
        stompClient2.stop();
    }
}
