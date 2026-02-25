import { Injectable, signal } from '@angular/core';
import { Asset } from '../model/Asset';

@Injectable({
  providedIn: 'root',
})
export class AssetsData {

   assets = signal<Asset[]>([]);

  
}
