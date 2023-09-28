package com.example.crudw.demo.Notification;

import com.example.crudw.demo.Service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
public class NotificationController {
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
    private  final NotificationService notificationService;

    public NotificationController(NotificationService notificationService){
        this.notificationService= notificationService;
    }
    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping(value = "/sub/{id}",produces = "text/event-stream")
    public SseEmitter sub(@PathVariable String id,
                          @RequestHeader(value = "Last-Event-Id",required = false,defaultValue = "")String lastEventId){
        logger.info("Received ID: {}", id);
        return notificationService.sub(id,lastEventId);
    }
    @GetMapping("/notifications/{id}")
    public ResponseEntity<NotificationsResponse> notifications(@PathVariable String id) {
        return ResponseEntity.ok().body(notificationService.findAllById(id));
    }

    @PostMapping("/deletenotification/{no}")
    public ResponseEntity<String> deletePost(@PathVariable("no") Long no) {
        notificationService.deleteNotification(no);
        return ResponseEntity.ok("알림이 삭제되었습니다.");
    }

}
