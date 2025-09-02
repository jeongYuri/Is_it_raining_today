package com.example.crudw.demo;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.crudw.demo.Heart.HeartRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.example.crudw.demo.Heart.HeartService;
import com.example.crudw.demo.Heart.Heart;

import java.util.Optional;
public class HeartServiceTest {
    @Mock
    private HeartRepository heartRepository;

    @InjectMocks
    private HeartService heartService;

    public HeartServiceTest(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddHeart(){
        Long userId = 1L;
        Long boardId = 1L;
        when(heartRepository.findHeartByUserIdAndBoardId(userId, boardId)).thenReturn(Optional.empty());

        heartService.addHeart(userId, boardId);
        verify(heartRepository, times(1)).save(any(Heart.class));
        verifyNoMoreInteractions(heartRepository); // 추가적인 호출이 없음을 검증
    }
}
