/*
 *  Copyright (c) 2023 DMG
 *  All Rights Reserved Worldwide.
 *
 *  THIS PROGRAM IS CONFIDENTIAL AND PROPRIETARY TO DMG
 *  AND CONSTITUTES A VALUABLE TRADE SECRET.
 */

package com.example.springbootoauthresourceserver.controller;

import com.example.springbootoauthresourceserver.domain.Employee;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Piyush Kumar.
 * @since 16/09/23.
 */

@RestController
public class SimpleRestController {

    // region check basic authentication based on roles

    @GetMapping("/")
    public ResponseEntity<Object> getEmployees(){

        Employee employee = Employee.builder()
            .id(1)
            .name("Piyush")
            .company("ABC")
            .country("INDIA")
            .department("IT")
            .employeeType("general")
            .build();

        return ResponseEntity.ok(employee);
    }

    @GetMapping("/employee/authenticatedUsr")
    public ResponseEntity<Object> getAuthenticatedEmployees(@AuthenticationPrincipal Jwt jwt) {

        Employee employee = Employee.builder()
            .id(1)
            .name("Piyush")
            .company("ABC")
            .country("INDIA")
            .department("IT")
            .employeeType("AuthenticUser")
            .jwt(jwt)
            .build();

        return ResponseEntity.ok(employee);
    }

    @GetMapping("/employee/adm")
    public ResponseEntity<Object> getAdminEmployees(){

        Employee employee = Employee.builder()
            .id(1)
            .name("Piyush")
            .company("ABC")
            .country("INDIA")
            .department("IT")
            .employeeType("Admin")
            .build();

        return ResponseEntity.ok(employee);
    }


    @GetMapping("/employee/usr")
    public ResponseEntity<Object> getUserEmployees(){

        Employee employee = Employee.builder()
            .id(1)
            .name("Piyush")
            .company("ABC")
            .country("INDIA")
            .department("IT")
            .employeeType("user")
            .build();

        return ResponseEntity.ok(employee);
    }

    @PostMapping("/employee/post-creds")
    public ResponseEntity<Object> getUserEmployees(@RequestBody Map<String, String> request){

        String username = request.get("username");
        String password = request.get("password");

        String body = username + " " + password;

        System.out.println("The creds are ::: " + body);
        return ResponseEntity.ok(body);
    }

    // endregion check basic authentication based on roles

    // region check basic authentication based on authorities

    @GetMapping("/checkAuthorities/admUser")
    public ResponseEntity<Object> getEmployeesForAdminAuthorities(){

        Employee employee = Employee.builder()
            .id(1)
            .name("Piyush")
            .company("ABC")
            .country("INDIA")
            .department("IT")
            .employeeType("general")
            .build();

        return ResponseEntity.ok(employee);
    }


    @GetMapping("/checkAuthorities/usr")
    public ResponseEntity<Object> getEmployeesForUserAuthorities(){

        Employee employee = Employee.builder()
            .id(1)
            .name("Piyush")
            .company("ABC")
            .country("INDIA")
            .department("IT")
            .employeeType("user")
            .build();

        return ResponseEntity.ok(employee);
    }

    // endregion check basic authentication based on authorities


}
