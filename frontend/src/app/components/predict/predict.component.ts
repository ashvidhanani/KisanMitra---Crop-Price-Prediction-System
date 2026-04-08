import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PredictionService } from '../../services/prediction.service';
import { AuthService } from '../../services/auth.service';
import { PredictionRequest, PredictionResponse } from '../../models/prediction.model';

@Component({
  selector: 'app-predict',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './predict.component.html',
  styleUrls: ['./predict.component.css']
})
export class PredictComponent implements OnInit {

  crops = [
    { group: 'Cereals',    items: ['Wheat','Rice','Maize','Barley','Jowar','Bajra'] },
    { group: 'Pulses',     items: ['Lentil (Masoor)','Chickpea (Chana)','Pigeon Pea (Tur)','Mung Bean (Moong)','Urad Dal'] },
    { group: 'Vegetables', items: ['Tomato','Onion','Potato','Brinjal','Okra (Bhindi)','Cauliflower'] },
    { group: 'Cash Crops', items: ['Cotton','Sugarcane','Groundnut','Soybean','Mustard'] },
    { group: 'Fruits',     items: ['Mango','Banana','Grapes','Pomegranate'] },
  ];

  markets = [
    { group: 'Gujarat',       items: ['Surat','Ahmedabad','Rajkot','Vadodara','Anand'] },
    { group: 'Maharashtra',   items: ['Mumbai','Pune','Nagpur','Nashik'] },
    { group: 'Punjab',        items: ['Ludhiana','Amritsar','Chandigarh'] },
    { group: 'Uttar Pradesh', items: ['Lucknow','Kanpur','Agra','Varanasi'] },
    { group: 'Karnataka',     items: ['Bengaluru','Mysuru','Hubli'] },
    { group: 'Rajasthan',     items: ['Jaipur','Jodhpur','Kota'] },
  ];

  request: PredictionRequest = { crop: '', market: '', date: '', userId: '' };
  result:  PredictionResponse | null = null;
  loading  = false;
  error    = '';
  trendData: { label: string; height: number; isCurrent: boolean }[] = [];

  private MONTHS = ['Jan','Feb','Mar','Apr','May','Jun',
                    'Jul','Aug','Sep','Oct','Nov','Dec'];

  constructor(
    private predictionService: PredictionService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Guard — redirect to login if not authenticated
    const session = this.authService.getSession();
    if (!session) {
      this.router.navigate(['/login']);
      return;
    }

    // Attach userId from session
    this.request.userId = session.userId ?? '';
    this.request.date   = new Date().toISOString().split('T')[0];
  }

  predict(): void {
    this.error = '';

    if (!this.request.crop || !this.request.market || !this.request.date) {
      this.error = 'Please fill in all fields before predicting.';
      return;
    }

    if (!this.request.userId) {
      this.error = 'Session expired. Please log in again.';
      this.router.navigate(['/login']);
      return;
    }

    this.loading = true;
    this.result  = null;

    this.predictionService.predictPrice(this.request).subscribe({
      next: (res) => {
        this.result = res;
        this.buildTrend(res.predictedPricePer20kg);
        this.loading = false;
      },
      error: (err) => {
        this.error = "⚠️ Prediction not available for this crop/market. Try another selection.";
        this.loading = false;
      }
    });
  }

  private buildTrend(basePrice: number): void {
    const deltas = [-3, 2, 0, 5, 1, -2];
    const base   = new Date(this.request.date);
    const prices = deltas.map(d => basePrice * (1 + d / 100));
    const max    = Math.max(...prices);

    this.trendData = deltas.map((_, i) => {
      const m = new Date(base);
      m.setMonth(m.getMonth() + i);
      return {
        label:     this.MONTHS[m.getMonth()],
        height:    Math.round((prices[i] / max) * 100),
        isCurrent: i === 0
      };
    });
  }

  formatCurrency(value: number): string {
    return '₹' + value.toLocaleString('en-IN', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    });
  }

  formatDate(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString('en-IN', {
      day: 'numeric', month: 'short', year: 'numeric'
    });
  }
}