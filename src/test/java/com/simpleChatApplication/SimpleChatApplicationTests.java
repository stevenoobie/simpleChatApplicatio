package com.simpleChatApplication;

import com.simpleChatApplication.model.User;
import com.simpleChatApplication.repository.ConversationRepository;
import com.simpleChatApplication.repository.UserRepository;
import com.simpleChatApplication.service.ConversationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class SimpleChatApplicationTests {

	@Autowired
	UserRepository userRepository;

	@Autowired
	ConversationRepository conversationRepository;

	@Test
	public void createUser(){
		User user=new User();
		user.setEmail("test2");
		user.setPassword(passwordEncoder().encode("password"));
		user.setFirstname("test firstname");
		user.setLastname("test lastname");
		user.setRoles("ROLE_ADMIN");
		userRepository.save(user);
	}
	@Test
	public void conversationCount(){
		System.out.println(conversationRepository.getConversationsCount());
	}

	@Bean
	PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}
}
