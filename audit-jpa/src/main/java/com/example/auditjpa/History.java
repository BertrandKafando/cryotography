package com.example.auditjpa;

import lombok.Data;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String objectType;
    private String objectId;
    private String type;
    private String message;

    public History(String userM, String toString, String add, String user_added) {
    }

    public History() {

    }
}
