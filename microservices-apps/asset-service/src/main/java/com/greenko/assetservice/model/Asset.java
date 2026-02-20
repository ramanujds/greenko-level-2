package com.greenko.assetservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "assets")
public class Asset {

    @Id
    @Schema(description = "Asset Id", example = "AA2100000")
    private String assetId;
    @Schema(description = "Asset Name", example = "Asset 1")
    private String assetName;
    private String type;
    private LocalDate installedDate;
    private String location;
    private String status;

}
