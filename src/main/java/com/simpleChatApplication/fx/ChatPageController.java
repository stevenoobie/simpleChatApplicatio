package com.simpleChatApplication.fx;

import com.simpleChatApplication.model.Message;
import com.simpleChatApplication.model.User;
import com.simpleChatApplication.service.ConversationService;
import com.simpleChatApplication.service.MessageService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
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
import java.util.*;

@Component
public class ChatPageController {
    @FXML
    Text receiverText;
    @FXML
    StackPane topPane;
    @FXML
    StackPane bottomPane;
    @FXML
    Text receiverName;
    @FXML
    TextField messageBodyField;
    @FXML
    Label pathLabel;

    @FXML
    ListView listView;

    @FXML
    public void onEnter(ActionEvent actionEvent) throws Exception {
        sendMessage();
    }

    Label label;
    @Autowired
    MessageService messageService;
    @Autowired
    ConversationService conversationService;

    private Long conversationId;
    private WebSocketStompClient stompClient1;
    private WebSocketStompClient stompClient2;
    private StompSession.Subscription subscription1, subscription2;
    private Message lastMessage;

    private Stage stage;
    public static ConfigurableApplicationContext applicationContext;
    private User sender, receiver;
    private String jwt;

    private int messageCount;//Used for making a report with the number of messages

    public void initialize() {
        topPane.setBackground(new Background(new BackgroundFill(Color.web("#3C3F41"), CornerRadii.EMPTY, Insets.EMPTY)));
        bottomPane.setBackground(new Background(new BackgroundFill(Color.web("#F1F3F4"), CornerRadii.EMPTY, Insets.EMPTY)));


    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
        setChatEnviroment();
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public void sendMessage() throws IOException {
        Message message = new Message(messageBodyField.getText());
        Long messageId = messageService.save(message);

        conversationService.save(conversationId, messageId, sender.getId(), receiver.getId());
        messageService.send(message, conversationId);
        messageBodyField.clear();
    }

    public void setChatEnviroment() {
        receiverText.setText(Character.toString(receiver.getFirstname().toUpperCase().charAt(0))
                + receiver.getLastname().toUpperCase().charAt(0));
        receiverName.setText(receiver.toString());
        getChatMessages();
        initializeSubscription();
        getConversationId();
        lastMessage = new Message();


    }

    public void getChatMessages() {

        Collection<Long> messagesIds = messageService.getChatMessagesIds(sender.getId(), receiver.getId());
        messageCount=messagesIds.size();
        List<Message> messageList = (List<Message>) messageService.getChatMessages(messagesIds);
        Label label;
        for (Message message : messageList) {

            label = new Label(message.getBody());
            listView.getItems().add(label);
            listView.scrollTo(label);
        }
    }

    private void getConversationId() {
        conversationId = conversationService.getConversationId(sender.getId(), receiver.getId());
    }

    private void initializeSubscription() {
        System.out.println("Initializing subscription");
        if (stompClient1 == null) {
            stompClient1 = new WebSocketStompClient(new StandardWebSocketClient());
        }
        stompClient1.setMessageConverter(new MappingJackson2MessageConverter());
        String url1 = "ws://localhost:8080/chatapp";
        String url2 = "ws://localhost:8081/chatapp";
        WebSocketHttpHeaders httpHeaders = new WebSocketHttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + jwt);

        stompClient1.connect(url1,httpHeaders, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                subscription1 = session.subscribe("/topic/messages/" + conversationId, new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders stompHeaders) {
                        return Message.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders stompHeaders, Object o) {
                        Message message = (Message) o;
                        System.out.println("Message 1: " + message.toString());
                        lastMessage = message;
                        Label label = new Label(message.getBody());
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                listView.getItems().add(label);
                                listView.scrollTo(label);
                            }

                        });

                    }

                });
            }

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Message.class;
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                System.out.println(exception.toString());
                super.handleTransportError(session, exception);
            }
        });

        //Second websocket
        if (stompClient2 == null)
            stompClient2 = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient2.setMessageConverter(new MappingJackson2MessageConverter());

        stompClient2.connect(url2,httpHeaders, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                subscription2 = session.subscribe("/topic/messages/" + conversationId, new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders stompHeaders) {
                        return Message.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders stompHeaders, Object o) {
                        Message message = (Message) o;

                        System.out.println("Message2: " + message.toString());
                        if (lastMessage.getDate() != message.getDate()) {
                            Label label = new Label(message.getBody());

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    listView.getItems().add(label);
                                    listView.scrollTo(label);
                                }
                            });

                        }
                    }
                });
            }

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Message.class;
            }
        });

    }

    public void createReport() throws FileNotFoundException, JRException {
        Map<String,Object> parameters=new HashMap<>();

        System.out.println(messageCount);
        List<Integer>dummyList=new ArrayList<>();
        dummyList.add(1);
        parameters.put("messages",messageCount);

        String path=System.getProperty("user.dir")+"\\src\\main\\resources\\reports";
        File file= ResourceUtils.getFile("classpath:jrxml\\chat.jrxml");
        JasperReport jasperReport= JasperCompileManager.compileReport(file.getAbsolutePath());
        JRBeanCollectionDataSource dataSource=new JRBeanCollectionDataSource(dummyList);
        JasperPrint jasperPrint= JasperFillManager.fillReport(jasperReport,parameters,dataSource);
        JasperExportManager.exportReportToPdfFile(jasperPrint,path+"\\chat.pdf");
        pathLabel.setText("Report created!");

    }


    public void back(ActionEvent event) throws IOException {

        subscription1.unsubscribe();
        subscription2.unsubscribe();
        stompClient1.stop();
        stompClient2.stop();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/first-page.fxml"));
        loader.setControllerFactory(applicationContext::getBean);
        Parent root = loader.load();
        FirstPageController firstPageController = (FirstPageController) loader.getController();
        firstPageController.setUser(sender);
        Scene scene = new Scene(root);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

}
