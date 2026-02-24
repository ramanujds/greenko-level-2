import { Injectable } from '@angular/core';
import { Asset } from '../model/Asset';

@Injectable({
  providedIn: 'root',
})
export class AssetsData {

   assets:Array<Asset> = [
      {
        assetId: 'A001',
        assetName: 'Turbine 101',
        type: 'Turbine',
        installedDate: new Date('2022-01-15'),
        location: 'Bangalore'
      },
      {
        assetId: 'A002',
        assetName: 'Solar Panel 202',
        type: 'Solar Panel',
        installedDate: new Date('2023-03-10'),
        location: 'Hyderabad'
      },
      {
        assetId: 'A003',
        assetName: 'Battery Storage 303',
        type: 'Battery Storage',
        installedDate: new Date('2021-11-05'),
        location: 'Chennai'
      },
      {
        assetId: 'A004',
        assetName: 'Wind Sensor 404',
        type: 'Wind Sensor',
        installedDate: new Date('2022-07-20'),
        location: 'Pune'
      },
      {
        assetId: 'A005',
        assetName: 'Hydro Generator 505',
        type: 'Hydro Generator',
        installedDate: new Date('2023-02-28'),
        location: 'Kochi'
      }
    ] 

    deleteAsset(assetId:string) {
      this.assets = this.assets.filter(asset => asset.assetId !== assetId);
    }

  
}
