"""
train_model.py
Run: python train_model.py
Generates: models/crop_price_model.pkl  and  models/encoders.pkl
"""

import pandas as pd
import numpy as np
from sklearn.ensemble import RandomForestRegressor
from sklearn.preprocessing import LabelEncoder
from sklearn.model_selection import train_test_split
from sklearn.metrics import mean_absolute_error, mean_squared_error, r2_score
import pickle
import os
import logging

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

CSV_PATH  = os.path.join(os.path.dirname(__file__), '..', 'dataset', 'crop_prices.csv')
MODEL_DIR = os.path.join(os.path.dirname(__file__), 'models')
os.makedirs(MODEL_DIR, exist_ok=True)


def load_dataset():
    if os.path.exists(CSV_PATH):
        logger.info(f"Loading dataset from {CSV_PATH}")
        return pd.read_csv(CSV_PATH)
    raise FileNotFoundError(f"Dataset not found at {CSV_PATH}")


def engineer_features(df):
    df = df.copy()
    df['date']    = pd.to_datetime(df['date'])
    df['month']   = df['date'].dt.month
    df['year']    = df['date'].dt.year
    df['day']     = df['date'].dt.day
    df['quarter'] = df['date'].dt.quarter
    df['week']    = df['date'].dt.isocalendar().week.astype(int)

    encoders = {}
    for col in ['crop', 'market']:
        le = LabelEncoder()
        df[col] = le.fit_transform(df[col].astype(str))
        encoders[col] = le

    return df, encoders


def train():
    df = load_dataset()
    logger.info(f"Dataset shape: {df.shape}")

    df, encoders = engineer_features(df)

    FEATURES = ['crop', 'market', 'month', 'year', 'day', 'quarter', 'week']
    TARGET   = 'price'

    X = df[FEATURES]
    y = df[TARGET]

    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.2, random_state=42
    )
    logger.info(f"Train: {len(X_train)} rows  |  Test: {len(X_test)} rows")

    model = RandomForestRegressor(
        n_estimators=200,
        max_depth=15,
        min_samples_leaf=3,
        random_state=42,
        n_jobs=-1
    )
    model.fit(X_train, y_train)

    y_pred = model.predict(X_test)
    logger.info("─── Model Evaluation ───")
    logger.info(f"  MAE  : ₹{mean_absolute_error(y_test, y_pred):.2f}")
    logger.info(f"  RMSE : ₹{np.sqrt(mean_squared_error(y_test, y_pred)):.2f}")
    logger.info(f"  R²   : {r2_score(y_test, y_pred):.4f}")

    importances = pd.Series(model.feature_importances_, index=FEATURES).sort_values(ascending=False)
    logger.info(f"\nFeature Importances:\n{importances}")

    model_path   = os.path.join(MODEL_DIR, 'crop_price_model.pkl')
    encoder_path = os.path.join(MODEL_DIR, 'encoders.pkl')

    with open(model_path, 'wb') as f:
        pickle.dump(model, f)
    with open(encoder_path, 'wb') as f:
        pickle.dump(encoders, f)

    logger.info(f"\nModel saved   → {model_path}")
    logger.info(f"Encoders saved → {encoder_path}")
    logger.info("✅ Done! Now run: python app.py")


if __name__ == '__main__':
    train()