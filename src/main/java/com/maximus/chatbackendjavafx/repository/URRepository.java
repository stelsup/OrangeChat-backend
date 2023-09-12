package com.maximus.chatbackendjavafx.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class URRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void addRelationship(Long userID, Long roomID) {
        try {
            System.out.println("addRelationship(): userID=" + userID + ", roomID=" + roomID + "...");
            Query q = (Query) entityManager.createNativeQuery("INSERT INTO user_rooms (user_id, room_id, user_position, user_status) VALUES (:userID, :roomID, null, 'IDLE'); SELECT count(*) from rooms;");
            //Query q = (Query) entityManager.createNativeQuery("SELECT count(*) FROM users");

            //entityManager.getTransaction().begin();
            q.setParameter("userID", userID);
            q.setParameter("roomID", roomID);
            // bad!
             //q.executeUpdate();

//                var res = q.getResultList();
            var res = q.getSingleResult();

            // q.executeUpdate();
             //entityManager.close();

            //entityManager.getTransaction().commit();

            //EntityTransaction et = entityManager.getTransaction();
            //et.begin();
            //entityManager.createNativeQuery("UPDATE ... ;").executeUpdate();
            //q.executeUpdate();
            //et.commit();




            //System.out.println("addRelationship(): res:" + q.getResultList().toString());
          //  System.out.println("addRelationship(): res:" + et.toString());
            System.out.println("addRelationship() end");
        }
        catch(Exception ex) {
            System.out.println("-------------------------------");
            System.out.println("addRelationship(): error: ");
            ex.printStackTrace();
            System.out.println("-------------------------------");
        }
    }

}
