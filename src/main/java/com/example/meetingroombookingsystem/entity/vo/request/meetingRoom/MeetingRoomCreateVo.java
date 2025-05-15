package com.example.meetingroombookingsystem.entity.vo.request.meetingRoom;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 创建会议室表单信息
 */
@Data
public class MeetingRoomCreateVo {
    @NotBlank(message = "会议室名称不能为空")
    @Size(min = 1, max = 100, message = "会议室名称长度必须在1到100个字符之间")
    private String roomName;

    @NotNull(message = "会议室类型不能为空")
    @Pattern(regexp = "^(classroom|round_table)$", message = "会议室类型只能是classroom或round_table")
    private String roomType;

    @NotNull(message = "座位数不能为空")
    @Min(value = 1, message = "座位数必须大于0")
    private Integer seatCount;

    @NotNull(message = "每小时价格不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "每小时价格必须大于0")
    private Double pricePerHour;

    @NotNull(message = "会议室状态不能为空")
    @Pattern(regexp = "^(available|locked|booked|in_use|under_maintenance)$", message = "会议室状态不合法")
    private String status;

    private String[] equipments;
}