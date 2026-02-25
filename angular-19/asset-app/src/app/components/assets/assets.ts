import { Component, OnInit, signal } from '@angular/core';
import { Asset } from '../../model/Asset';
import { AssetCard } from "../asset-card/asset-card";
import { CommonModule } from '@angular/common';
import { AssetsData } from '../../services/assets-data';
import { AssetsApiService } from '../../services/assets-api-service';

@Component({
  selector: 'app-assets',
  imports: [AssetCard, CommonModule],
  templateUrl: './assets.html',
  styleUrl: './assets.css',
})
export class Assets implements OnInit {

  constructor(protected assetClient:AssetsApiService, protected assetData:AssetsData) {}

    

  ngOnInit(): void {
    this.assetClient.getAssets().subscribe((response) => {
      console.log(response);
      this.assetData.assets.set(response);
    });
  }
}
