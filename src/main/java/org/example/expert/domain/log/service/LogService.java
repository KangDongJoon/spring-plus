package org.example.expert.domain.log.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.log.entity.Log;
import org.example.expert.domain.log.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long createLog(Long todoId, Long managerId, Long requestUserId) {
        Log saveLog = new Log(todoId, managerId, requestUserId);
        logRepository.save(saveLog);
        return saveLog.getId();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logUpdate(Boolean b) {

    }

}
