package com.maximus.chatbackendjavafx.model;

import com.sun.istack.NotNull;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "rooms")
@Where(clause = "visible = true")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uniqueID;

    @NotNull
    @Column(name = "visible")
    private boolean isVisible;
    @NotNull
    @Column( length = 80)
    private String name;
    private String avatar;
    private String lastMessagePreview;
    @NotNull
    private LocalDateTime dateOfModify;
    @NotNull
    private Long ownerId;


    public Room() {}

    public Room(String name, String avatar, Long ownerId){
        this.name = name;
        this.avatar = avatar;
        this.isVisible = true;
        this.dateOfModify = LocalDateTime.now();
        this.ownerId = ownerId;
    }

    public Long getUniqueID() {
        return uniqueID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLastMessagePreview() {
        return lastMessagePreview;
    }

    public void setLastMessagePreview(String lastMessagePreview) {
        this.lastMessagePreview = lastMessagePreview;
    }

    public void softDeleted() {
        this.isVisible = false; //помечаем запись как мертвую
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public LocalDateTime getDateOfModify() {
        return dateOfModify;
    }

    public void setDateOfModify(LocalDateTime dateOfModify) {
        this.dateOfModify = dateOfModify;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}
