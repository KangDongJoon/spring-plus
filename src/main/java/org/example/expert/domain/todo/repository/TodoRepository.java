package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface TodoRepository extends JpaRepository<Todo, Long>, CustomTodoRepository {

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

//    @Query("SELECT t FROM Todo t " +
//            "LEFT JOIN t.user " +
//            "WHERE t.id = :todoId")
//    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);

    @Query("SELECT t FROM Todo t WHERE t.weather = :weather")
    Page<Todo> findByWeather(@Param("weather") String weather, Pageable pageable);

    @Query("SELECT t FROM Todo t WHERE t.modifiedAt >= :modifiedAtStart")
    Page<Todo> findByModifiedAtAfter(@Param("modifiedAtStart") LocalDateTime modifiedAtStart, Pageable pageable);

    @Query("SELECT t FROM Todo t WHERE t.modifiedAt <= :modifiedAtEnd")
    Page<Todo> findByModifiedAtBefore(@Param("modifiedAtEnd") LocalDateTime modifiedAtEnd, Pageable pageable);

    @Query("SELECT t FROM Todo t WHERE t.modifiedAt BETWEEN :modifiedAtStart AND :modifiedAtEnd")
    Page<Todo> findByModifiedAtBetween(@Param("modifiedAtStart") LocalDateTime modifiedAtStart, @Param("modifiedAtEnd") LocalDateTime modifiedAtEnd, Pageable pageable);

}
