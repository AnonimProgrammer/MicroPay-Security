package com.micropay.security.mapper;

import com.micropay.security.dto.request.RegisterRequest;
import com.micropay.security.dto.response.UserResponse;
import com.micropay.security.model.RoleType;
import com.micropay.security.model.UserModel;
import com.micropay.security.model.UserStatus;
import com.micropay.security.model.entity.Role;
import com.micropay.security.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserMapperImpl();
    }

    @Test
    void shouldMapUserEntityToUserModel() {
        Role role = new Role();
        role.setRole(RoleType.ADMIN);

        User user = new User();
        user.setRole(role);
        user.setPhoneNumber("phoneNumber");
        user.setFullName("Omar Ismailov");
        user.setEmail("omar@icloud.com");
        user.setStatus(UserStatus.ACTIVE);

        UserModel model = mapper.toModel(user);

        assertNotNull(model);
        assertEquals(RoleType.ADMIN, model.getRole());
        assertEquals("phoneNumber", model.getPhoneNumber());
        assertEquals("Omar Ismailov", model.getFullName());
        assertEquals("omar@icloud.com", model.getEmail());
    }

    @Test
    void shouldReturnNull_WhenUserEntityIsNull() {
        assertNull(mapper.toModel(null));
    }

    @Test
    void shouldBuildUserEntityFromRegisterRequestAndRole() {
        RegisterRequest request = new RegisterRequest(
                "phoneNumber", "Omar Ismailov", "omar@icloud.com", "111111"
        );
        Role role = new Role();
        role.setRole(RoleType.USER);

        User user = mapper.buildEntity(request, role);

        assertNotNull(user);
        assertEquals("Omar Ismailov", user.getFullName());
        assertEquals("phoneNumber", user.getPhoneNumber());
        assertEquals("omar@icloud.com", user.getEmail());
        assertEquals(role, user.getRole());
    }

    @Test
    void shouldHandleNullsInBuildEntity() {
        Role role = new Role();
        role.setRole(RoleType.USER);

        User user = mapper.buildEntity(null, role);
        assertNotNull(user);
        assertEquals(role, user.getRole());
        assertNull(user.getEmail());
        assertNull(user.getFullName());

        assertNull(mapper.buildEntity(null, null));
    }

    @Test
    void shouldMapUserToUserResponse() {
        User user = new User();
        user.setPhoneNumber("phoneNumber");
        user.setFullName("Omar Ismailov");
        user.setEmail("omar@icloud.com");

        UserResponse response = mapper.toResponse(user);

        assertNotNull(response);
        assertEquals("phoneNumber", response.phoneNumber());
        assertEquals("Omar Ismailov", response.fullName());
        assertEquals("omar@icloud.com", response.email());
    }

    @Test
    void shouldReturnNull_WhenUserIsNullInToResponse() {
        assertNull(mapper.toResponse(null));
    }
}
