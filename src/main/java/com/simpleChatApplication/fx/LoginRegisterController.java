package com.simpleChatApplication.fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginRegisterController {
    public static ConfigurableApplicationContext applicationContext;

    private Stage stage;
    @FXML
    Button loginBtn;

    @FXML
    Button registerBtn;



    public void logIn(ActionEvent event)throws IOException {
        FXMLLoader loader=new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/login.fxml"));
        loader.setControllerFactory(applicationContext::getBean);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    public void register(ActionEvent event)throws IOException{

        FXMLLoader loader=new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/register.fxml"));
        loader.setControllerFactory(applicationContext::getBean);
        Parent root=loader.load();
        Scene scene = new Scene(root);
        stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
