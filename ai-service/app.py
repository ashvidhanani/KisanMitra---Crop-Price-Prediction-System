from flask import Flask, request, jsonify
from flask_cors import CORS
import pickle
import pandas as pd
import os
import logging

app = Flask(__name__)
CORS(app)

# Logging setup
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Paths
MODEL_PATH = os.path.join(os.path.dirname(__file__), 'models', 'crop_price_model.pkl')
ENCODER_PATH = os.path.join(os.path.dirname(__file__), 'models', 'encoders.pkl')

model = None
encoders = None

# 🔥 Load model
def load_model():
    global model, encoders
    try:
        with open(MODEL_PATH, 'rb') as f:
            model = pickle.load(f)
        with open(ENCODER_PATH, 'rb') as f:
            encoders = pickle.load(f)
        logger.info("✅ Model and encoders loaded successfully.")
    except FileNotFoundError:
        logger.error("❌ Model files not found. Run train_model.py first.")

load_model()

# 🔥 ROOT endpoint (no more 404)
@app.route('/', methods=['GET'])
def home():
    return jsonify({
        "message": "Flask AI Service Running 🚀",
        "status": "OK"
    })

# 🔥 Health check
@app.route('/health', methods=['GET'])
def health():
    return jsonify({
        "status": "ok",
        "model_loaded": model is not None
    })

# 🔥 Prediction endpoint
@app.route('/predict', methods=['POST'])
def predict():
    try:
        data = request.get_json()

        if not data:
            return jsonify({"error": "No JSON data received"}), 400

        crop = data.get('crop')
        market = data.get('market')
        date_str = data.get('date')

        if not all([crop, market, date_str]):
            return jsonify({"error": "Missing fields: crop, market, date"}), 400

        if model is None or encoders is None:
            return jsonify({"error": "Model not loaded"}), 503

        # Date processing
        date = pd.to_datetime(date_str)
        month = date.month
        year = date.year
        day = date.day
        quarter = date.quarter
        week = int(date.isocalendar().week)

        # Encoding
        try:
            crop_enc = encoders['crop'].transform([crop])[0]
            market_enc = encoders['market'].transform([market])[0]
        except Exception:
            return jsonify({"error": "Invalid crop or market value"}), 400

        # Feature creation
        features = pd.DataFrame(
            [[crop_enc, market_enc, month, year, day, quarter, week]],
            columns=['crop', 'market', 'month', 'year', 'day', 'quarter', 'week']
        )

        # Prediction
        price_per_quintal = float(model.predict(features)[0])
        price_per_20kg = round(price_per_quintal / 5, 2)

        return jsonify({
            "crop": crop,
            "market": market,
            "date": date_str,
            "predicted_price_per_20kg": price_per_20kg,
            "predicted_price_per_quintal": round(price_per_quintal, 2),
            "unit": "INR per 20 kg"
        })

    except Exception as e:
        logger.exception("❌ Prediction error")
        return jsonify({"error": str(e)}), 500


# 🔥 Run app
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)