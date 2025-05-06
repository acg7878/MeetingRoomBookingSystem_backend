package com.example.meetingroombookingsystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@MapperScan("com.example.meetingroombookingsystem.mapper")
public class MeetingRoomBookingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeetingRoomBookingSystemApplication.class, args);
    }

}
