FROM python:3.11-slim

WORKDIR /app

# System deps dla OpenCV
RUN apt-get update && apt-get install -y \
    libglib2.0-0 libsm6 libxrender1 libxext6 \
    && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

# Skopiuj wytrenowany model dart-sense tutaj
# COPY model/dart_sense.pt model/dart_sense.pt

EXPOSE 8000

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
