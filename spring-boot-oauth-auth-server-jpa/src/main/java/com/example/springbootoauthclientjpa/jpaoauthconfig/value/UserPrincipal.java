///*
// *  Copyright (c) 2023 DMG
// *  All Rights Reserved Worldwide.
// *
// *  THIS PROGRAM IS CONFIDENTIAL AND PROPRIETARY TO DMG
// *  AND CONSTITUTES A VALUABLE TRADE SECRET.
// */
//
//package com.example.springbootoauthclientjpa.jpaoauthconfig.value;
//
//import com.example.springbootoauthclientjpa.jpaoauthconfig.entity.User;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.stream.Collectors;
//import lombok.AllArgsConstructor;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
///**
// * @author Piyush Kumar.
// * @since 16/09/23.
// */
//
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public class UserPrincipal implements UserDetails {
//
//    private User user;
//
//    @Override
//    public String getPassword() {
//        return user.getPassword();
//    }
//
//    @Override
//    public String getUsername() {
//        return user.getFirstName() + " " + user.getLastName();
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return user.isActive();
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//
//        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
//
//        List<SimpleGrantedAuthority> rolesGrantedAuthorities = user.getRolesList().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
//        List<SimpleGrantedAuthority> authorities = user.getAuthoritiesList().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
//
//        grantedAuthorities.addAll(rolesGrantedAuthorities);
//        grantedAuthorities.addAll(authorities);
//
//        return grantedAuthorities;
//    }
//}
