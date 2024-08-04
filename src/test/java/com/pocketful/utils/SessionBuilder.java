package com.pocketful.utils;

import com.pocketful.entity.Account;
import com.pocketful.util.JsonWebToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class SessionBuilder {

    public static String build(Account account) {
        var authentication = new UsernamePasswordAuthenticationToken(account, null, account.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return JsonWebToken.generate(account.getEmail());
    }

}
