from fastapi.testclient import TestClient

from app.main import app

client = TestClient(app)


def test_health_reports_ok():
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json()["status"] == "ok"


def test_health_reports_no_model_before_training(tmp_path, monkeypatch):
    from app import config

    monkeypatch.setattr(config.settings, "model_path", tmp_path / "missing.json")
    assert client.get("/health").json()["model_loaded"] is False
