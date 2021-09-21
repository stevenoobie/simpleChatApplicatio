package com.simpleChatApplication.service;

import com.simpleChatApplication.model.User;
import com.simpleChatApplication.repository.UserRepository;
import com.simpleChatApplication.security.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate template;



    public User getUserByID(Long id) {
        return userRepository.getById(id);
    }

    public String createUser(String email, String password, String firstName, String lastName) {
        @Valid User user = new User();
        user.setEmail(email);
        user.setFirstname(firstName);
        user.setLastname(lastName);
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        userRepository.save(user);
        sendUserToSubscribers(user);
        return "User Created Successfully";
    }

    public User getUserByEmail(String email) {
        User user=userRepository.findUserByEmail(email);
        return user;
    }


    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Long getUsersCount(){
        return userRepository.count();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
            Map<String, String> errors = new HashMap<>();
            ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @MessageMapping("/chatapp")
    public void sendUserToSubscribers(User user) {
        template.convertAndSend("/topic/users", user);
        System.out.println("Sending user to users WebSocket ");
    }




    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user=userRepository.findUserByEmail(email);
        System.out.println("In loadUserByUsername method in UserService.");
        if(user==null){
            throw new UsernameNotFoundException(email);
        }
        return new MyUserDetails(user);
    }


}
