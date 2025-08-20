package com.tradexpert.service;

import com.tradexpert.model.CoinDTO;
import com.tradexpert.response.ApiResponse;

public interface ChatBotService {
    ApiResponse getCoinDetails(String coinName);

    CoinDTO getCoinByName(String coinName);

    String simpleChat(String prompt);
}
