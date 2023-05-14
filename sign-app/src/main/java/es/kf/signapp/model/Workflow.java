package es.kf.signapp.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@DynamicUpdate
public class Workflow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private SignMode signatureMode;


    @Column(name = "sign_order")
    private int order;

    @Enumerated(EnumType.STRING)
    private WorkflowStatus status;

    private String note;

    private LocalDate processDate;


    @ManyToOne
    private User userToSign;

    @JsonIgnore
    @ManyToOne
    private Document document;

    @JsonIgnore
    @OneToMany(mappedBy = "workflow")
    private List<SignaturePosition> SignaturePosition;
}

