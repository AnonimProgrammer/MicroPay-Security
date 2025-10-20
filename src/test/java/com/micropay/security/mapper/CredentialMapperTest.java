package com.micropay.security.mapper;

import com.micropay.security.model.entity.Credential;
import com.micropay.security.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CredentialMapperTest {

    private CredentialMapper mapper;
    private final static String PIN_HASH = "PIN_HASH";

    @BeforeEach
    void setUp() {
        mapper = new CredentialMapperImpl();
    }

    @Test
    void shouldBuildCredentialEntity_WhenUserAndPinHashProvided() {
        User user = new User();

        Credential credential = mapper.buildEntity(user, PIN_HASH);

        assertNotNull(credential);
        assertEquals(user, credential.getUser());
        assertEquals(PIN_HASH, credential.getPinHash());
    }

    @Test
    void shouldBuildCredentialEntity_WhenUserIsNullButPinHashProvided() {
        Credential credential = mapper.buildEntity(null, PIN_HASH);

        assertNotNull(credential);
        assertNull(credential.getUser());
        assertEquals(PIN_HASH, credential.getPinHash());
    }

    @Test
    void shouldBuildCredentialEntity_WhenUserProvidedButPinHashIsNull() {
        User user = new User();

        Credential credential = mapper.buildEntity(user, null);

        assertNotNull(credential);
        assertEquals(user, credential.getUser());
        assertNull(credential.getPinHash());
    }

    @Test
    void shouldReturnNull_WhenUserAndPinHashAreNull() {
        Credential credential = mapper.buildEntity(null, null);

        assertNull(credential);
    }
}
