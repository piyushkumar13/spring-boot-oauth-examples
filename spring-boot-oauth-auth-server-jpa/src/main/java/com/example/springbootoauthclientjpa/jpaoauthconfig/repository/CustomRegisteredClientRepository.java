/*
 *  Copyright (c) 2023 DMG
 *  All Rights Reserved Worldwide.
 *
 *  THIS PROGRAM IS CONFIDENTIAL AND PROPRIETARY TO DMG
 *  AND CONSTITUTES A VALUABLE TRADE SECRET.
 */

package com.example.springbootoauthclientjpa.jpaoauthconfig.repository;

import com.example.springbootoauthclientjpa.jpaoauthconfig.entity.CustomRegisteredClient;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Piyush Kumar.
 * @since 07/10/23.
 */
public interface CustomRegisteredClientRepository extends JpaRepository<CustomRegisteredClient, String> {

    Optional<CustomRegisteredClient> findByClientId(String clientId);
}
