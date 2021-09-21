package com.simpleChatApplication;

import com.simpleChatApplication.fx.MainFX;
import javafx.application.Application;
import org.jboss.jandex.Main;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication()
public class SimpleChatApplication {

	public static void main(String[] args) {
		Application.launch(MainFX.class, args);
	}

}
