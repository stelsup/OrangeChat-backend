package com.maximus.chatbackendjavafx.model;


import com.sun.istack.NotNull;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Blob;
import java.sql.Clob;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Where(clause = "visible = true")
public class Message {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uniqueID;
    @NotNull
    @Column(name = "visible")
    private boolean isVisible;
    @NotNull
    private Long roomId;
    @NotNull
    private Long senderId;
    private LocalDateTime timestamp;
    @NotNull
    @Lob
    private String text;
    @Lob
    private byte[] props;

    public Message(){}

    public Message(Long roomId, Long senderId){
        this.isVisible = true;
        this.roomId = roomId;
        this.senderId = senderId;
        this.timestamp = LocalDateTime.now();
    }

    public void softDeleted() {
        this.isVisible = false; //помечаем запись как мертвую
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public Long getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(Long uniqueID) {
        this.uniqueID = uniqueID;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public byte[] getProps() {
        return props;
    }

    public void setProps(byte[] props) {
        this.props = props;
    }
}
