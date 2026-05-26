"""
Wrapper na dart-sense / YOLOv8 model.

Pipeline:
1. Wczytaj obraz z bajtów
2. Puść przez YOLO → bounding boxy rzutek + 4 punkty kalibracyjne
3. Oblicz środek i skalę tarczy z punktów kalibracyjnych
4. Zmapuj współrzędne rzutek na pole tarczy → wynik słowny (T20, D16, ...)
5. Narysuj adnotacje na obrazie
6. Zwróć JSON z wynikami + base64 annotowanego zdjęcia
"""

import cv2
import numpy as np
import base64
import math
import logging
from pathlib import Path
from typing import Optional

logger = logging.getLogger(__name__)

# Standardowe wymiary tarczy (mm) — wg BDO/WDF spec
BOARD_RADIUS_MM = 170.0
DOUBLE_OUTER_MM = 170.0
DOUBLE_INNER_MM = 162.0
TRIPLE_OUTER_MM = 107.0
TRIPLE_INNER_MM = 99.0
BULL_OUTER_MM = 31.8 / 2
BULL_INNER_MM = 12.7 / 2

# Segmenty tarczy clockwise od góry
SEGMENTS = [20, 1, 18, 4, 13, 6, 10, 15, 2, 17, 3, 19, 7, 16, 8, 11, 14, 9, 12, 5]
SEG_ANGLE = (2 * math.pi) / 20


def score_from_coords(x: float, y: float) -> str:
    cx, cy = 400.0, 400.0
    dx = x - cx
    dy = y - cy

    dist = math.sqrt(dx * dx + dy * dy)

    if dist <= BULL_INNER_MM:
        return "50"
    if dist <= BULL_OUTER_MM:
        return "25"
    if dist > DOUBLE_OUTER_MM:
        return "0"

    if dist <= TRIPLE_INNER_MM:
        multiplier = 1
    elif dist <= TRIPLE_OUTER_MM:
        multiplier = 3
    elif dist <= DOUBLE_INNER_MM:
        multiplier = 1
    else:
        multiplier = 2

    angle_rad = math.atan2(dx, -dy)
    if angle_rad < 0:
        angle_rad += 2 * math.pi

    angle_rad = (angle_rad + SEG_ANGLE / 2) % (2 * math.pi)
    seg_index = int(angle_rad / SEG_ANGLE) % 20
    value = SEGMENTS[seg_index]

    if multiplier == 1:
        return str(value)
    elif multiplier == 2:
        return f"D{value}"
    else:
        return f"T{value}"


def score_to_points(score_str: str) -> int:
    if score_str == "50":
        return 50
    if score_str == "25":
        return 25
    if score_str == "0":
        return 0
    if score_str.startswith("T"):
        return 3 * int(score_str[1:])
    if score_str.startswith("D"):
        return 2 * int(score_str[1:])
    return int(score_str)


def draw_annotations(image: np.ndarray, detections: list, scores: list) -> np.ndarray:
    annotated = image.copy()

    colors = {
        "dart": (0, 255, 100),
        "calib": (255, 165, 0),
    }

    for det in detections:
        x, y = int(det["x"]), int(det["y"])
        cls = det["class"]

        if cls == "dart":
            idx = det.get("dart_idx", 0)
            score = scores[idx] if idx < len(scores) else "?"
            pts = score_to_points(score) if score != "?" else 0

            cv2.drawMarker(annotated, (x, y), colors["dart"], cv2.MARKER_CROSS, 30, 3)
            cv2.circle(annotated, (x, y), 18, colors["dart"], 2)

            label = f"{score} ({pts}p)"
            (tw, th), _ = cv2.getTextSize(label, cv2.FONT_HERSHEY_SIMPLEX, 0.8, 2)
            cv2.rectangle(annotated, (x - 5, y - th - 15), (x + tw + 5, y - 5), (0, 0, 0), -1)
            cv2.putText(annotated, label, (x, y - 8), cv2.FONT_HERSHEY_SIMPLEX, 0.8, colors["dart"], 2)

        elif cls.startswith("calib"):
            cv2.drawMarker(annotated, (x, y), colors["calib"], cv2.MARKER_DIAMOND, 20, 2)

    return annotated


