package com.micropay.security.repo;

import com.micropay.security.model.UserStatus;
import com.micropay.security.model.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface UserRepositoryExtension {

    List<User> findUsers(UserStatus status, int limit, LocalDateTime cursorDate, String sortOrder);
}
