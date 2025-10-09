package com.micropay.security.mapper;

import com.micropay.security.dto.request.RegisterRequest;
import com.micropay.security.dto.response.UserResponse;
import com.micropay.security.model.UserModel;
import com.micropay.security.model.entity.Role;
import com.micropay.security.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "entity.role.role", target = "role")
    UserModel toModel(User entity);

    @Mapping(source = "request.phoneNumber", target = "phoneNumber")
    @Mapping(source = "request.fullName", target = "fullName")
    @Mapping(source = "request.email", target = "email")
    @Mapping(source = "role", target = "role")
    User buildEntity(RegisterRequest request, Role role);

    UserResponse toResponse(User user);

}
