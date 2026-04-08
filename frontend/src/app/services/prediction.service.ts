import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  PredictionRequest,
  PredictionResponse,
  PredictionHistory
} from '../models/prediction.model';

@Injectable({ providedIn: 'root' })
export class PredictionService {

  private apiUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  predictPrice(request: PredictionRequest): Observable<PredictionResponse> {
    return this.http.post<PredictionResponse>(
      `${this.apiUrl}/predictPrice`, request
    );
  }

  // Pass userId as query param → GET /api/history?userId=...
  getHistory(userId: string): Observable<PredictionHistory[]> {
    const params = new HttpParams().set('userId', userId);
    return this.http.get<PredictionHistory[]>(
      `${this.apiUrl}/history`, { params }
    );
  }
}