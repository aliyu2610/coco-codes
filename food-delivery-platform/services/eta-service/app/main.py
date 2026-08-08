import logging
import logging.config
import json
from fastapi import FastAPI

with open("logging.json") as f:
    logging.config.dictConfig(json.load(f))

logger = logging.getLogger(__name__)
app = FastAPI(title="eta-service")

@app.get("/health")
def health():
    return {"status": "ok"}
