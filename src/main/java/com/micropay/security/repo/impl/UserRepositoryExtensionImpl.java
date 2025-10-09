package com.micropay.security.repo.impl;

import com.micropay.security.model.UserStatus;
import com.micropay.security.model.entity.User;
import com.micropay.security.repo.UserRepositoryExtension;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepositoryExtensionImpl implements UserRepositoryExtension {

    private final EntityManager entityManager;

    @Override
    public List<User> findUsers(UserStatus status, int limit, LocalDateTime cursorDate, String sortOrder) {
        StringBuilder jpql = new StringBuilder("SELECT u FROM User u WHERE 1=1");

        if (status != null) {
            jpql.append(" AND u.status = :status");
        }
        if (cursorDate != null) {
            if (sortOrder.equals("ASC")) {
                jpql.append(" AND u.createdAt > :cursorDate");
            } else {
                jpql.append(" AND u.createdAt < :cursorDate");
            }
        }
        jpql.append(" ORDER BY u.createdAt ").append(sortOrder);

        TypedQuery<User> query = entityManager.createQuery(jpql.toString(), User.class)
                .setMaxResults(limit + 1);

        if (status != null) {
            query.setParameter("status", status);
        }
        if (cursorDate != null) {
            query.setParameter("cursorDate", cursorDate);
        }
        return query.getResultList();
    }
}
