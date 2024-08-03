package com.pocketful.util;

import com.pocketful.entity.Account;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class SessionContext {
    public static Account get() {
        return (Account) SecurityContextHolder.getContext()
            .getAuthentication()
            .getPrincipal();
    }
}
