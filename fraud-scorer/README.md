# fraud-scorer

A FastAPI service that scores each payment for fraud risk while the switch waits.
It sits on the authorisation path, so it has a latency budget and the switch needs a
policy for when it is slow or down. Both are decided in week 5.

```bash
python3 -m venv .venv && . .venv/bin/activate
pip install -r requirements-dev.txt
uvicorn app.main:app --reload --port 8000   # http://localhost:8000/docs
pytest
ruff check . && ruff format --check .
```

| Setting | Default | Meaning |
| --- | --- | --- |
| `FRAUD_MODEL_PATH` | `training/artifacts/model.json` | Where the trained model is loaded from |
