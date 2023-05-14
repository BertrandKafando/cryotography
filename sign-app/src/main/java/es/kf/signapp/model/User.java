package es.kf.signapp.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import es.kf.signapp.security.user.model.AppUser;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@DynamicUpdate
public class User extends AppUser {
    private String avatar;
    private String email;
    private String login;
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<SignMode> signModes = List.of(SignMode.SIGNATURE, SignMode.GRIFFE, SignMode.ANNOTATION,SignMode.VISEUR);

    @JsonIgnore
    @OneToMany(mappedBy = "sender")
    private List<Document> documents;
    @JsonIgnore
    @OneToMany(mappedBy = "userToSign")
    private List<Workflow> workflows;
}
