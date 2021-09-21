package com.simpleChatApplication.fx;

import com.simpleChatApplication.SimpleChatApplication;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class MainFX extends Application {

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() throws Exception {
        String[] args = getParameters().getRaw().toArray(new String[0]);

        this.applicationContext = new SpringApplicationBuilder()
                .sources(SimpleChatApplication.class)
                .run(args);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(this);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader=new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/login-register.fxml"));
        loader.setControllerFactory(applicationContext::getBean);

        LoginRegisterController.applicationContext=applicationContext;
        RegisterController.applicationContext=applicationContext;
        LoginController.applicationContext=applicationContext;
        FirstPageController.applicationContext=applicationContext;
        ChatPageController.applicationContext=applicationContext;

        Parent root= loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        this.applicationContext.close();
        Platform.exit();
    }
}
