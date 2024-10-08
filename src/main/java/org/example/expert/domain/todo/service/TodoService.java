package org.example.expert.domain.todo.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final WeatherClient weatherClient;

    @Transactional
    public TodoSaveResponse saveTodo(AuthUser authUser, TodoSaveRequest todoSaveRequest) {
        User user = User.fromAuthUser(authUser);

        String weather = weatherClient.getTodayWeather();

        Todo newTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );
        Todo savedTodo = todoRepository.save(newTodo);

        return new TodoSaveResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getContents(),
                weather,
                new UserResponse(user.getId(), user.getEmail())
        );
    }

    public Page<TodoResponse> getTodos(int page, int size, String weather, LocalDateTime modifiedAtStart, LocalDateTime modifiedAtEnd) {
        Pageable pageable = PageRequest.of(page - 1, size);

        // weather 조건이 있는경우
        if (weather != null) {
            return todoRepository.findByWeather(weather, pageable).map(this::createResponse);
        }

        // 둘 다 입력한경우
        if (modifiedAtStart != null && modifiedAtEnd != null) {
            return todoRepository.findByModifiedAtBetween(modifiedAtStart, modifiedAtEnd, pageable).map(this::createResponse);
        }

        // 기간 시작부분만 입력한 경우
        if (modifiedAtStart != null) {
            return todoRepository.findByModifiedAtAfter(modifiedAtStart, pageable).map(this::createResponse);
        }

        // 기간 끝부분만 입력한 경우
        if (modifiedAtEnd != null) {
            return todoRepository.findByModifiedAtBefore(modifiedAtEnd, pageable).map(this::createResponse);
        }



        Page<Todo> todos = todoRepository.findAllByOrderByModifiedAtDesc(pageable);

        return todos.map(this::createResponse);
    }

    public TodoResponse getTodo(long todoId) {
        Todo todo = todoRepository.findByIdWithUser(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        User user = todo.getUser();

        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(user.getId(), user.getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        );
    }

    public Page<TodoSearchResponse> searchTodo(int page, int size, String title, LocalDateTime createdAtStart, LocalDateTime createdAtEnd, String nickname) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Todo> todos = todoRepository.findByCondition(title, createdAtStart, createdAtEnd, nickname, pageable);

        return todos.map(this::createSearchResponse);
    }

    public TodoResponse createResponse(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(todo.getUser().getId(), todo.getUser().getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        );
    }

    public TodoSearchResponse createSearchResponse(Todo todo) {
        return new TodoSearchResponse(todo);
    }
}
