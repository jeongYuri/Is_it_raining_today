package com.example.crudw.demo.Notification;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    List<Notification> findByUser(String user);
    @Transactional
    void deleteById(Long id);
    void deleteByCommentNo(Long CommentNo);

}
