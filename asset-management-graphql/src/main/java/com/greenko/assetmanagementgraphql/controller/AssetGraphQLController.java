package com.greenko.assetmanagementgraphql.controller;

import com.greenko.assetmanagementgraphql.model.Asset;
import com.greenko.assetmanagementgraphql.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AssetGraphQLController {

    private final AssetService assetService;


    @QueryMapping
    public List<Asset> getAllAssets() {
        return assetService.getAllAssets();
    }

    @QueryMapping
    public Asset getAssetById(@Argument String assetId) {
        return assetService.getAssetById(assetId).orElse(null);
    }

    @QueryMapping
    public List<Asset> getAssetsByName(@Argument String assetName) {
        return assetService.getAssetsByName(assetName);
    }

    @QueryMapping
    public List<Asset> searchAssets(@Argument String searchTerm) {
        return assetService.searchAssetsByName(searchTerm);
    }

    @QueryMapping
    public List<Asset> getAssetsByType(@Argument String type) {
        return assetService.getAssetsByType(type);
    }

    @QueryMapping
    public List<Asset> getAssetsByStatus(@Argument String status) {
        return assetService.getAssetsByStatus(status);
    }

    @QueryMapping
    public List<Asset> getAssetsByLocation(@Argument String location) {
        return assetService.getAssetsByLocation(location);
    }
    

    @MutationMapping
    public Asset createAsset(
            @Argument String assetId,
            @Argument String assetName,
            @Argument String type,
            @Argument(name = "installedDate") String installedDateStr,
            @Argument String location,
            @Argument String status) {

        Asset asset = new Asset();
        asset.setAssetId(assetId);
        asset.setAssetName(assetName);
        asset.setType(type);
        asset.setInstalledDate(java.time.LocalDate.parse(installedDateStr));
        asset.setLocation(location);
        asset.setStatus(status);

        return assetService.saveAsset(asset);
    }

    @MutationMapping
    public Asset updateAsset(
            @Argument String assetId,
            @Argument String assetName,
            @Argument String type,
            @Argument(name = "installedDate") String installedDateStr,
            @Argument String location,
            @Argument String status) {

        Asset assetDetails = new Asset();
        assetDetails.setAssetName(assetName);
        assetDetails.setType(type);
        if (installedDateStr != null) {
            assetDetails.setInstalledDate(java.time.LocalDate.parse(installedDateStr));
        }
        assetDetails.setLocation(location);
        assetDetails.setStatus(status);

        return assetService.updateAsset(assetId, assetDetails).orElse(null);
    }

    @MutationMapping
    public boolean deleteAsset(@Argument String assetId) {
        return assetService.deleteAsset(assetId);
    }
}


