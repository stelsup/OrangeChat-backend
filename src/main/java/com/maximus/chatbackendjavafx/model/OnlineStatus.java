package com.maximus.chatbackendjavafx.model;

import com.maximus.chatdto.EOnlineStatusInfo;
import com.sun.istack.NotNull;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Embeddable
public class OnlineStatus {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EOnlineStatusInfo status;
    @NotNull
    @Column(nullable = false)
    private LocalDateTime lastTimeOnline;



    public OnlineStatus(){}

    public OnlineStatus(EOnlineStatusInfo status, LocalDateTime lastTimeOnline){
        this.status = status;
        this.lastTimeOnline = lastTimeOnline;
    }

    public EOnlineStatusInfo getStatus() {
        return status;
    }

    public void setStatus(EOnlineStatusInfo status) {
        this.status = status;
    }

    public LocalDateTime getLastTimeOnline() {
        return lastTimeOnline;
    }

    public void setLastTimeOnline(LocalDateTime lastTimeOnline) {this.lastTimeOnline = lastTimeOnline; }

}
