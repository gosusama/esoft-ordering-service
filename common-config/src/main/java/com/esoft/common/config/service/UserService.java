package com.esoft.common.config.service;

import com.esoft.common.config.entity.User;

import java.util.List;

public interface UserService {
    User getCurrentUser();

    List<String> getCurrentUserRoles();

    Boolean isAdmin();
}
