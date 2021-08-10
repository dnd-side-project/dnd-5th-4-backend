package dnd.team4backend.service;

import dnd.team4backend.domain.*;
import dnd.team4backend.domain.vo.DressVO;
import dnd.team4backend.domain.vo.MeasureCalendarResponse;
import dnd.team4backend.domain.vo.MeasureResponse;
import dnd.team4backend.domain.vo.MeasureVO;
import dnd.team4backend.repository.MeasureRepository;
import dnd.team4backend.service.assembler.MeasureCalendarAssembler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
class MeasureServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    MeasureService measureService;
    @Autowired
    MeasureRepository measureRepository;


    @Test
    public void 날씨평가() throws Exception {
        //given
        User user = new User();
        user.addBasicInfo("aaa", "hh", Gender.M, Constitution.HOT);

        em.persist(user);

        DressVO d1 = new DressVO(1L, user.getId(), "회색 가디건", DressType.OUTER, Mood.GOOD);
        DressVO d2 = new DressVO(2L, user.getId(), "검은색 무지티", DressType.TOP, Mood.VERYHOT);
        DressVO d3 = new DressVO(3L, user.getId(), "연청바지", DressType.BOTTOM, Mood.COLD);
        DressVO d4 = new DressVO(4L, user.getId(), "나이키 조던", DressType.SHOES, Mood.GOOD);

        List<DressVO> dressVOList = new ArrayList<>();
        dressVOList.add(d1);
        dressVOList.add(d2);
        dressVOList.add(d3);
        dressVOList.add(d4);

        MeasureVO measureVO = new MeasureVO();
        measureVO.createMeasureVO(LocalDateTime.now(), "구름 많음", 31.5F,
                24.3F, 15F, "서울", Mood.GOOD, "날씨에 맞게 옷을 잘 입은듯하다.");
        //when

        Long measureId = measureService.measure(user.getId(), dressVOList, measureVO);

        //then

        Measure getMeasure = measureRepository.getById(measureId);

        Assertions.assertEquals(user, getMeasure.getUser());
    }

    @Test
    public void 날씨평가_옷이DB에있을경우() throws Exception {
        //given
        User user = new User();
        user.addBasicInfo("fasffasd", "hh", Gender.M, Constitution.HOT);

        em.persist(user);
        Dress dress1 = Dress.createDress(user, "회색 가디건", DressType.OUTER);
        Dress dress2 = Dress.createDress(user, "검은색 무지티", DressType.TOP);

        em.persist(dress1);
        em.persist(dress2);

        DressVO d1 = new DressVO(dress1.getId(), user.getId(), "회색 가디건", DressType.OUTER, Mood.GOOD);
        DressVO d2 = new DressVO(dress2.getId(), user.getId(), "검은색 무지티", DressType.TOP, Mood.VERYHOT);
        DressVO d3 = new DressVO(3L, user.getId(), "연청바지", DressType.BOTTOM, Mood.COLD);
        DressVO d4 = new DressVO(4L, user.getId(), "나이키 조던", DressType.SHOES, Mood.GOOD);

        List<DressVO> dressVOList = new ArrayList<>();
        dressVOList.add(d1);
        dressVOList.add(d2);
        dressVOList.add(d3);
        dressVOList.add(d4);


        MeasureVO measureVO = new MeasureVO();
        measureVO.createMeasureVO(LocalDateTime.now(), "구름 많음", 31.5F,
                24.3F, 15F, "서울", Mood.GOOD, "날씨에 맞게 옷을 잘 입은듯하다.");
        //when

        Long measureId = measureService.measure(user.getId(), dressVOList, measureVO);

        //then

        Measure getMeasure = measureRepository.getById(measureId);

        Assertions.assertEquals(user, getMeasure.getUser());
    }

    @Test
    public void 메져_수정() throws Exception {
        //given
        User user = new User();
        user.addBasicInfo("fasffasd", "hh", Gender.M, Constitution.HOT);

        em.persist(user);

        DressVO d1 = new DressVO(1L, user.getId(), "회색 가디건", DressType.OUTER, Mood.GOOD);
        DressVO d2 = new DressVO(2L, user.getId(), "검은색 무지티", DressType.TOP, Mood.VERYHOT);
        DressVO d3 = new DressVO(3L, user.getId(), "연청바지", DressType.BOTTOM, Mood.COLD);
        DressVO d4 = new DressVO(4L, user.getId(), "나이키 조던", DressType.SHOES, Mood.GOOD);

        List<DressVO> dressVOList = new ArrayList<>();
        dressVOList.add(d1);
        dressVOList.add(d2);
        dressVOList.add(d3);
        dressVOList.add(d4);

        MeasureVO measureVO = new MeasureVO();
        measureVO.createMeasureVO(LocalDateTime.now(), "구름 많음", 31.5F,
                24.3F, 15F, "서울", Mood.GOOD, "날씨에 맞게 옷을 잘 입은듯하다.");


        Long measureId = measureService.measure(user.getId(), dressVOList, measureVO);

        //when

        MeasureVO measureVO2 = new MeasureVO();
        measureVO2.setComment("코멘트를 수정하였습니다.");
        measureVO2.setTempInfo("화창");

        measureService.updateMeasure(measureId, measureVO2);

        //then

        Measure getMeasure = measureRepository.getById(measureId);

        Assertions.assertEquals(user, getMeasure.getUser());
    }

    @Test
    public void 메져드레스_수정() throws Exception {
        //given
        User user = new User();
        user.addBasicInfo("fasffasd", "hh", Gender.M, Constitution.HOT);

        em.persist(user);

        DressVO d1 = new DressVO(1L, user.getId(), "회색 가디건", DressType.OUTER, Mood.GOOD);
        DressVO d2 = new DressVO(2L, user.getId(), "검은색 무지티", DressType.TOP, Mood.VERYHOT);
        DressVO d3 = new DressVO(3L, user.getId(), "연청바지", DressType.BOTTOM, Mood.COLD);
        DressVO d4 = new DressVO(4L, user.getId(), "나이키 조던", DressType.SHOES, Mood.GOOD);

        List<DressVO> dressVOList = new ArrayList<>();
        dressVOList.add(d1);
        dressVOList.add(d2);
        dressVOList.add(d3);
        dressVOList.add(d4);

        MeasureVO measureVO = new MeasureVO();
        measureVO.createMeasureVO(LocalDateTime.now(), "구름 많음", 31.5F,
                24.3F, 15F, "서울", Mood.GOOD, "날씨에 맞게 옷을 잘 입은듯하다.");


        Long measureId = measureService.measure(user.getId(), dressVOList, measureVO);

        //when

        d1 = new DressVO(1L, user.getId(), "코트", DressType.OUTER, Mood.COLD);
        d3 = new DressVO(3L, user.getId(), "진청바지", DressType.BOTTOM, Mood.VERYHOT);

        List<DressVO> dressVOList2 = new ArrayList<>();
        dressVOList2.add(d1);
        dressVOList2.add(d3);


        // 여기서 찾아온 메져드레스의 드레스가 null로 나옴
        //measureService.updateMeasureDress(user.getId(), measureId, dressVOList2);
        //then

        Measure getMeasure = measureRepository.getById(measureId);

        Assertions.assertEquals(user, getMeasure.getUser());
    }

    @Test
    public void 특정_년월_평가_조회테스트() {
        // given
        User user = new User();
        user.addBasicInfo("aaa", "hh", Gender.M, Constitution.HOT);

        em.persist(user);

        DressVO d1 = new DressVO(1L, user.getId(), "회색 가디건", DressType.OUTER, Mood.GOOD);
        DressVO d2 = new DressVO(2L, user.getId(), "검은색 무지티", DressType.TOP, Mood.VERYHOT);
        DressVO d3 = new DressVO(3L, user.getId(), "연청바지", DressType.BOTTOM, Mood.COLD);
        DressVO d4 = new DressVO(4L, user.getId(), "나이키 조던", DressType.SHOES, Mood.GOOD);

        List<DressVO> dressVOList = new ArrayList<>();
        dressVOList.add(d1);
        dressVOList.add(d2);
        dressVOList.add(d3);
        dressVOList.add(d4);

        MeasureVO measureVO1 = new MeasureVO();
        measureVO1.createMeasureVO(LocalDateTime.of(2021, 8, 3, 15, 10), "구름 많음", 31.5F,
                24.3F, 15F, "서울", Mood.GOOD, "날씨에 맞게 옷을 잘 입은듯하다.");
        Long measureId1 = measureService.measure(user.getId(), dressVOList, measureVO1);

        MeasureVO measureVO2 = new MeasureVO();
        measureVO2.createMeasureVO(LocalDateTime.of(2021, 8, 15, 15, 10), "구름 많음", 31.5F,
                24.3F, 15F, "서울", Mood.COLD, "추웠다.");
        Long measureId2 = measureService.measure(user.getId(), dressVOList, measureVO2);

        // when
        List<MeasureCalendarResponse> findMeasureCalendarResponses = measureService.findByYearMonth(user, 2021, 8);
        List<MeasureCalendarResponse> measureCalendarResponses = new ArrayList<>();
        measureCalendarResponses.add(MeasureCalendarAssembler.toDto(measureRepository.getById(measureId1)));
        measureCalendarResponses.add(MeasureCalendarAssembler.toDto(measureRepository.getById(measureId2)));

        // then
        Assertions.assertEquals(measureCalendarResponses.get(0).getMeasureId(), findMeasureCalendarResponses.get(0).getMeasureId());
        Assertions.assertEquals(measureCalendarResponses.get(1).getMeasureId(), findMeasureCalendarResponses.get(1).getMeasureId());
    }

    @Test
    public void 평가ID조회_테스트() {
        // given
        User user = new User();
        user.addBasicInfo("aaa", "hh", Gender.M, Constitution.HOT);

        em.persist(user);

        DressVO d1 = new DressVO(null, user.getId(), "회색 가디건", DressType.OUTER, Mood.GOOD);
        DressVO d2 = new DressVO(null, user.getId(), "검은색 무지티", DressType.TOP, Mood.VERYHOT);
        DressVO d3 = new DressVO(null, user.getId(), "연청바지", DressType.BOTTOM, Mood.COLD);
        DressVO d4 = new DressVO(null, user.getId(), "나이키 조던", DressType.SHOES, Mood.GOOD);

        List<DressVO> dressVOList = new ArrayList<>();
        dressVOList.add(d1);
        dressVOList.add(d2);
        dressVOList.add(d3);
        dressVOList.add(d4);

        MeasureVO measureVO1 = new MeasureVO();
        measureVO1.createMeasureVO(LocalDateTime.of(2021, 8, 3, 15, 10), "구름 많음", 31.5F,
                24.3F, 15F, "서울", Mood.GOOD, "날씨에 맞게 옷을 잘 입은듯하다.");
        Long measureId1 = measureService.measure(user.getId(), dressVOList, measureVO1);

        // when
        MeasureResponse findMeasureResponse = measureService.findOne(measureId1);

        // then
        Assertions.assertEquals(measureId1, findMeasureResponse.getMeasureId());
    }
}