package com.example.crudw.demo.Notification;

import com.example.crudw.demo.Board.Board;
import com.example.crudw.demo.Board.BoardRepository;
import com.example.crudw.demo.Service.CommentService;
import com.example.crudw.demo.comment.Comment;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

    @Autowired
    private BoardRepository boardRepository;

    private Comment comment;
    private Notification notification;

    public NotificationService(EmitterRepository emitterRepository, BoardRepository boardRepository,NotificationRepository notificationRepository) {
        this.emitterRepository = emitterRepository;
        this.boardRepository = boardRepository;
        this.notificationRepository= notificationRepository;
    }

    public SseEmitter sub(String userId, String lastEventId) {
        String id = userId + "_" + System.currentTimeMillis();
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(id,emitter);
        emitter.onCompletion(() -> emitterRepository.deleteById(id));
        emitter.onTimeout(() -> emitterRepository.deleteById(id));
        sendToClient(emitter, id,"EventStream Created. [userId=" + userId + "]");//더미 객체
        if (!lastEventId.isEmpty()) {// 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithId(String.valueOf(userId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
        }
        return emitter;
    }
    private void sendToClient(SseEmitter emitter, String id, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(id)
                    .name("sse")
                    .data( data));
        } catch (IOException exception) {
            emitterRepository.deleteById(id);
            throw new RuntimeException("연결오류!");
        }
    }
    @Transactional
    public void send(String user, Comment comment) {
        Notification notification = createNotification(user, comment);
        String name = notification.getComment().getWriterName();
        notificationRepository.save(notification);
        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllByIdStartingWith(user);
        sseEmitters.forEach((id, emitter) -> {
            emitterRepository.saveEventCache(user, notification); // 데이터 캐시 저장(유실 데이터 찾기 위함)
            sendToClient(emitter, id, notification.getMessage());
        });

        logger.info("알림 전송: 댓글쓴 사람{}",name);
    }
    /*
    private Notification createNotification(String user, Comment comment) {
        Long boardNo = comment.getBoardNo();
        Optional<Board> boardOptional = boardRepository.findById(boardNo);
        Board board = boardOptional.get();
        String message = "'" + board.getTitle() + "' 게시글에 댓글이 달렸습니다.";
        logger.info("게시번호={}",boardNo);
        logger.info("작성자={}",board.getWriterName());
        return new Notification(comment, user,board,message); //user = board.getWriterName() ->comment에서 받아옴
    }*/
    private Notification createNotification(String user, Comment comment) {
        Long boardNo = comment.getBoardNo();
        Optional<Board> boardOptional = boardRepository.findById(boardNo);
        if (boardOptional.isPresent()) {
            Board board = boardOptional.get();
            String message = "'" + board.getTitle() + "' 게시글에 댓글이 달렸습니다.";

            return Notification.builder()
                    .user(user)
                    .comment(comment)
                    .writer(comment.getWriterName())
                    .board(board)
                    .message(message)
                    .build();
        }else {
            throw new EntityNotFoundException("게시글을 찾을 수 없습니다.");
        }
    }

    @Transactional
    public List<NotificationsResponse> findAllById(String userId) {
        List<Notification> notifications = notificationRepository.findByUser(userId);
        return NotificationsResponse.fromList(notifications); // 리스트 변환 후 반환
    }
    @Transactional
    public void deleteNotification(Long id){
        notificationRepository.deleteById(id);
    }


    @Transactional
    public void deleteByCommentNo(Long no) {
        notificationRepository.deleteByCommentNo(no);
    }
}
