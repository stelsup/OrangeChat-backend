package com.maximus.chatbackendjavafx.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.maximus.chatbackendjavafx.model.OnlineStatus;
import com.maximus.chatbackendjavafx.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;
    private Long uniqueID;

    private String login;
    private String firstName;
    private String lastName;
    private String avatar;
    private LocalDate dateOfBirth;
    @JsonIgnore
    private String password;
    private String email;

    private OnlineStatus onlineStatus;

    private Collection<? extends GrantedAuthority> authorities;


    public UserDetailsImpl(Long uniqueID, String login, String firstName, String lastName,
                           String avatar, LocalDate dateOfBirth, String email, String password, OnlineStatus onlineStatus,
                           Collection<? extends GrantedAuthority> authorities) {
        this.uniqueID = uniqueID;
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatar = avatar;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.password = password;
        this.onlineStatus = onlineStatus;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getUniqueID(),
                user.getLogin(),
                user.getFirstName(),
                user.getLastName(),
                user.getAvatar(),
                user.getDateOfBirth(),
                user.getEmail(),
                user.getPassword(),
                user.getOnlineStatus(),
                authorities);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getUniqueID() {
        return uniqueID;
    }

    public String getLogin() {
        return login;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public OnlineStatus getOnlineStatus() {
        return onlineStatus;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uniqueID == null) ? 0 : uniqueID.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserDetailsImpl other = (UserDetailsImpl) obj;
        if (uniqueID == null) {
            if (other.uniqueID != null)
                return false;
        } else if (!uniqueID.equals(other.uniqueID))
            return false;
        return true;
    }
}
