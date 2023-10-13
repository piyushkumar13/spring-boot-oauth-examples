/*
 *  Copyright (c) 2023 DMG
 *  All Rights Reserved Worldwide.
 *
 *  THIS PROGRAM IS CONFIDENTIAL AND PROPRIETARY TO DMG
 *  AND CONSTITUTES A VALUABLE TRADE SECRET.
 */

package com.example.springbootoauthresourceserver.controller;

import com.example.springbootoauthresourceserver.domain.Employee;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Piyush Kumar.
 * @since 13/10/23.
 */

@RestController
public class MyKeyclockControler {

    @GetMapping("/keyclock/authenticatedUsr")
    public ResponseEntity<Object> getAuthenticEmployees(@AuthenticationPrincipal Jwt jwt) {

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
    @GetMapping("/keyclock/scopedUsr")
    public ResponseEntity<Object> getScopedEmployees(@AuthenticationPrincipal Jwt jwt) {

        Employee employee = Employee.builder()
            .id(1)
            .name("Piyush")
            .company("ABC")
            .country("INDIA")
            .department("IT")
            .employeeType("ScopedUser")
            .jwt(jwt)
            .build();

        return ResponseEntity.ok(employee);
    }

    @GetMapping("/keyclock/developerUsr")
    public ResponseEntity<Object> getDeveloperEmployees(@AuthenticationPrincipal Jwt jwt) {
        Employee employee = Employee.builder()
            .id(1)
            .name("Piyush")
            .company("ABC")
            .country("INDIA")
            .department("IT")
            .employeeType("Developer")
            .jwt(jwt)
            .build();
        return ResponseEntity.ok(employee);
    }


//    @GetMapping("/keyclock/scopedUsr")
//    public ResponseEntity<Object> getScopedEmployees(@AuthenticationPrincipal Jwt jwt) {
//
//        return ResponseEntity.ok(jwt);
//    }
}
