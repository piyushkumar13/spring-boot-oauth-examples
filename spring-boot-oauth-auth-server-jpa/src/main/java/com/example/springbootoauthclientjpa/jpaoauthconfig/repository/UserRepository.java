/*
 *  Copyright (c) 2023 DMG
 *  All Rights Reserved Worldwide.
 *
 *  THIS PROGRAM IS CONFIDENTIAL AND PROPRIETARY TO DMG
 *  AND CONSTITUTES A VALUABLE TRADE SECRET.
 */

package com.example.springbootoauthclientjpa.jpaoauthconfig.repository;

import com.example.springbootoauthclientjpa.jpaoauthconfig.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Piyush Kumar.
 * @since 16/09/23.
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    User getUserByEmailId(String emailId);
}
