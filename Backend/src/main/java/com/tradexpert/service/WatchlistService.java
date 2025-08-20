package com.tradexpert.service;

import com.tradexpert.model.Coin;
import com.tradexpert.model.User;
import com.tradexpert.model.Watchlist;

public interface WatchlistService {

    Watchlist findUserWatchlist(Long userId) throws Exception;

    Watchlist createWatchList(User user);

    Watchlist findById(Long id) throws Exception;

    Coin addItemToWatchlist(Coin coin,User user) throws Exception;
}
