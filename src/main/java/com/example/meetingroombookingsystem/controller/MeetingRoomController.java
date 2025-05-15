package com.example.meetingroombookingsystem.controller;


import com.example.meetingroombookingsystem.entity.vo.RestBean;
import com.example.meetingroombookingsystem.entity.vo.request.meetingRoom.MeetingRoomCreateVo;
import com.example.meetingroombookingsystem.entity.vo.request.meetingRoom.MeetingRoomFilterVo;
import com.example.meetingroombookingsystem.entity.vo.request.meetingRoom.MeetingRoomUpdateVo;
import com.example.meetingroombookingsystem.entity.vo.request.order.OrderMeetingRoomBookVo;
import com.example.meetingroombookingsystem.entity.vo.response.meetingRoom.MeetingRoomFliterResponseVo;
import com.example.meetingroombookingsystem.entity.vo.response.meetingRoom.MeetingRoomResponseVo;
import com.example.meetingroombookingsystem.service.MeetingRoomsService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@RestController
@RequestMapping("/meeting-rooms")
public class MeetingRoomController {
    @Resource
    MeetingRoomsService meetingRoomService;

    @PreAuthorize("hasAuthority('Create Meeting Room')") 
    @PostMapping("/create")
    public RestBean<Void> createMeetingRoom(@RequestBody @Valid MeetingRoomCreateVo meetingRoomCreateVo) {
        return this.messageHandle(() ->
                meetingRoomService.createMeetingRoom(meetingRoomCreateVo));
    }

    @PreAuthorize("hasAuthority('Delete Meeting Room')")
    @PostMapping("/delete")
    public RestBean<Void> deleteMeetingRoom(@RequestBody Map<String, String> requestBody) {
        String meetingRoomName = requestBody.get("meetingRoomName");
        return this.messageHandle(() ->
                meetingRoomService.deleteMeetingRoom(meetingRoomName));
    }

    @PreAuthorize("hasAuthority('View Meeting Room List')")
    @GetMapping("/list")
    public RestBean<List<MeetingRoomResponseVo>> listAllMeetingRooms() {
        return RestBean.success(meetingRoomService.listAllMeetingRooms());
    }

    @PreAuthorize("hasAuthority('Edit Meeting Room')")
    @PostMapping("/update")
    public RestBean<Void> updateMeetingRoom(@RequestBody @Valid MeetingRoomUpdateVo meetingRoomUpdateVo) {
        return this.messageHandle(() ->
                meetingRoomService.updateMeetingRoom(meetingRoomUpdateVo));
    }

    @PreAuthorize("hasAuthority('Update Meeting Room Status')")
    @PutMapping("/update-status")
    public RestBean<Void> updateMeetingRoomStatus(@RequestBody Map<String, String> requestBody) {
        String meetingRoomName = requestBody.get("meetingRoomName");
        String status = requestBody.get("status");
        return this.messageHandle(() ->
                meetingRoomService.updateMeetingRoomStatus(meetingRoomName, status));
    }

    @PreAuthorize("hasAuthority('Set and Modify Rental Price')")
    @PutMapping("/update-price")
    public RestBean<Void> updateMeetingRoomPrice(@RequestParam String meetingRoomName, @RequestParam Double pricePerHour) {
        return this.messageHandle(() ->
                meetingRoomService.updateMeetingRoomPrice(meetingRoomName, pricePerHour));
    }

    @PreAuthorize("hasAuthority('Fliter Meeting Room')")
    @PostMapping("/fliter")
    public RestBean<List<MeetingRoomFliterResponseVo>> filterMeetingRooms(@RequestBody MeetingRoomFilterVo filterRequest) {
        return RestBean.success(meetingRoomService.filterMeetingRooms(
                filterRequest.getStartTime(),
                filterRequest.getEndTime(),
                filterRequest.getAttendees(),
                filterRequest.getEquipment()
        ));

    }

    @PreAuthorize("hasAuthority('Book Meeting Room')")
    @PostMapping("/book")
    public RestBean<Void> bookMeetingRoom(@RequestBody OrderMeetingRoomBookVo vo) {
        return this.messageHandle(() ->
                meetingRoomService.bookMeetingRoom(vo.getMeetingRoomName(), vo.getCustomerName(),vo.getStartTime(),vo.getEndTime()));
    }


    @PreAuthorize("hasAnyAuthority('View Meeting Room Equipment')")
    @GetMapping("/equipment")
    public RestBean<List<String>> getMeetingRoomEquipment(@RequestParam String meetingRoomName) {
        return RestBean.success(meetingRoomService.getMeetingRoomEquipment(meetingRoomName));
    }



    /**
     * 针对于返回值为String作为错误信息的方法进行统一处理
     * @param action 具体操作
     * @return 响应结果
     * @param <T> 响应结果类型
     */
    private <T> RestBean<T> messageHandle(Supplier<String> action){
        String message = action.get();
        if(message == null)
            return RestBean.success();
        else
            return RestBean.failure(400, message);
    }
}
