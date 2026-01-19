package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.dto.AppUserDTO;

public interface AppUserService {

    public AppUserDTO createUser(AppUserDTO dto);
    AppUserDTO readUser(Long appUserId);
    AppUserDTO updateUser(Long appUserId, AppUserDTO dto);
    void deleteUser(Long appUserId);
}
