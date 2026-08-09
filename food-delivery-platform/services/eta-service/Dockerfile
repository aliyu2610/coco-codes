FROM python:3.12-slim
WORKDIR /app
COPY services/eta-service/requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY services/eta-service/app/ .
COPY infra/logging/logging.json .
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8084"]
