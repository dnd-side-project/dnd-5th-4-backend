package dnd.team4backend.service;

import dnd.team4backend.domain.*;
import dnd.team4backend.domain.vo.DressVO;
import dnd.team4backend.domain.vo.MeasureCalendarResponse;
import dnd.team4backend.domain.vo.MeasureResponse;
import dnd.team4backend.domain.vo.MeasureVO;
import dnd.team4backend.repository.DressRepository;
import dnd.team4backend.repository.MeasureDressRepository;
import dnd.team4backend.repository.MeasureRepository;
import dnd.team4backend.repository.UserRepository;
import dnd.team4backend.service.assembler.MeasureAssembler;
import dnd.team4backend.service.assembler.MeasureCalendarAssembler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MeasureService {

    private final MeasureRepository measureRepository;
    private final DressRepository dressRepository;
    private final UserRepository userRepository;
    private final MeasureDressRepository measureDressRepository;

    public MeasureService(MeasureRepository measureRepository, DressRepository dressRepository, UserRepository userRepository, MeasureDressRepository measureDressRepository) {
        this.measureRepository = measureRepository;
        this.dressRepository = dressRepository;
        this.userRepository = userRepository;
        this.measureDressRepository = measureDressRepository;
    }

    /**
     * 날씨 평가
     */
    @Transactional
    public Long measure(String userId, List<DressVO> dresses,
                        MeasureVO measureVO) {
        User user = userRepository.findOne(userId);
        List<MeasureDress> measureDressList = new ArrayList<>();

        // 메져 드레스 생성
        for (DressVO dressVO : dresses) {
            // 만약 옷이 원래 없던 옷이면 dress DB에 저장하고 measureDress를 생성
            if (dressVO.getId() == null) {
                Dress dress = Dress.createDress(user, dressVO.getName(), dressVO.getDressType());
                dressRepository.save(dress);
                MeasureDress measureDress = MeasureDress.createMeasureDress(dress, dressVO.getPartialMood());
                measureDressList.add(measureDress);
            } else { // 원래 있던 옷이면 dress DB에서 그 옷 찾아서 measureDress 생성
                Dress dress = dressRepository.getById(dressVO.getId());
                MeasureDress measureDress = MeasureDress.createMeasureDress(dress, dressVO.getPartialMood());
                measureDressList.add(measureDress);
            }
        }

        // 평가 생성
        Measure measure = Measure.createMeasure(user, measureVO.getDate(),
                measureVO.getTempInfo(), measureVO.getTemperatureHigh(),
                measureVO.getTemperatureLow(), measureVO.getHumidity(), measureVO.getArea(),
                measureVO.getMood(), measureVO.getComment(), measureDressList);


        // 평가 저장
        measureRepository.save(measure);

        return measure.getId();
    }

    @Transactional
    public void updateMeasure(Long measureId, MeasureVO measureVO) { // measureVO는 수정된정보를 가지고 있는 measure
        Measure measure = measureRepository.getById(measureId);
        measure.modifyMeasure(measureVO);
    }

    @Transactional
    public void updateMeasureDress(String userId, Long measureId, List<DressVO> dresses) {
        Measure measure = measureRepository.getById(measureId);
        User user = userRepository.findOne(userId);

        List<MeasureDress> measureDressList = measure.getMeasureDressList();
        for (MeasureDress measureDress : measureDressList) {
            MeasureDress findMeasureDress = measureDressRepository.findOne(measureDress.getId());
            for (DressVO dressVO : dresses) {
                if (dressVO.getId() == findMeasureDress.getDress().getId()) {
                    Dress dress = dressRepository.getById(findMeasureDress.getDress().getId());
                    dress.setDressName(dressVO.getName());
                    dress.setDressType(dressVO.getDressType());
                    dressRepository.save(dress);
                    findMeasureDress.setDress(dress);
                    findMeasureDress.setPartialMood(dressVO.getPartialMood());
                }
            }
        }

    }

    @Transactional
    public void deleteMeasure(Long id) {
        measureRepository.deleteById(id);
    }

    public Page<Measure> fetchPages(Long lastMeasureId, int size, MeasureType measureType, User user, Float temperatureHigh, Float temperatureLow, Float humidity) {
        PageRequest pageRequest = PageRequest.of(0, size);
        Float tempHigh1 = temperatureHigh - 1F;
        Float tempHigh2 = temperatureHigh + 1F;
        Float tempLow1 = temperatureLow - 1F;
        Float tempLow2 = temperatureLow + 1F;
        Float humid1 = humidity - 2F;
        Float humid2 = humidity + 2F;
        if (measureType == MeasureType.USER) {
            return measureRepository.findByWeatherOfUser(lastMeasureId, user, tempHigh1, tempHigh2, tempLow1, tempLow2, humid1, humid2, pageRequest);
        } else // FetchType.OTHERS
        {
            return measureRepository.findByWeatherOfOthers(lastMeasureId, user, tempHigh1, tempHigh2, tempLow1, tempLow2, humid1, humid2, pageRequest);
        }
    }

    public List<MeasureResponse> fetchMeasurePagesBy(Long lastMeasureId, int size, MeasureType measureType, User user, Float temperatureHigh, Float temperatureLow, Float humidity) {
        Page<Measure> measures = fetchPages(lastMeasureId, size, measureType, user, temperatureHigh, temperatureLow, humidity);
        return MeasureAssembler.toDtos(measures.getContent());
    }

    public List<MeasureCalendarResponse> findByYearMonth(User user, int year, int month) {
        GregorianCalendar gc = new GregorianCalendar();
        int lastDayOfMonth = Month.of(month).length(gc.isLeapYear(year));
        LocalDateTime fromDateTime = LocalDateTime.of(LocalDate.of(year, month, 1), LocalTime.MIN);
        LocalDateTime toDateTime = LocalDateTime.of(LocalDate.of(year, month, lastDayOfMonth), LocalTime.MAX);
        List<Measure> measures = measureRepository.findByUserAndDateBetween(user, fromDateTime, toDateTime);
        return MeasureCalendarAssembler.toDtos(measures);
    }

    public MeasureResponse findOne(Long measureId) {
        Measure measure = measureRepository.getById(measureId);
        return MeasureAssembler.toDto(measure);
    }
}
