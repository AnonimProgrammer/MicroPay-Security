package com.micropay.security.mapper;

import com.micropay.security.model.UserModel;
import com.micropay.security.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements Mapper<User, UserModel> {

    @Override
    public UserModel map(User source) {
        return new UserModel.Builder()
                .phoneNumber(source.getPhoneNumber())
                .email(source.getEmail())
                .fullName(source.getFullName())
                .status(source.getStatus())
                .createdAt(source.getCreatedAt())
                .updatedAt(source.getUpdatedAt())
                .build();
    }

}
