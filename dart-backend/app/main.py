from fastapi import FastAPI, File, UploadFile, HTTPException
from fastapi.responses import JSONResponse
from fastapi.middleware.cors import CORSMiddleware
import uvicorn

from app.scorer import DartScorer

app = FastAPI(title="Dart Score API", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

scorer = DartScorer()


@app.get("/health")
def health():
    return {"status": "ok", "model_loaded": scorer.model_loaded}


@app.post("/score")
async def score_image(image: UploadFile = File(...)):
    """
    Przyjmuje zdjęcie tarczy, zwraca wyniki rzutek i annotowane zdjęcie.

    Response:
    {
        "darts": ["T20", "D16", "1"],   # wyniki każdej rzutki
        "total": 93,                     # suma punktów
        "dart_count": 3,
        "annotated_image": "<base64>"    # zdjęcie z zaznaczonymi rzutkami
    }
    """
    if not image.content_type.startswith("image/"):
        raise HTTPException(status_code=400, detail="Plik musi być obrazem (jpg, png...)")

    image_bytes = await image.read()

    if len(image_bytes) > 20 * 1024 * 1024:  # 20MB limit
        raise HTTPException(status_code=400, detail="Obraz za duży (max 20MB)")

    try:
        result = scorer.process_image(image_bytes)
        return JSONResponse(content=result)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Błąd przetwarzania: {str(e)}")


if __name__ == "__main__":
    uvicorn.run("app.main:app", host="0.0.0.0", port=8000, reload=True)
