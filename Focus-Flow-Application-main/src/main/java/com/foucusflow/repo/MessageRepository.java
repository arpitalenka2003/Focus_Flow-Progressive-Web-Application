package com.foucusflow.repo;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.foucusflow.entity.Message;
import com.foucusflow.entity.User;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long>{

    @Modifying
    @Transactional
    @Query(value = "insert into user_messages(user_id,messages_id) values (:user_id, :m_id)", nativeQuery = true)
    public void saveMyMessage(@Param("user_id") long userId, @Param("m_id") long fId);

    //@Modifying
    @Query(value = "select * from message inner join user_messages on message.id = user_messages.messages_id where user_id=?1 and to_user_id=?2",nativeQuery = true)
    public List<Message> getUserSpecificMessage(long userId, long fId);
    
    @Query(value = "select * from message inner join user_messages on message.id = user_messages.messages_id where user_id=?1 and to_user_id=?2 ORDER BY send_date DESC;",nativeQuery = true)
    public List<Message> getUserSpecificLastMessage(long userId, long fId);
    
    List<Message> findAllByToUser(User user);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE user_messages SET messages_id = NULL WHERE user_id = ?1", nativeQuery = true)
    void updateUserMessages(long userId);
    
    @Modifying
    @Transactional
    @Query(value = "delete from user_messages where user_id=?1", nativeQuery = true)
    void deleteUserMessages(long userId);
}
