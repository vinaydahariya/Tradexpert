package com.tradexpert.repository;

import com.tradexpert.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetsRepository extends JpaRepository<Asset,Long> {
   public List<Asset> findByUserId(Long userId);

   Asset findByUserIdAndCoinId(Long userId, String coinId);

   Asset findByIdAndUserId(Long assetId, Long userId);

//   Optional<Assets> findByUserIdAndSymbolAndPortfolioId(Long userId,String symbol, Long portfolioId);
}
