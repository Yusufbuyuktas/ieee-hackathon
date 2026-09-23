
import pytest

from fastapi.testclient import TestClient

import main

from main import (
    app,
    PhotoValidationResponse
)


# ==========================================
# TEST İSTEMCİSİ
# ==========================================

client = TestClient(app)


# Testlerde kullanacağımız örnek URL

PHOTO_URL = (
    "https://upload.wikimedia.org/"
    "wikipedia/commons/test.jpg"
)


# ==========================================
# SAHTE FOTOĞRAF İNDİRME
# ==========================================

async def fake_download_image(photo_url: str):

    return b"fake-image-data"


# ==========================================
# SAHTE FOTOĞRAF HAZIRLAMA
# ==========================================

def fake_prepare_image(image_data: bytes):

    return b"prepared-image-data"


# ==========================================
# TEST BAĞIMLILIKLARI
# ==========================================

@pytest.fixture
def mock_image_processing(monkeypatch):

    monkeypatch.setattr(
        main,
        "download_image",
        fake_download_image
    )

    monkeypatch.setattr(
        main,
        "prepare_image",
        fake_prepare_image
    )


# ==========================================
# TEST 1 — HEALTH ENDPOINT
# ==========================================

def test_health_endpoint():

    response = client.get("/health")

    assert response.status_code == 200

    data = response.json()

    assert data["status"] == "ok"

    assert data["service"] == "ai-service"


# ==========================================
# TEST 2 — GEÇERSİZ KATEGORİ
# ==========================================

def test_invalid_category():

    response = client.post(
        "/moderate-photo",
        json={
            "photo_url": PHOTO_URL,
            "category": "araba_kazasi"
        }
    )

    assert response.status_code == 422


# ==========================================
# TEST 3 — GEÇERSİZ URL
# ==========================================

def test_invalid_url():

    response = client.post(
        "/moderate-photo",
        json={
            "photo_url": "not-a-url",
            "category": "balik_olumu"
        }
    )

    assert response.status_code == 422


# ==========================================
# TEST 4 — İZİN VERİLMEYEN FOTOĞRAF ADRESİ
# ==========================================

def test_forbidden_image_host():

    response = client.post(
        "/moderate-photo",
        json={
            "photo_url":
                "https://example.com/photo.jpg",

            "category": "balik_olumu"
        }
    )

    assert response.status_code == 400

    assert response.json()["detail"] == (
        "Fotoğraf adresine izin verilmiyor."
    )


# ==========================================
# TEST 5 — MODERASYON SONUÇLARI
# ==========================================

@pytest.mark.parametrize(

    "category, consistent, confidence, expected",

    [
        (
            "balik_olumu",
            True,
            0.95,
            "approved"
        ),

        (
            "balik_olumu",
            False,
            0.95,
            "inconsistent"
        ),

        (
            "kotu_koku",
            True,
            0.30,
            "review"
        )

    ]

)
def test_moderation_endpoint(

    monkeypatch,
    mock_image_processing,

    category,
    consistent,
    confidence,
    expected

):

    # Gerçek Gemini fonksiyonu yerine
    # sahte değerlendirme fonksiyonu

    def fake_analyze_photo(
        image_data,
        report_category
    ):

        return PhotoValidationResponse(

            tutarli=consistent,

            guven_skoru=confidence,

            aciklama="Test değerlendirmesi."

        )

    monkeypatch.setattr(
        main,
        "analyze_photo",
        fake_analyze_photo
    )

    # API isteğini gönder

    response = client.post(

        "/moderate-photo",

        json={
            "photo_url": PHOTO_URL,
            "category": category
        }

    )

    # HTTP cevabını kontrol et

    assert response.status_code == 200

    data = response.json()

    # Moderasyon sonucunu kontrol et

    assert data["moderation_status"] == expected

    assert data["tutarli"] == consistent

    assert data["guven_skoru"] == confidence


# ==========================================
# TEST 6 — GEÇERSİZ AI CEVABI
# ==========================================

def test_invalid_ai_response(

    monkeypatch,
    mock_image_processing

):

    def fake_analyze_photo(
        image_data,
        category
    ):

        raise ValueError(
            "Geçersiz AI cevabı"
        )

    monkeypatch.setattr(
        main,
        "analyze_photo",
        fake_analyze_photo
    )

    response = client.post(

        "/moderate-photo",

        json={
            "photo_url": PHOTO_URL,
            "category": "balik_olumu"
        }

    )

    assert response.status_code == 502


# ==========================================
# TEST 7 — GEMINI ERİŞİM HATASI
# ==========================================

def test_gemini_unavailable(

    monkeypatch,
    mock_image_processing

):

    def fake_analyze_photo(
        image_data,
        category
    ):

        raise RuntimeError(
            "Gemini servisine erişilemiyor."
        )

    monkeypatch.setattr(
        main,
        "analyze_photo",
        fake_analyze_photo
    )

    response = client.post(

        "/moderate-photo",

        json={
            "photo_url": PHOTO_URL,
            "category": "balik_olumu"
        }

    )

    assert response.status_code == 503