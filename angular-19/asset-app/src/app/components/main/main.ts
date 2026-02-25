import { Component } from '@angular/core';
import { Assets } from "../assets/assets";
import { AssetForm } from "../asset-form/asset-form";

@Component({
  selector: 'app-main',
  imports: [Assets, AssetForm],
  templateUrl: './main.html',
  styleUrl: './main.css',
})
export class Main {

}
