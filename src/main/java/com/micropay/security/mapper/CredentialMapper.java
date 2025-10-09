package com.micropay.security.mapper;

import com.micropay.security.model.entity.Credential;
import com.micropay.security.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CredentialMapper {

    @Mapping(source = "user", target = "user")
    @Mapping(source = "pinHash", target = "pinHash")
    Credential buildEntity(User user, String pinHash);

}
