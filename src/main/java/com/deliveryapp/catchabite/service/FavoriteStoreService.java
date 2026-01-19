package com.deliveryapp.catchabite.service;

import java.util.List;

import com.deliveryapp.catchabite.dto.FavoriteStoreDTO;

public interface FavoriteStoreService {
    public FavoriteStoreDTO addFavorite(FavoriteStoreDTO dto);
    public List<FavoriteStoreDTO> getFavoritesByUser(Long appUserId);
    public void removeFavorite(Long favoriteId);
}
