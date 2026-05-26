# 🎯 Dart Score Backend

FastAPI backend do rozpoznawania wyników w darts ze zdjęcia.

## Stack
- **FastAPI** — REST API
- **YOLOv8** (dart-sense) — detekcja rzutek i kalibracja tarczy
- **OpenCV** — transformacja perspektywy + adnotacje

## Setup

### 1. Klonuj dart-sense i pobierz model

```bash
git clone https://github.com/bnww/dart-sense.git
mkdir -p model
# Skopiuj wytrenowany model (best.pt z dart-sense) do:
cp dart-sense/runs/detect/train/weights/best.pt model/dart_sense.pt
```

> Jeśli dart-sense nie ma gotowego modelu w repo, musisz go wytrenować
> albo napisać do autora (bnww) — projekt ma licencję non-commercial.

### 2. Instalacja

```bash
pip install -r requirements.txt
```

### 3. Uruchomienie

```bash
uvicorn app.main:app --reload --port 8000
```

Lub przez Docker:

```bash
docker build -t dart-backend .
docker run -p 8000:8000 dart-backend
```

## API

### `GET /health`
Sprawdzenie czy serwer działa i czy model jest załadowany.

```json
{ "status": "ok", "model_loaded": true }
```

### `POST /score`
Wysyłasz zdjęcie tarczy, dostajesz wyniki.

**Request:**
```
Content-Type: multipart/form-data
Body: image=<plik.jpg>
```

**Response:**
```json
{
  "darts": ["T20", "D16", "1"],
  "total": 93,
  "dart_count": 3,
  "calibration_ok": true,
  "mock_mode": false,
  "annotated_image": "<base64 jpg>",
  "detections": [
    { "class": "dart", "x": 412.3, "y": 308.1, "confidence": 0.94 },
    { "class": "calib_20", "x": 400.0, "y": 50.2, "confidence": 0.99 }
  ]
}
```

### Przykład — Kotlin (Android)

```kotlin
val client = OkHttpClient()

val body = MultipartBody.Builder()
    .setType(MultipartBody.FORM)
    .addFormDataPart(
        "image", "dart.jpg",
        RequestBody.create("image/jpeg".toMediaType(), imageBytes)
    )
    .build()

val request = Request.Builder()
    .url("https://twoj-backend.com/score")
    .post(body)
    .build()

client.newCall(request).execute().use { response ->
    val json = JSONObject(response.body!!.string())
    val darts = json.getJSONArray("darts")
    val total = json.getInt("total")
    val annotatedBase64 = json.getString("annotated_image")
    // Dekoduj base64 → Bitmap i wyświetl w ImageView
}
```

## Tryb MOCK

Jeśli model `.pt` nie jest załadowany, API działa w trybie MOCK —
zwraca przykładowe detekcje. Przydatne do testowania apki mobilnej
zanim model będzie gotowy.

`"mock_mode": true` w response informuje apkę o tym trybie.

## Struktura projektu

```
dart-backend/
├── app/
│   ├── main.py        # FastAPI endpointy
│   └── scorer.py      # Logika YOLO + homografia + scoring
├── model/
│   └── dart_sense.pt  # ← skopiuj tutaj model z dart-sense
├── requirements.txt
├── Dockerfile
└── README.md
```

## Jak działa scoring

1. **YOLO** wykrywa pozycje rzutek (pikselowe XY) + 4 punkty kalibracyjne tarczy
2. **Homografia** (cv2.findHomography) transformuje obraz do znormalizowanego układu tarczy
3. **Geometria** — dla każdej rzutki oblicza odległość od środka i kąt → wynik (T20, D16, ...)
4. **Adnotacje** — rysuje zaznaczenia na obrazie, enkoduje do base64

Kalibracja przez 4 punkty eliminuje wpływ kąta kamery i dystansu.
