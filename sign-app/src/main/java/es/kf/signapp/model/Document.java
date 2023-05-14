package es.kf.signapp.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@DynamicUpdate
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String path;
    private String filename;
    //private String sender;
    private String metadata;
    private LocalDate creationDate;
    private LocalDate updateDate;
    private String notes;
    //private String workflow;

    @ManyToOne
    private User sender;

    @Transient
    private  String base64;


    @OneToMany (mappedBy = "document")
    private List<Workflow> workflows = new ArrayList<>();

}
