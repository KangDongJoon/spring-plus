package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CustomTodoRepository {

    Optional<Todo> findByIdWithUser(Long todoId);

    Page<Todo> findByCondition(String title
            , LocalDateTime createdAtStart, LocalDateTime createdAtEnd
            , String nickname, Pageable pageable);
}
