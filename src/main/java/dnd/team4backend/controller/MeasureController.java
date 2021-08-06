package dnd.team4backend.controller;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dnd.team4backend.domain.*;
import dnd.team4backend.repository.UserRepository;
import dnd.team4backend.service.MeasureService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class MeasureController {

    private final MeasureService measureService;
    private final UserRepository userRepository;

    public MeasureController(MeasureService measureService, UserRepository userRepository) {
        this.measureService = measureService;
        this.userRepository = userRepository;
    }

    @PostMapping(value = "measure")
    public String addMeasure(@RequestBody MeasureForm measureForm) {

        String userId = measureForm.getUserId();
        if (userRepository.findOne(userId) == null) {
            JsonObject obj = new JsonObject();
            obj.addProperty("status", 400);
            obj.addProperty("msg", "해당하는 id의 회원이 존재하지 않습니다.");

            return obj.toString();
        }

        User user = userRepository.findOne(userId);

        LocalDateTime date = measureForm.getDate();
        Float temperatureHigh = measureForm.getTemperatureHigh();
        Float temperatureLow = measureForm.getTemperatureLow();
        Float humidity = measureForm.getHumidity();
        String area = measureForm.getArea();
        String tempInfo = measureForm.getTempInfo();
        List<MeasureDressForm> dresses = measureForm.getDresses();
        Mood mood = measureForm.getMood();
        String comment = measureForm.getComment();

        List<DressVO> dressVOList = new ArrayList<>();
        for (MeasureDressForm mdf : dresses) {
            DressVO dressVO = new DressVO(mdf.getId(), mdf.getUserId(), mdf.getName(), mdf.getType(), mdf.getPartialMood());
            dressVOList.add(dressVO);
        }

        MeasureVO measureVO = new MeasureVO();
        measureVO.createMeasureVO(date, tempInfo, temperatureHigh, temperatureLow, humidity, area, mood, comment);

        try {
            measureService.measure(user.getId(), dressVOList, measureVO);
            JsonObject obj = new JsonObject();
            obj.addProperty("status", 200);
            obj.addProperty("msg", "평가 등록 완료");
            return obj.toString();

        } catch (IllegalStateException e) {
            JsonObject obj = new JsonObject();

            obj.addProperty("status", 400);
            obj.addProperty("msg", e.getMessage());

            return obj.toString();
        }

    }

    @PatchMapping(value = "measure/{id}")
    public String updateMeasure(@RequestBody MeasureForm measureForm, @PathVariable("id") Long id) {
        String userId = measureForm.getUserId();
        if (userRepository.findOne(userId) == null) {
            JsonObject obj = new JsonObject();
            obj.addProperty("status", 400);
            obj.addProperty("msg", "해당하는 id의 회원이 존재하지 않습니다.");

            return obj.toString();
        }
        User user = userRepository.findOne(userId);

        LocalDateTime date = measureForm.getDate();
        Float temperatureHigh = measureForm.getTemperatureHigh();
        Float temperatureLow = measureForm.getTemperatureLow();
        Float humidity = measureForm.getHumidity();
        String area = measureForm.getArea();
        String tempInfo = measureForm.getTempInfo();
        List<MeasureDressForm> dresses = measureForm.getDresses();
        Mood mood = measureForm.getMood();
        String comment = measureForm.getComment();

        MeasureVO measureVO = new MeasureVO();
        measureVO.createMeasureVO(date, tempInfo, temperatureHigh, temperatureLow, humidity, area, mood, comment);

        if (measureForm.getDresses() == null) {

            try {
                measureService.updateMeasure(id, measureVO);
                JsonObject obj = new JsonObject();
                obj.addProperty("status", 200);
                obj.addProperty("msg", "평가 수정 완료");
                return obj.toString();

            } catch (IllegalStateException e) {
                JsonObject obj = new JsonObject();

                obj.addProperty("status", 400);
                obj.addProperty("msg", e.getMessage());

                return obj.toString();
            }

        } else { // 드레스 리스트가 있을 경우
            try {
                measureService.updateMeasure(id, measureVO);
                List<DressVO> dressVOList = new ArrayList<>();
                for (MeasureDressForm mdf : dresses) {
                    DressVO dressVO = new DressVO(mdf.getId(), mdf.getUserId(), mdf.getName(), mdf.getType(), mdf.getPartialMood());
                    dressVOList.add(dressVO);
                }
                measureService.updateMeasureDress(userId, id, dressVOList);

                JsonObject obj = new JsonObject();
                obj.addProperty("status", 200);
                obj.addProperty("msg", "평가 드레스 수정 완료");
                return obj.toString();

            } catch (IllegalStateException e) {
                JsonObject obj = new JsonObject();

                obj.addProperty("status", 400);
                obj.addProperty("msg", e.getMessage());

                return obj.toString();
            }
        }
    }

    @DeleteMapping(value = "measure/{id}")
    public String deleteMeasure(@RequestBody Map<String, Object> req, @PathVariable("id") Long id) {
        String userId = req.get("userId").toString();
        if (userRepository.findOne(userId) == null) {
            JsonObject obj = new JsonObject();
            obj.addProperty("status", 400);
            obj.addProperty("msg", "해당하는 id의 회원이 존재하지 않습니다.");

            return obj.toString();
        }

        try {
            measureService.deleteMeasure(id);
            JsonObject obj = new JsonObject();
            obj.addProperty("status", 200);
            obj.addProperty("msg", "평가 삭제 완료");
            return obj.toString();

        } catch (IllegalStateException e) {
            JsonObject obj = new JsonObject();

            obj.addProperty("status", 400);
            obj.addProperty("msg", e.getMessage());

            return obj.toString();
        }
    }

    @GetMapping(value = "measure")
    public String findUserMeasure(@RequestBody WeatherUserForm weatherUserForm) {
        String userId = weatherUserForm.getUserId();
        User user = userRepository.findOne(userId);
        List<Measure> measureList = measureService.findByWeather(user, weatherUserForm.getTemperatureHigh(), weatherUserForm.getTemperatureLow(), weatherUserForm.getHumidity());

        JsonObject obj = new JsonObject();
        obj.addProperty("status", 200);
        obj.addProperty("msg", "입력하신 날씨와 유사한 유저의 평가들을 조회하였습니다.");
        obj.addProperty("userId", weatherUserForm.getUserId());
        obj.addProperty("temperatureHigh", weatherUserForm.getTemperatureHigh());
        obj.addProperty("temperatureLow", weatherUserForm.getTemperatureLow());
        obj.addProperty("humidity", weatherUserForm.getHumidity());

        JsonArray measures = new JsonArray();
        for (Measure measure : measureList) {
            JsonObject measureObj = new JsonObject();
            measureObj.addProperty("measureId", measure.getId());
            measureObj.addProperty("temperatureHigh", measure.getTemperatureHigh());
            measureObj.addProperty("temperatureLow", measure.getTemperatureLow());
            measureObj.addProperty("humidity", measure.getHumidity());
            measureObj.addProperty("mood", measure.getMood().toString());
            JsonArray measureDresses = new JsonArray();
            for (MeasureDress measureDress : measure.getMeasureDressList()) {
                Dress dress = measureDress.getDress();
                JsonObject dressObj = new JsonObject();
                dressObj.addProperty("dressName", dress.getDressName());
                dressObj.addProperty("dressType", dress.getDressType().toString());
                dressObj.addProperty("partialMood", measureDress.getPartialMood().toString());
                measureDresses.add(dressObj);
            }
            measureObj.add("measureDresses", measureDresses);
            measures.add(measureObj);
        }
        obj.add("measures", measures);
        return obj.toString();
    }
}
