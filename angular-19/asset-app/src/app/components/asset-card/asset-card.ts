import { Component, Input } from '@angular/core';
import { Asset } from '../../model/Asset';
import { CommonModule } from '@angular/common';
import { AssetsData } from '../../services/assets-data';

@Component({
  selector: 'app-asset-card',
  imports: [CommonModule],
  templateUrl: './asset-card.html',
  styleUrl: './asset-card.css',
})
export class AssetCard {

  @Input("asset")
  asset?:Asset;

  constructor(protected assetData:AssetsData) {}

  deleteAsset(id:any) {
    if(confirm("Are you sure you want to delete this asset?")) 
    this.assetData.deleteAsset(id);
  }

}
