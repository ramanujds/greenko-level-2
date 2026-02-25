import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AssetsApiService } from '../../services/assets-api-service';
import { AssetsData } from '../../services/assets-data';

@Component({
  selector: 'app-asset-form',
  imports: [ReactiveFormsModule,CommonModule],
  templateUrl: './asset-form.html',
  styleUrl: './asset-form.css',
})
export class AssetForm implements OnInit {

  constructor(private formBuilder:FormBuilder, private assetApiService:AssetsApiService, private assetData:AssetsData) {}

  assetForm?:any;
  assets:any[] = [];

  ngOnInit(): void {
    this.assets = this.assetData.assets();
    this.assetForm = this.formBuilder.group({
    assetName: ['',[Validators.required, Validators.minLength(3)]],
    type: [''],
    installedDate: [''],
    location: ['']
  });
  }

  registerAsset(){
    let asset = this.assetForm.value;
    this.assetApiService.createAsset(asset).subscribe((response)=>{
      alert("Asset created successfully");
      this.assets.push(response);
      this.assetData.assets.set(this.assets);
    });
  }
  
  


}
