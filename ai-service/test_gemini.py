
import os

from google import genai


# Docker ortam değişkenlerinden API anahtarını al
api_key = os.getenv("GEMINI_API_KEY")

# Kullanılacak Gemini modeli
model_name = os.getenv(
    "GEMINI_MODEL",
    "gemini-3.8-flash"
)


if not api_key:
    raise RuntimeError(
        "GEMINI_API_KEY ortam değişkeni bulunamadı."
    )


# Gemini istemcisini oluştur
client = genai.Client(
    api_key=api_key
)


# İlk API isteğimizi gönder
response = client.models.generate_content(
    model=model_name,
    contents=(
        "Merhaba Gemini! "
        "Ergene Nehri su kirliliği izleme sistemi "
        "için geliştirilen AI servisini test ediyoruz. "
        "Lütfen kısa bir Türkçe cevap ver."
    )
)


# Gemini'nin cevabını yazdır
print("Kullanılan model:", model_name)

print("Gemini cevabı:")

print(response.text)