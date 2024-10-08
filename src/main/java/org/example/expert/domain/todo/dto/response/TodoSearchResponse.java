package org.example.expert.domain.todo.dto.response;

import lombok.Getter;
import org.example.expert.domain.todo.entity.Todo;

@Getter
public class TodoSearchResponse {

    private final String title;
    private final int managersCount;
    private final int commentsCount;

    public TodoSearchResponse(Todo todo){
        this.title = todo.getTitle();
        this.managersCount = todo.getManagers().size();
        this.commentsCount = todo.getComments().size();
    }

}

