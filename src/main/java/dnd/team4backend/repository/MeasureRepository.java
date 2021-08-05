package dnd.team4backend.repository;

import dnd.team4backend.domain.Measure;
import dnd.team4backend.domain.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class MeasureRepository {

    @PersistenceContext
    private final EntityManager em;

    public MeasureRepository(EntityManager em) {
        this.em = em;
    }

    public void save(Measure measure) {
        // merge는 전부다 갈아 엎는다
        // 따라서 일부만 변경하고 싶을때는 변경감지를 이용한다.
        em.persist(measure);
    }

    public Measure findOne(Long id) {
        return em.find(Measure.class, id);
    }

    /**
     * 특정 유저의 평가 중, 해당 날씨 데이터와 유사한
     * 평가들을 조회
     */
    public List<Measure> findByWeather(User user, Float temperatureHigh, Float temperatureLow, Float humidity) {
        return em.createQuery("select m from Measure m where " +
                "m.user = :user and ((m.temperatureHigh <= :temperatureHigh+1F and m.temperatureHigh >= :temperatureHigh-1F) or " +
                "(m.temperatureLow <= :temperatureLow+1F and m.temperatureLow >= :temperatureLow-1F) or " +
                "(m.humidity <= :humidity+1F and m.humidity >= :humidity-1F))", Measure.class)
                .setParameter("temperatureHigh", temperatureHigh).setParameter("temperatureLow", temperatureLow)
                .setParameter("humidity", humidity).setParameter("user", user)
                .getResultList();
    }
}
