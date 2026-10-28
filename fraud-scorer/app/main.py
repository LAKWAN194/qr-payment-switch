"""The fraud-scorer service.

The switch calls this on the authorisation path, so latency and failure behaviour matter
as much as accuracy. The scoring endpoint, its request and response shapes, and the
latency budget are yours to design in week 5 (see docs/adr/README.md).
"""

from fastapi import FastAPI

from app.config import settings

app = FastAPI(title="fraud-scorer", version="0.1.0")


@app.get("/health")
def health() -> dict:
    """Liveness check. `model_loaded` turns true once week 5 adds model loading."""
    return {"status": "ok", "model_loaded": settings.model_path.exists()}
