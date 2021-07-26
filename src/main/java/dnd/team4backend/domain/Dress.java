package dnd.team4backend.domain;

import javax.persistence.*;

@Entity
public class Dress {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "dress_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String dressName;

    @Enumerated(EnumType.STRING)
    private DressType dressType; // TOP, BOTTOM, OUTER, SHOES, ETC


    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getDressName() {
        return dressName;
    }

    public DressType getDressType() {
        return dressType;
    }

}
