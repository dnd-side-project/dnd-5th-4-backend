package dnd.team4backend.service;

import dnd.team4backend.domain.*;
import dnd.team4backend.repository.DressRepository;
import dnd.team4backend.repository.MeasureDressRepository;
import dnd.team4backend.repository.MeasureRepository;
import dnd.team4backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
            if (dressRepository.findOne(dressVO.getId()) == null) {
                Dress dress = Dress.createDress(user, dressVO.getName(), dressVO.getDressType());
                dressRepository.save(dress);
                MeasureDress measureDress = MeasureDress.createMeasureDress(dress, dressVO.getPartialMood());
                measureDressList.add(measureDress);
            } else { // 원래 있던 옷이면 dress DB에서 그 옷 찾아서 measureDress 생성
                Dress dress = dressRepository.findOne(dressVO.getId());
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
        Measure measure = measureRepository.findOne(measureId);
        measure.modifyMeasure(measureVO);
    }

    @Transactional
    public void updateMeasureDress(String userId, Long measureId, List<DressVO> dresses) {
        Measure measure = measureRepository.findOne(measureId);
        User user = userRepository.findOne(userId);

        List<MeasureDress> measureDressList = measure.getMeasureDressList();
        for (MeasureDress measureDress : measureDressList) {
            MeasureDress findMeasureDress = measureDressRepository.findOne(measureDress.getId());
            for (DressVO dressVO : dresses) {
                if (dressVO.getId() == findMeasureDress.getDress().getId()) {
                    Dress dress = dressRepository.findOne(findMeasureDress.getDress().getId());
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
    public List<Measure> findByWeather(User user, Float temperatureHigh, Float temperatureLow, Float humidity) {
        return measureRepository.findByWeather(user, temperatureHigh, temperatureLow, humidity);
    }

}
