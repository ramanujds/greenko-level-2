package com.greenko.assetmanagementgraphql.service;

import com.greenko.assetmanagementgraphql.model.Asset;
import com.greenko.assetmanagementgraphql.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;

    // Fetch all assets
    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    // Fetch asset by ID
    public Optional<Asset> getAssetById(String assetId) {
        return assetRepository.findById(assetId);
    }

    // Fetch assets by exact name
    public List<Asset> getAssetsByName(String assetName) {
        return assetRepository.findByAssetName(assetName);
    }

    // Fetch assets by name (case-insensitive search)
    public List<Asset> searchAssetsByName(String assetName) {
        return assetRepository.findByAssetNameContainingIgnoreCase(assetName);
    }

    // Fetch assets by type
    public List<Asset> getAssetsByType(String type) {
        return assetRepository.findByType(type);
    }

    // Fetch assets by status
    public List<Asset> getAssetsByStatus(String status) {
        return assetRepository.findByStatus(status);
    }

    // Fetch assets by location
    public List<Asset> getAssetsByLocation(String location) {
        return assetRepository.findByLocation(location);
    }

    // Save or create new asset
    public Asset saveAsset(Asset asset) {
        return assetRepository.save(asset);
    }

    // Update existing asset
    public Optional<Asset> updateAsset(String assetId, Asset assetDetails) {
        return assetRepository.findById(assetId).map(asset -> {
            if (assetDetails.getAssetName() != null) {
                asset.setAssetName(assetDetails.getAssetName());
            }
            if (assetDetails.getType() != null) {
                asset.setType(assetDetails.getType());
            }
            if (assetDetails.getInstalledDate() != null) {
                asset.setInstalledDate(assetDetails.getInstalledDate());
            }
            if (assetDetails.getLocation() != null) {
                asset.setLocation(assetDetails.getLocation());
            }
            if (assetDetails.getStatus() != null) {
                asset.setStatus(assetDetails.getStatus());
            }
            return assetRepository.save(asset);
        });
    }

    // Delete asset by ID
    public boolean deleteAsset(String assetId) {
        if (assetRepository.existsById(assetId)) {
            assetRepository.deleteById(assetId);
            return true;
        }
        return false;
    }
}

