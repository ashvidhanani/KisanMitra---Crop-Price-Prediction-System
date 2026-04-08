import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { PredictionService } from '../../services/prediction.service';
import { AuthService } from '../../services/auth.service';
import { PredictionHistory } from '../../models/prediction.model';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css']
})
export class HistoryComponent implements OnInit {

  history: PredictionHistory[] = [];
  loading  = true;
  error    = '';
  userName = '';

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

    this.userName = session.name ?? '';
    this.loadHistory(session.userId ?? '');
  }

  loadHistory(userId: string): void {
    if (!userId) {
      this.error   = 'Invalid session. Please log in again.';
      this.loading = false;
      return;
    }

    this.loading = true;
    this.error   = '';

    // GET /api/history?userId=...
    this.predictionService.getHistory(userId).subscribe({
      next: (data) => {
        this.history = data;
        this.loading = false;
      },
      error: () => {
        this.error   = 'Could not load history. Is the backend running?';
        this.loading = false;
      }
    });
  }

  refresh(): void {
    const session = this.authService.getSession();
    if (session) this.loadHistory(session.userId ?? '');
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

  formatTimestamp(ts: string): string {
    return new Date(ts).toLocaleString('en-IN', {
      dateStyle: 'medium', timeStyle: 'short'
    });
  }
}