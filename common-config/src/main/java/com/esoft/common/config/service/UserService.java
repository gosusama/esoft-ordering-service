package com.esoft.common.config.service;

import com.esoft.common.config.entity.User;

import java.util.List;

public interface UserService {
    User getCurrentUser();

    Boolean isAdmin();
}
