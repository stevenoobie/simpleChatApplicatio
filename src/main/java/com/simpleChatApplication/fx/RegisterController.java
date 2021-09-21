package com.simpleChatApplication.fx;

import com.simpleChatApplication.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RegisterController {


    public static ConfigurableApplicationContext applicationContext;

    @Autowired
    UserService userService;

    @FXML
    TextField email;
    @FXML
    TextField firstName;
    @FXML
    TextField lastName;
    @FXML
    PasswordField password;

    @FXML
    Label success;

    private Stage stage;


    public void Login(ActionEvent event) throws IOException {
        FXMLLoader loader=new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/login.fxml"));
        loader.setControllerFactory(applicationContext::getBean);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    public void submit(){

            String email=this.email.getText();
            String password=this.password.getText();
            String firstName=this.firstName.getText();
            String lastName=this.lastName.getText();
            if(email!=null || password!=null || firstName!=null || lastName!=null){
                String status=userService.createUser(email,password,firstName,lastName);
                success.setText("Account created successfully !");
            }else {
                System.out.println("One or more of the fields is/are empty");
            }
    }


}
