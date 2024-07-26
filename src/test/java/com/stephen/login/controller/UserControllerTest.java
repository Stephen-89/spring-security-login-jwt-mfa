package com.stephen.login.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stephen.login.dto.UserDto;
import com.stephen.login.entity.User;
import com.stephen.login.util.JwtTokenUtil;
import com.stephen.login.controller.UserController;
import com.stephen.login.service.UserRolesDetailsService;
import com.stephen.login.service.UserService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @MockBean
    private UserService userService;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private UserRolesDetailsService userRolesDetailsService;

	@Autowired
    private MockMvc mockMvc;    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    @WithMockUser(roles = "USER")
    void testReadUser_Success() throws Exception {

        User user = mockUser();

        Mockito.when(userService.getLoggedInUser()).thenReturn(user);

        mockMvc.perform(get("/profile")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("testuser"));
        
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUpdateUser_Success() throws Exception {
    	
    	UserDto userDto = mockUserDto();
        userDto.setUsername("updateduser");
        userDto.setPassword("newpassword");

        User user = mockUser();
        user.setUsername("updateduser");

        Mockito.when(userService.updateUser(any(UserDto.class))).thenReturn(user);

        mockMvc.perform(put("/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("updateduser"));
        
    }
    

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteUser_Success() throws Exception {
    	
        mockMvc.perform(delete("/deactivate")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        
    }

	private UserDto mockUserDto() {
		UserDto userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setPassword("password");
		return userDto;
	}

	private User mockUser() {
		User user = new User();
        user.setUsername("testuser");
		return user;
	}

}
