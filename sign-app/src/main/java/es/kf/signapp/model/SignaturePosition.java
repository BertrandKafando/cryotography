package es.kf.signapp.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Setter
@Entity
@DynamicUpdate  // change updated fields only
public class SignaturePosition {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int x;
    private int y;
    private int width;
    private int height;
    private int pageIndex;
    @ManyToOne
    private Workflow workflow;
}
