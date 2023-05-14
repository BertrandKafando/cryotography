package com.example.auditjpa;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.lang.reflect.Field;


@Component
public class MyEntityListener {

    private HistoryRepository historyRepository;
    public MyEntityListener( HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
        System.out.println("historyRepository = " + historyRepository);
    }

    public MyEntityListener() {
    }


    @PreUpdate
    public void beforeUpdate(Object entity) throws IllegalAccessException {

        createHistory(entity, "update");
    }

    @PrePersist
    public void beforePersist(Object entity) throws IllegalAccessException {
        createHistory(entity, "create");
    }

    private void createHistory(Object entity, String type) throws IllegalAccessException {
        Class<?> clazz = entity.getClass();
        Field idField = null;
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                idField = field;
                break;
            }
        }
        if (idField == null) {
            throw new IllegalArgumentException("Entity " + clazz.getSimpleName() + " does not have an ID field");
        }
        idField.setAccessible(true);
        Long id = (Long) idField.get(entity);

        History history = new History();
        history.setObjectType(clazz.getSimpleName());
        history.setObjectId(id.toString());
        history.setType(type);
        history.setMessage(clazz.getSimpleName() + " with ID " + id + " was " + type + "d");
        System.out.println("history = " + history);
        historyRepository.save(history);
    }
}
