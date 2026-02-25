import { Component, Input } from '@angular/core';
import { Asset } from '../../model/Asset';
import { CommonModule } from '@angular/common';
import { AssetsData } from '../../services/assets-data';
import { AssetsApiService } from '../../services/assets-api-service';

@Component({
  selector: 'app-asset-card',
  imports: [CommonModule],
  templateUrl: './asset-card.html',
  styleUrl: './asset-card.css',
})
export class AssetCard {

  @Input("asset")
  asset?:Asset;

  constructor(protected assetClinet:AssetsApiService, protected assetData:AssetsData) {}

  deleteAsset(id:any) {
    if(confirm("Are you sure you want to delete this asset?")) 
    this.assetClinet.deleteAsset(id).subscribe((response) => {
      console.log(response);
      alert("Asset deleted successfully");
      this.assetData.assets.set(this.assetData.assets().filter(asset => asset.assetId !== id));
    });
  }

}
