package io.github.hoooosi.imagehosting.aop.auth.common;

public interface Handler {
    ID getID();

    Long handlePermission(Long id);
}
