package com.foucusflow.repo;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.foucusflow.entity.Contacts;

@Repository
public interface ContactsRepository extends JpaRepository<Contacts, Long>{

    @Query(value = "select * from contacts where user = ? and is_contact = 1;",nativeQuery = true)
    public List<Contacts> listOfContacts(long id);

    @Query(value = "select * from contacts where user = ? and is_blocked = 1 and is_contact = 0;",nativeQuery = true)
    public List<Contacts> listOfBlockedContacts(long id);
    
    @Query(value = "select * from contacts where user = ? and my_contacts_id = ?;",nativeQuery = true)
    public Contacts checkBlockedOrNot(long userId, long conId);

    @Modifying
    @Transactional
    @Query(value = "delete from contacts where user = :user_id and my_contacts_id = :f_id", nativeQuery = true)
    public void deleteMyContact(@Param("user_id") long userId, @Param("f_id") long fId);

    @Modifying
    @Transactional
    @Query(value = "update contacts set save_name = :rename where user = :user_id and my_contacts_id = :f_id", nativeQuery = true)
    public void renameContact(@Param("rename") String rename, @Param("user_id") long userId, @Param("f_id") long fId);
    
    @Modifying
    @Transactional
    @Query(value = "update contacts set save_name = :rename, is_friend = :isfriend where f_id = :fid", nativeQuery = true)
    public void updateMakeContact(@Param("rename") String rename, @Param("isfriend") boolean isFriend, @Param("fid") long fId);
    
    @Modifying
    @Transactional
    @Query(value = "update contacts set is_blocked = :isblocked where f_id = :fid", nativeQuery = true)
    public void updateBlockedContact(@Param("isblocked") boolean isFriend, @Param("fid") long fId);
    
}
