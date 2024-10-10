package org.example.expert.domain.log.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.common.entity.Timestamped;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "logs")
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long todoId;
    private Long managerId;
    private Long requestUserId;
    private Boolean success = false;
    private LocalDateTime createdAt;

    public Log(Long todoId, Long managerId ,Long requestUserId) {
        this.todoId = todoId;
        this.managerId = managerId;
        this.requestUserId = requestUserId;
        this.createdAt = LocalDateTime.now();
    }

    public void setSuccess(Boolean b){
        this.success = b;
    }
}
