export interface PredictionRequest {
  crop:    string;
  market:  string;
  date:    string;
  userId:  string;       // ← added
}

export interface PredictionResponse {
  crop:                     string;
  market:                   string;
  date:                     string;
  predictedPricePer20kg:    number;
  predictedPricePerQuintal: number;
  unit:                     string;
  timestamp:                string;
}

export interface PredictionHistory {
  id:                       string;
  crop:                     string;
  market:                   string;
  date:                     string;
  predictedPricePer20kg:    number;
  predictedPricePerQuintal: number;
  createdAt:                string;
}