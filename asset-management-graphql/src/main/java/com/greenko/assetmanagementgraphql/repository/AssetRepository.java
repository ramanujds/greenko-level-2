package com.greenko.assetmanagementgraphql.repository;

import com.greenko.assetmanagementgraphql.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, String> {
    List<Asset> findByAssetName(String assetName);
    List<Asset> findByAssetNameContainingIgnoreCase(String assetName);
    List<Asset> findByType(String type);
    List<Asset> findByStatus(String status);
    List<Asset> findByLocation(String location);
}

