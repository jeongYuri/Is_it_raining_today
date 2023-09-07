package com.example.crudw.demo;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class TimeEntity {
    @CreatedDate
    @Column(updatable = true)
    private LocalDateTime create_time;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modify_time;

    public LocalDateTime getCreateTime() {
        return create_time;
    }

    public LocalDateTime getModifyTime() {
        return modify_time;
    }

    @PrePersist
    public void prePersist(){
        LocalDateTime now = LocalDateTime.now();
        create_time = now;
        modify_time = now;
    }
    @PreUpdate
    public void preUpdate() {
        modify_time = LocalDateTime.now();
    }

}
