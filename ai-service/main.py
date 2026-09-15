from fastapi import FastAPI

app = FastAPI(title="Ergene AI Servisi")


@app.get("/health")
def health_check():
    return {"status": "ok", "service": "ai-service"}
