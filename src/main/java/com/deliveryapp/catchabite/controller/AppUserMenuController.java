package com.deliveryapp.catchabite.controller;

import com.deliveryapp.catchabite.dto.UserMenuDetailDTO;
import com.deliveryapp.catchabite.service.UserMenuCategoryService;
import com.deliveryapp.catchabite.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/appuser/menus")
@RequiredArgsConstructor
public class AppUserMenuController {

    private final UserMenuCategoryService userMenuCategoryService;

    @GetMapping("/{menuId}")
    public ResponseEntity<ApiResponse<UserMenuDetailDTO>> getMenuDetail(@PathVariable Long menuId) {
        UserMenuDetailDTO data = userMenuCategoryService.getMenuDetail(menuId);
        return ResponseEntity.ok(ApiResponse.ok(data));
    }
}