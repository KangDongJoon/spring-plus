package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class TodoRepositoryImpl implements CustomTodoRepository{

    @Autowired
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(todo)
                        .leftJoin(todo.user, user).fetchJoin()
                        .where(todo.id.eq(todoId))
                        .fetchOne()
        );
    }

    @Override
    public Page<Todo> findByCondition(String title
            , LocalDateTime createdAtStart, LocalDateTime createdAtEnd
            , String nickname, Pageable pageable) {

        BooleanExpression condition = buildCondition(title, createdAtStart, createdAtEnd, nickname);

        List<Todo> results = jpaQueryFactory
                .selectFrom(todo)
                .leftJoin(todo.managers)
                .leftJoin(todo.comments).fetchJoin()
                .where(condition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory
                .selectFrom(todo)
                .where(condition)
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    private BooleanExpression buildCondition(String title, LocalDateTime createdAtStart
            , LocalDateTime createdAtEnd, String nickname) {
        BooleanExpression condition = todo.isNotNull();

        if (title != null && !title.isEmpty()) {
            condition = condition.and(todo.title.containsIgnoreCase(title));
        }

        if (createdAtStart != null) {
            condition = condition.and(todo.createdAt.after(createdAtStart));
        }

        if (createdAtEnd != null) {
            condition = condition.and(todo.createdAt.before(createdAtEnd));
        }

        if (nickname != null && !nickname.isEmpty()) {
            condition = condition.and(todo.user.nickname.contains(nickname));
        }

        return condition;
    }

}