class DartScorer:
    def __init__(self, model_path: Optional[str] = None):
        self.model = None
        self.model_loaded = False
        self._load_model(model_path)

    def _load_model(self, model_path: Optional[str]):
        try:
            from ultralytics import YOLO

            search_paths = [
                model_path,
                "model/dart_sense.pt",
                "dart_sense.pt",
                "dart-sense/runs/detect/train/weights/best.pt",
            ]

            for path in search_paths:
                if path and Path(path).exists():
                    self.model = YOLO(path)
                    self.model_loaded = True
                    logger.info(f"Model załadowany z: {path}")
                    return

            logger.warning("Nie znaleziono modelu .pt — działa w trybie MOCK.")

        except ImportError:
            logger.warning("ultralytics nie zainstalowane — tryb MOCK")

    def _run_yolo(self, image: np.ndarray) -> list:
        results = self.model(image, verbose=False, conf=0.5)[0]
        detections = []

        CLASS_MAP = {
            0: "calib_20",
            1: "calib_3",
            2: "calib_11",
            3: "calib_6",
            4: "dart",
            5: "dart",
            6: "dart",
        }

        for box in results.boxes:
            cls_id = int(box.cls[0])
            cls_name = CLASS_MAP.get(cls_id, "unknown")
            x_center = float(box.xywh[0][0])
            y_center = float(box.xywh[0][1])
            conf = float(box.conf[0])

            detections.append({
                "class": cls_name,
                "x": x_center,
                "y": y_center,
                "confidence": round(conf, 3),
            })

        return detections

    def _mock_detections(self, image: np.ndarray) -> list:
        h, w = image.shape[:2]
        cx, cy = w / 2, h / 2
        return [
            {"class": "dart",     "x": cx + 10,  "y": cy - 60,  "confidence": 0.95},
            {"class": "dart",     "x": cx - 30,  "y": cy + 20,  "confidence": 0.92},
            {"class": "dart",     "x": cx + 50,  "y": cy + 40,  "confidence": 0.88},
            {"class": "calib_20", "x": cx,       "y": cy - 165, "confidence": 0.99},
            {"class": "calib_6",  "x": cx + 165, "y": cy,       "confidence": 0.99},
            {"class": "calib_3",  "x": cx,       "y": cy + 165, "confidence": 0.99},
            {"class": "calib_11", "x": cx - 165, "y": cy,       "confidence": 0.99},
        ]

    def _compute_calibration(self, detections: list) -> Optional[dict]:
        """
        Oblicza środek tarczy i skalę z 4 punktów kalibracyjnych.
        """
        calib_map = {}
        for det in detections:
            if det["class"].startswith("calib"):
                calib_map[det["class"]] = (det["x"], det["y"])

        required = ["calib_20", "calib_6", "calib_3", "calib_11"]
        if not all(k in calib_map for k in required):
            logger.warning(f"Brakuje punktów kalibracyjnych: {list(calib_map.keys())}")
            return None

        # Środek tarczy = średnia punktów kalibracyjnych
        cx = sum(calib_map[k][0] for k in required) / 4
        cy = sum(calib_map[k][1] for k in required) / 4

        # Skala = 170mm / średnia odległość punktów od środka
        dists = [math.sqrt((calib_map[k][0] - cx) ** 2 + (calib_map[k][1] - cy) ** 2) for k in required]
        scale = DOUBLE_OUTER_MM / (sum(dists) / len(dists))

        return {"cx": cx, "cy": cy, "scale": scale}

    def _transform_dart_coords(self, x: float, y: float, calib: dict):
        """Transformuje współrzędne rzutki do układu tarczy."""
        dx = (x - calib["cx"]) * calib["scale"]
        dy = (y - calib["cy"]) * calib["scale"]
        return 400.0 + dx, 400.0 + dy

    def process_image(self, image_bytes: bytes) -> dict:
        nparr = np.frombuffer(image_bytes, np.uint8)
        image = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

        if image is None:
            raise ValueError("Nie można zdekodować obrazu")

        # Resize do 800px zachowując proporcje
        h, w = image.shape[:2]
        if w > 800:
            scale = 800 / w
            image = cv2.resize(image, (800, int(h * scale)))

        # Detekcja
        if self.model_loaded:
            detections = self._run_yolo(image)
        else:
            detections = self._mock_detections(image)

        # Kalibracja
        calib = self._compute_calibration(detections)

        # Oblicz wyniki rzutek
        dart_detections = [d for d in detections if d["class"] == "dart"]
        scores = []

        for i, det in enumerate(dart_detections):
            det["dart_idx"] = i
            if calib is not None:
                tx, ty = self._transform_dart_coords(det["x"], det["y"], calib)
                score = score_from_coords(tx, ty)
            else:
                h, w = image.shape[:2]
                cx, cy = w / 2, h / 2
                scale = BOARD_RADIUS_MM / (min(h, w) * 0.4)
                nx = (det["x"] - cx) * scale + 400
                ny = (det["y"] - cy) * scale + 400
                score = score_from_coords(nx, ny)

            scores.append(score)

        total = sum(score_to_points(s) for s in scores)

        # Adnotacje
        annotated = draw_annotations(image, detections, scores)

        _, buffer = cv2.imencode(".jpg", annotated, [cv2.IMWRITE_JPEG_QUALITY, 85])
        img_base64 = base64.b64encode(buffer).decode("utf-8")

        return {
            "darts": scores,
            "total": total,
            "dart_count": len(scores),
            "calibration_ok": calib is not None,
            "mock_mode": not self.model_loaded,
            "annotated_image": img_base64,
            "detections": [
                {
                    "class": d["class"],
                    "x": round(d["x"], 1),
                    "y": round(d["y"], 1),
                    "confidence": d["confidence"],
                }
                for d in detections
            ],
        }