package com.example.crudw.demo.Notification;

import com.example.crudw.demo.Board.Board;
import com.example.crudw.demo.Board.BoardRepository;
import com.example.crudw.demo.Service.CommentService;
import com.example.crudw.demo.comment.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final EmitterRepository emitterRepository;

    @Autowired
    private BoardRepository boardRepository;

    public NotificationService(EmitterRepository emitterRepository, BoardRepository boardRepository) {
        this.emitterRepository = emitterRepository;
    }

    public SseEmitter sub(String userId, String lastEventId) {
        String id = userId + "_" + System.currentTimeMillis();
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(id,emitter);

        emitter.onCompletion(() -> emitterRepository.deleteById(id));
        emitter.onTimeout(() -> emitterRepository.deleteById(id));

        sendToClient(emitter, id,  userId + "새로운 댓글이 달렸습니다");

        if (!lastEventId.isEmpty()) {// 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithId(String.valueOf(userId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
        }
        logger.info("두구두구={}",emitter);
        return emitter;
    }

    public void send(String user, Comment comment, String s) {
        Notification notification = createNotification(user, comment);
        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllByIdStartingWith(user);
        sseEmitters.forEach((id, emitter) -> {
            emitterRepository.saveEventCache(user, notification); // 데이터 캐시 저장(유실 데이터 찾기 위함)
            sendToClient(emitter, id, Notification.from(notification));
        });
        logger.info("ㅅㅂ",sseEmitters);
    }
    private Notification createNotification(String user, Comment comment) {
        Long boardNo = comment.getBoardNo();
        Optional<Board> boardOptional = boardRepository.findById(boardNo);
        Board board = boardOptional.get();
        logger.info("게시번호={}",boardNo);
        logger.info("작성자={}",board.getWriterName());
        return new Notification(comment, board.getWriterName(), board);

    }

    private void sendToClient(SseEmitter emitter, String id, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(id)
                    .name("sse")
                    .data(data));
            logger.info("데이터{}",data);
        } catch (IOException exception) {
            emitterRepository.deleteById(id);
            throw new RuntimeException("연결오류!");
        }
        logger.info("게시될까?={}",emitter);
    }
}
