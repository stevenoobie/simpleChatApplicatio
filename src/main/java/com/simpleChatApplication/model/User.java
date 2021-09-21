package com.simpleChatApplication.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "user")
public class User implements Comparable<User> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 64)
    @Size(min = 8,message = "Password must be more than 8 characters")
    @NotBlank(message = "Password is mandatory")
    private String password;
    @Column(nullable = false,length = 45,unique = true)
    @NotBlank(message = "Email is mandatory")
    private String email;
    @Column(nullable = false,length = 20)
    @Size(min = 4, message = "First name is too short")
    @NotBlank(message = "First Name is mandatory")
    private String firstname;
    @Column(nullable = false,length = 20)
    @Size(min = 2,message = "Last name is too short")
    @NotBlank(message = "Last is mandatory")
    private String lastname;
    private String roles;

    public Long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        String firstLetStr = firstname.substring(0, 1);
        String remLetStr = firstname.substring(1);
        firstLetStr = firstLetStr.toUpperCase();
        String capitalizedFirstName = firstLetStr + remLetStr;

        firstLetStr = lastname.substring(0, 1);
        remLetStr = lastname.substring(1);
        firstLetStr = firstLetStr.toUpperCase();
        String capitalizedLastName = firstLetStr + remLetStr;



        return capitalizedFirstName+" "+capitalizedLastName;
    }

    @Override
    public int compareTo(User o) {
        return this.getId().compareTo(o.getId());
    }
}
