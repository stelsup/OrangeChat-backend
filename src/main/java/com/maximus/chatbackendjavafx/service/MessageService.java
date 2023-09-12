package com.maximus.chatbackendjavafx.service;


import com.maximus.chatbackendjavafx.model.Message;
import com.maximus.chatbackendjavafx.repository.MessageRepository;
import com.maximus.chatbackendjavafx.service.exceptions.NoMessagesForRoomException;
import com.maximus.chatdto.MessageInfo;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialClob;
import java.sql.Clob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;


    public MessageService(MessageRepository messageRepository){
        this.messageRepository = messageRepository;
    }


    public List<MessageInfo> getMessages(Long roomId) throws NoMessagesForRoomException, SQLException {

        //TODO заменить на оконный интервал
        List<Message> messages = messageRepository.findTop100ByRoomIdOrderByTimestampAsc(roomId);

        if(messages.size() == 0){
            throw new NoMessagesForRoomException("No messages for roomID=" + roomId.toString());
        }

        List<MessageInfo> messageInfoList = new ArrayList<>();

        for(Message message: messages){
            MessageInfo info = new MessageInfo();
            info.setId(message.getUniqueID());
            info.setRoomId(message.getRoomId());
            info.setSenderId(message.getSenderId());
            info.setTimestamp(message.getTimestamp());
            //Clob clob = message.getText();
            //info.setText(clob.getSubString(1, (int)clob.length())); /// Возможны варианты
            info.setText(message.getText());

            //Blob blob = message.getProps();
            //info.setContent();
            messageInfoList.add(info);
        }

        return messageInfoList;
    }

    public List<MessageInfo> getMessages(Long roomId, LocalDateTime userPosition) throws NoMessagesForRoomException, SQLException {
        List<Message> messages = null;
        try {
            messages = messageRepository.findMessagesByRoomByPosition(roomId, userPosition);
        }catch (Exception ex) {
            System.out.println("findMessagesByRoomByPosition() ERROR=" + ex.toString());
            throw new NoMessagesForRoomException("Error finding messages for roomID=" + roomId.toString());
        }

        if(messages.size() == 0){
            throw new NoMessagesForRoomException("No messages for roomID=" + roomId.toString());
        }

        List<MessageInfo> messageInfoList = new ArrayList<>();

        for(Message message: messages){
            MessageInfo info = new MessageInfo();
            info.setId(message.getUniqueID());
            info.setRoomId(message.getRoomId());
            info.setSenderId(message.getSenderId());
            info.setTimestamp(message.getTimestamp());
            //Clob clob = message.getText();
            //info.setText(clob.getSubString(1, (int)clob.length())); /// Возможны варианты
            info.setText(message.getText());
            //Blob blob = message.getProps();
            //info.setContent();
            messageInfoList.add(info);
        }

        return messageInfoList;
    }

    public MessageInfo sendMessage(MessageInfo newMessage) throws SQLException {

        Message message = new Message(newMessage.getRoomId(), newMessage.getSenderId());
        //Clob clob = new SerialClob(newMessage.getText().toCharArray());
        //message.setText(clob);
        message.setText(newMessage.getText());
        //Blob blob
        //message.setContent();

        Message resultMessage = messageRepository.save(message);

        newMessage.setId(resultMessage.getUniqueID());
        newMessage.setTimestamp(resultMessage.getTimestamp());

        return newMessage;
    }

    public MessageInfo getMessage(Long messageId) throws SQLException {

        Message message = messageRepository.findByUniqueID(messageId).get();

        MessageInfo info = new MessageInfo();
        info.setId(message.getUniqueID());
        info.setRoomId(message.getRoomId());
        info.setSenderId(message.getSenderId());
        info.setTimestamp(message.getTimestamp());
        //Clob clob = message.getText();
        //info.setText(clob.getSubString(1, (int)clob.length())); /// Возможны варианты
        info.setText(message.getText());
        //Blob blob = message.getProps();
        //info.setContent();

        return info;
    }


    public void changeMessage(MessageInfo changedMessage) throws SQLException {

        Message message = messageRepository.findByUniqueID(changedMessage.getId()).get();
        //Clob clob = new SerialClob(changedMessage.getText().toCharArray());
        //message.setText(clob);
        message.setText(changedMessage.getText());

        //Blob blob = message.getProps();
        //message.setContent(blob);
        messageRepository.save(message);

    }


    public void deleteMessage(Long messageId){

        Message message = messageRepository.findByUniqueID(messageId).get();
        message.softDeleted();
        messageRepository.save(message);

    }

}
