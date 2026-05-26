"""
Testowy klient do sprawdzenia backendu dart-score.
Użycie: python test_client.py <ścieżka_do_zdjecia>
Przykład: python test_client.py image.png
"""

import sys
import json
import base64
import requests
from pathlib import Path

BASE_URL = "http://localhost:8000"


def test_health():
    print("=== Health check ===")
    r = requests.get(f"{BASE_URL}/health")
    data = r.json()
    print(f"Status: {data['status']}")
    print(f"Model załadowany: {data['model_loaded']}")
    print()


def test_score(image_path: str):
    path = Path(image_path)
    if not path.exists():
        print(f"Błąd: plik '{image_path}' nie istnieje")
        sys.exit(1)

    print(f"=== Wysyłam zdjęcie: {path.name} ===")

    with open(path, "rb") as f:
        r = requests.post(
            f"{BASE_URL}/score",
            files={"image": (path.name, f, "image/jpeg")}
        )

    if r.status_code != 200:
        print(f"Błąd {r.status_code}: {r.text}")
        sys.exit(1)

    data = r.json()

    print(f"Tryb MOCK: {data['mock_mode']}")
    print(f"Kalibracja OK: {data['calibration_ok']}")
    print(f"Liczba rzutek: {data['dart_count']}")
    print(f"Wyniki: {data['darts']}")
    print(f"Suma punktów: {data['total']}")
    print()

    print("=== Detekcje ===")
    for d in data["detections"]:
        print(f"  {d['class']:10} x={d['x']:6.1f} y={d['y']:6.1f} conf={d['confidence']:.2f}")
    print()

    # Zapisz annotowane zdjęcie
    if data.get("annotated_image"):
        out_path = path.stem + "_annotated.jpg"
        img_bytes = base64.b64decode(data["annotated_image"])
        with open(out_path, "wb") as f:
            f.write(img_bytes)
        print(f"Annotowane zdjęcie zapisane: {out_path}")


if __name__ == "__main__":
    test_health()

    if len(sys.argv) < 2:
        print("Podaj ścieżkę do zdjęcia jako argument:")
        print("  python test_client.py image.png")
        sys.exit(1)

    test_score(sys.argv[1])