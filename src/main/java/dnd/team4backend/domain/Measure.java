package dnd.team4backend.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Measure {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "measure_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime date;

    private String tempInfo;

    private Float temperatureHigh;

    private Float temperatureLow;

    private Float humidity;

    private String area;

    @Enumerated(EnumType.STRING)
    private Mood mood; // VERYHOT, HOT, GOOD, COLD, VERYCOLD

    private String comment;

    @OneToMany(mappedBy = "measure")
    private List<MeasureDress> measureDressList;

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getTempInfo() {
        return tempInfo;
    }

    public Float getTemperatureHigh() {
        return temperatureHigh;
    }

    public Float getTemperatureLow() {
        return temperatureLow;
    }

    public Float getHumidity() {
        return humidity;
    }

    public String getArea() {
        return area;
    }

    public Mood getMood() {
        return mood;
    }

    public String getComment() {
        return comment;
    }

    public List<MeasureDress> getMeasureDressList() {
        return measureDressList;
    }

    public void setUser(User user) {
        this.user = user;
        user.getMeasureList().add(this);
    }
}
