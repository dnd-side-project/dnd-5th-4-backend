package dnd.team4backend.repository;

import dnd.team4backend.domain.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
public class MeasureRepositoryTest {

    @Autowired
    MeasureRepository measureRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    DressRepository dressRepository;

    @Test
    public void 유사날씨_평가_조회_테스트() {
        // given
        User user1 = new User();
        user1.addBasicInfo("testUser1", "jun", Gender.M, 25, Constitution.COLD);
        userRepository.save(user1);

        Dress dress1 = Dress.createDress(user1, "나이키 반바지", DressType.BOTTOM);
        Dress dress2 = Dress.createDress(user1, "칼하트 반팔", DressType.TOP);
        dressRepository.save(dress1);
        dressRepository.save(dress2);

        MeasureDress measureDress1 = MeasureDress.createMeasureDress(dress1, Mood.GOOD);
        MeasureDress measureDress2 = MeasureDress.createMeasureDress(dress2, Mood.GOOD);
        List<MeasureDress> measureDressList = new ArrayList<>();
        measureDressList.add(measureDress1);
        measureDressList.add(measureDress2);

        Measure measure1 = Measure.createMeasure(user1, LocalDateTime.now(), "구름 많음", 31.5F,
                24.3F, 15F, "서울", Mood.GOOD, "적당했다", measureDressList);
        Measure measure2 = Measure.createMeasure(user1, LocalDateTime.now(), "구름 많음", 31.4F,
                24.2F, 15.1F, "서울", Mood.GOOD, "아주 적당했다", measureDressList);

        measureRepository.save(measure1);
        measureRepository.save(measure2);

        // when
        // 최고온도, 최저온도, 습도가 비슷할때
        List<Measure> findMeasureList = measureRepository.findByWeather(user1, 31.3F, 24.2F, 15F);
        // 하나도 비슷하지않을때
        List<Measure> emptyMeasureList = measureRepository.findByWeather(user1, 33F, 26F, 17F);
        // 하나의 요소만 비슷
        List<Measure> findMeasureList2 = measureRepository.findByWeather(user1, 33F, 26F, 15.3F);

        // then
        List<Measure> measureList = new ArrayList<>();
        measureList.add(measure1);
        measureList.add(measure2);
        List<Measure> emptyList = new ArrayList<>();

        Assertions.assertEquals(measureList, findMeasureList);
        Assertions.assertEquals(emptyList, emptyMeasureList);
        Assertions.assertEquals(measureList, findMeasureList2);
    }
}
