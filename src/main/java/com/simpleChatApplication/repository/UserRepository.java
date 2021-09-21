package com.simpleChatApplication.repository;

import com.simpleChatApplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public interface UserRepository extends JpaRepository<User,Long> {

   @Query(value = "select * from chatapp.user as u where u.email= :email and u.password= :password",nativeQuery = true)
   User getUserbyEmail(@Param("email") String email,@Param("password") String password);

   @Query(value = "select * from chatapp.user",nativeQuery = true)
   Collection<User> getAllUsers();

   User findUserByEmail(String email);


}
