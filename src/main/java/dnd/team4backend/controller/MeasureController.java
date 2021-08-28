package dnd.team4backend.controller;


import com.google.gson.JsonObject;
import dnd.team4backend.controller.form.MeasureDressForm;
import dnd.team4backend.controller.form.MeasureForm;
import dnd.team4backend.domain.MeasureType;
import dnd.team4backend.domain.Mood;
import dnd.team4backend.domain.User;
import dnd.team4backend.domain.vo.*;
import dnd.team4backend.repository.UserRepository;
import dnd.team4backend.service.MeasureService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
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
        try {
            measureService.validateMeasureByDate(user, date);
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
    public ResponseEntity getMeasurePagesUser(@RequestParam String userId, @RequestParam Float tempHigh,
                                              @RequestParam Float tempLow, @RequestParam Float humid,
                                              @RequestParam Long lastMeasureId, @RequestParam int size, @RequestParam String measureType) {
        try {
            User user = userRepository.findOne(userId);
            List<MeasureResponse> measureResponses = new ArrayList<>();
            BasicResponseEntity basicResponseEntity;
            if (measureType.equals("others")) {
                measureResponses = measureService.fetchMeasurePagesBy(lastMeasureId, size, MeasureType.OTHERS, user, tempHigh, tempLow, humid);
                basicResponseEntity = new MeasureResponseEntity(200, "해당 유저를 제외한 다른 유저들의 평가를 조회하였습니다.", measureResponses);
            } else // measureType == "user"
            {
                measureResponses = measureService.fetchMeasurePagesBy(lastMeasureId, size, MeasureType.USER, user, tempHigh, tempLow, humid);
                basicResponseEntity = new MeasureResponseEntity(200, "해당 유저의 평가를 조회하였습니다.", measureResponses);
            }
            return new ResponseEntity(basicResponseEntity, HttpStatus.OK);
        } catch (IllegalStateException e) {
            BasicResponseEntity basicResponseEntity = new BasicResponseEntity(400, "평가 조회 중 오류가 발생했습니다.");
            return new ResponseEntity(basicResponseEntity, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "measure/calendar")
    public ResponseEntity getUserMeasuresYearMonth(@RequestParam String userId, @RequestParam int year, @RequestParam int month) {
        try {
            User user = userRepository.findOne(userId);
            List<MeasureCalendarResponse> measureCalendarResponses = measureService.findByYearMonth(user, year, month);
            MeasureCalendarResponseEntity responseEntity = new MeasureCalendarResponseEntity(
                    200, "유저의 해당 년, 월 평가들을 조회하였습니다.", measureCalendarResponses);
            return new ResponseEntity(responseEntity, HttpStatus.OK);
        } catch (IllegalStateException e) {
            BasicResponseEntity responseEntity = new BasicResponseEntity(400, "평가 조회 중 오류가 발생했습니다.");
            return new ResponseEntity(responseEntity, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "measure/{id}")
    public ResponseEntity getMeasureById(@PathVariable("id") Long measureId) {
        try {
            MeasureResponse measureResponse = measureService.findOne(measureId);
            List<MeasureResponse> measureResponseList = new ArrayList<>();
            measureResponseList.add(measureResponse);
            MeasureResponseEntity responseEntity = new MeasureResponseEntity(
                    200, "해당 measure ID를 가진 평가를 조회하였습니다.",
                    measureResponseList
            );
            return new ResponseEntity(responseEntity, HttpStatus.OK);
        } catch (IllegalStateException e) {
            BasicResponseEntity responseEntity = new BasicResponseEntity(400, "평가 조회 중 오류가 발생했습니다.");
            return new ResponseEntity(responseEntity, HttpStatus.BAD_REQUEST);
        }
    }

}
