package com.example.librarymanagement.controller;

import com.example.librarymanagement.dtos.requests.user.SignupRequest;
import com.example.librarymanagement.entities.User;
import com.example.librarymanagement.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private SignupRequest request;
    private User response;

    @BeforeEach
    void initData() {
        request = SignupRequest.builder()
                .email("ducdam1@gmail.com")
                .fullName("Đàm Đức")
                .username("ducdam1")
                .password("220903")
                .confirmPassword("220903")
                .phoneNumber("0889326357")
                .cid("034203014056")
                .address("Thái Bình")
                .build();

        response = User.builder()
                .email("ducdam1@gmail.com")
                .fullName("Đàm Đức")
                .username("ducdam1")
                .password("220903")
                .phoneNumber("0889326357")
                .cid("034203014056")
                .address("Thái Bình")
                .build();
    }
}
