package com.simpleChatApplication.fx;

import com.simpleChatApplication.model.User;
import com.simpleChatApplication.security.utils.JwtUtil;
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
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpClient;

@Component
public class LoginController {
    public static ConfigurableApplicationContext applicationContext;
    @FXML
    TextField email;

    @FXML
    PasswordField password;

    @FXML
    Label error;
    @Autowired
    UserService userService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtUtil jwtUtil;

    private Stage stage;

    public void login(ActionEvent event) throws IOException {
        String email = this.email.getText();
        String password = this.password.getText();
        String jwt = null;
        if (email != "" || password != "") {
            try {
                jwt = authenticate(email, password);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (jwt != null) {

                User searchedUser = userService.getUserByEmail(email);

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/first-page.fxml"));
                loader.setControllerFactory(applicationContext::getBean);
                Parent root = loader.load();
                FirstPageController firstPageController = (FirstPageController) loader.getController();
                firstPageController.setJwt(jwt);
                firstPageController.setUser(searchedUser);

                Scene scene = new Scene(root);
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();

            }

        }
    }

    public void register(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/register.fxml"));
        loader.setControllerFactory(applicationContext::getBean);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    private String authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (BadCredentialsException e) {
            error.setText("Wrong credentials!");
            throw new Exception("Invalid username or password", e);

        }

        final UserDetails userDetails = userService.loadUserByUsername(username);
        final String jwt = jwtUtil.generateToken(userDetails);
        System.out.println("JWT: "+jwt);
        return jwt;

    }
}
