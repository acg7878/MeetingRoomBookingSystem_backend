package com.example.meetingroombookingsystem.controller;


import com.example.meetingroombookingsystem.entity.vo.RestBean;
import com.example.meetingroombookingsystem.entity.vo.request.meetingRoom.MeetingRoomCreateVo;
import com.example.meetingroombookingsystem.entity.vo.request.meetingRoom.MeetingRoomUpdateVo;
import com.example.meetingroombookingsystem.entity.vo.response.meetingRoom.MeetingRoomResponseVo;
import com.example.meetingroombookingsystem.service.MeetingRoomsService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public RestBean<Void> deleteMeetingRoom(String meetingRoomName) {
        return this.messageHandle(() ->
                meetingRoomService.deleteMeetingRoom(meetingRoomName));
    }

    @GetMapping("/list")
    public RestBean<List<MeetingRoomResponseVo>> listAllMeetingRooms() {
        return RestBean.success(meetingRoomService.listAllMeetingRooms());
    }

    @PreAuthorize("hasAuthority('Edit Meeting Room')")
    @PutMapping("/update")
    public RestBean<Void> updateMeetingRoom(@RequestBody @Valid MeetingRoomUpdateVo meetingRoomUpdateVo) {
        return this.messageHandle(() ->
                meetingRoomService.updateMeetingRoom(meetingRoomUpdateVo));
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
