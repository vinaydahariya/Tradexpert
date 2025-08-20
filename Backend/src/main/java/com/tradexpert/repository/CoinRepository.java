package com.tradexpert.repository;

import com.tradexpert.model.Coin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinRepository extends JpaRepository<Coin,String> {
}
