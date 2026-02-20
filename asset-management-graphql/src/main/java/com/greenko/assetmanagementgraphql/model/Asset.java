package com.greenko.assetmanagementgraphql.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "assets")
public class Asset {

    @Id
    private String assetId;
    private String assetName;
    private String type;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate installedDate;
    private String location;
    private String status;

}
