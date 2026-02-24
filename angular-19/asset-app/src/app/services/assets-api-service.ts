import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Asset } from '../model/Asset';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AssetsApiService {

  readonly baseUrl = 'http://localhost:8081/api/assets';

  constructor(private assetClient: HttpClient) {}

  getAssets() : Observable<Asset[]> {
    return this.assetClient.get<Asset[]>(this.baseUrl);
  }

  getAssetById(id: number) : Observable<Asset> {
    return this.assetClient.get<Asset>(`${this.baseUrl}/${id}`);
  }

  createAsset(asset: Asset) : Observable<Asset> {
    return this.assetClient.post<Asset>(this.baseUrl, asset);
  }

  deleteAsset(id: string) : Observable<void> {
    return this.assetClient.delete<void>(`${this.baseUrl}/${id}`);
  }
}
