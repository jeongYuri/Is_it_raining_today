package com.example.crudw.demo.Notification;

import com.example.crudw.demo.Service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
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

}
