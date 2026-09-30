
import io
import os

from enum import Enum
from urllib.parse import urlparse

import httpx

from PIL import Image, UnidentifiedImageError

from fastapi import FastAPI, HTTPException

from fastapi.concurrency import run_in_threadpool

from pydantic import (
    BaseModel,
    Field,
    AnyHttpUrl,
    ValidationError
)

from google import genai
from google.genai import types
from google.genai import errors


# ==========================================
# 1. UYGULAMA AYARLARI
# ==========================================

app = FastAPI(
    title="Ergene AI Servisi",
    description="Vatandaş bildirimlerinin AI ile değerlendirilmesi",
    version="0.2.0"
)


GEMINI_API_KEY = os.getenv("GEMINI_API_KEY")

GEMINI_MODEL = os.getenv(
    "GEMINI_MODEL",
    "gemini-3.5-flash-lite"
)


if not GEMINI_API_KEY:
    raise RuntimeError(
        "GEMINI_API_KEY ortam değişkeni bulunamadı."
    )


client = genai.Client(
    api_key=GEMINI_API_KEY
)


# İzin verilen fotoğraf barındırma alanları

ALLOWED_IMAGE_HOSTS = {
    host.strip().lower()
    for host in os.getenv(
        "PHOTO_ALLOWED_HOSTS",
        "upload.wikimedia.org,images.unsplash.com,134.112.41.108"
    ).split(",")
    if host.strip()
}


MAX_IMAGE_SIZE = 5 * 1024 * 1024

Image.MAX_IMAGE_PIXELS = 12_000_000


# ==========================================
# 2. VERİ MODELLERİ
# ==========================================

class ReportCategory(str, Enum):

    BULANIK_SU = "bulanik_su"

    KIRLI_RENK_DEGISIMI = "kirli_renk_degisimi"

    BALIK_OLUMU = "balik_olumu"

    KOTU_KOKU = "kotu_koku"

    DIGER = "diger"

class ModerationStatus(str, Enum):

    APPROVED = "approved"

    REVIEW = "review"

    INCONSISTENT = "inconsistent"

class PhotoValidationRequest(BaseModel):

    photo_url: AnyHttpUrl

    category: ReportCategory


class PhotoValidationResponse(BaseModel):

    guven_skoru: float = Field(
        ge=0.0,
        le=1.0
    )

    aciklama: str


class ModerationResponse(BaseModel):

    guven_skoru: float = Field(
        ge=0.0,
        le=1.0
    )

    aciklama: str

    moderation_status: ModerationStatus

    model: str

# ==========================================
# 3. FOTOĞRAF İNDİRME
# ==========================================

async def download_image(
    photo_url: str
) -> bytes:

    parsed_url = urlparse(photo_url)

    is_allowed_external_url = (
        parsed_url.scheme == "https"
        and parsed_url.hostname in ALLOWED_IMAGE_HOSTS
        and parsed_url.port in (None, 443)
    )
    is_allowed_internal_url = (
        parsed_url.scheme == "http"
        and parsed_url.hostname == "backend"
        and parsed_url.port == 8080
    )

    if not (is_allowed_external_url or is_allowed_internal_url):

        raise HTTPException(
            status_code=400,
            detail="Fotoğraf adresine izin verilmiyor."
        )

    try:

        async with httpx.AsyncClient(
            timeout=15.0,
            follow_redirects=False,
            headers={
                "User-Agent": os.getenv(
                    "PHOTO_USER_AGENT",
                    "ErgeneWaterMonitoring/0.1"
                )
            }
        ) as http_client:

            async with http_client.stream(
                "GET",
                photo_url
            ) as response:

                if response.status_code != 200:

                    raise HTTPException(
                        status_code=400,
                        detail="Fotoğraf indirilemedi."
                    )

                image_data = bytearray()

                async for chunk in response.aiter_bytes():

                    image_data.extend(chunk)

                    if len(image_data) > MAX_IMAGE_SIZE:

                        raise HTTPException(
                            status_code=413,
                            detail="Fotoğraf 5 MB sınırını aşıyor."
                        )

                return bytes(image_data)

    except httpx.RequestError:

        raise HTTPException(
            status_code=400,
            detail="Fotoğraf adresine erişilemiyor."
        )


# ==========================================
# 4. FOTOĞRAF DOĞRULAMA
# ==========================================

def prepare_image(
    image_data: bytes
) -> bytes:

    try:

        with Image.open(
            io.BytesIO(image_data)
        ) as image:

            if image.format not in (
                "JPEG",
                "PNG",
                "WEBP"
            ):

                raise HTTPException(
                    status_code=415,
                    detail="Desteklenmeyen görsel formatı."
                )

            image.load()

            image = image.convert("RGB")

            output = io.BytesIO()

            image.save(
                output,
                format="JPEG",
                quality=85
            )

            return output.getvalue()

    except (
        UnidentifiedImageError,
        OSError,
        ValueError,
        Image.DecompressionBombError
    ):

        raise HTTPException(
            status_code=415,
            detail="Geçerli bir görsel dosyası gönderilmedi."
        )


# ==========================================
# 5. AI PROMPT
# ==========================================

def create_prompt(
    category: ReportCategory
) -> str:

    category_descriptions = {

        ReportCategory.BULANIK_SU:
            "Suyun bulanık görünmesi",

        ReportCategory.KIRLI_RENK_DEGISIMI:
            "Suda gözle görülür renk değişimi",

        ReportCategory.BALIK_OLUMU:
            "Ölü balıkların görülmesi",

        ReportCategory.KOTU_KOKU:
            "Kötü koku bildirimi",

        ReportCategory.DIGER:
            "Diğer çevresel gözlem"

    }

    selected_category = category_descriptions[
        category
    ]

    return f"""
Sen Ergene Nehri Su Kirliliği İzleme Sistemi
için çalışan bir görüntü değerlendirme
asistanısın.

Görevin:

Vatandaşın gönderdiği fotoğrafın seçtiği
çevresel gözlem kategorisiyle tutarlı olup
olmadığını değerlendirmek.

Seçilen kategori:

{category.value}

Kategori açıklaması:

{selected_category}

Kurallar:

1. Yalnızca fotoğrafta görülebilen
   kanıtlara dayan.

2. Fotoğrafın seçilen çevresel gözlem
   kategorisini ne ölçüde desteklediğini
   değerlendir.

3. Fotoğrafta görülmeyen olayları
   gerçekleşmiş gibi varsayma.

4. Fotoğraftan arsenik, kurşun veya
   başka bir kimyasal maddenin
   konsantrasyonunu tahmin etme.

5. Fotoğraftan suyun kesin olarak
   sağlıksız veya içilemez olduğu
   sonucunu çıkarma.

6. Kötü koku fotoğraftan doğrudan
   doğrulanamaz. Bu kategoride
   guven_skoru değerini 0.80 veya
   üzerinde verme.

7. Görsel belirsiz, düşük kaliteli veya
   seçilen kategoriyle yalnızca kısmen
   ilişkiliyse guven_skoru değerini düşür.

8. Fotoğraf seçilen kategoriyle tamamen
   alakasızsa veya kategoriyi destekleyen
   görsel kanıt yoksa düşük guven_skoru ver.

9. guven_skoru alanında 0 ile 1 arasında
   bir değerlendirme göstergesi üret.

   Bu skor, fotoğraftaki görsel kanıtın
   seçilen kategoriyle ne kadar güçlü
   biçimde uyumlu olduğunu ifade eder.

   Bu değer bilimsel olarak kalibre
   edilmiş bir olasılık değildir.

10. Açıklamanı kısa ve Türkçe yaz.

Yalnızca belirtilen JSON şemasına
uygun cevap üret.
"""


# ==========================================
# 6. GEMINI DEĞERLENDİRMESİ
# ==========================================

def analyze_photo(
    image_data: bytes,
    category: ReportCategory
) -> PhotoValidationResponse:

    prompt = create_prompt(category)

    response = client.models.generate_content(

        model=GEMINI_MODEL,

        contents=[

            types.Part.from_bytes(
                data=image_data,
                mime_type="image/jpeg"
            ),

            prompt

        ],

        config=types.GenerateContentConfig(

            response_mime_type="application/json",

            response_schema=PhotoValidationResponse

        )

    )

    if not response.text:

        raise ValueError(
            "Gemini boş cevap döndürdü."
        )

    return PhotoValidationResponse.model_validate_json(
        response.text
    )


# ==========================================
# 7. MODERASYON KARAR MEKANİZMASI
# ==========================================

def determine_moderation_status(
    result: PhotoValidationResponse
) -> ModerationStatus:

    if result.guven_skoru >= 0.80:

        return ModerationStatus.APPROVED

    if result.guven_skoru >= 0.50:

        return ModerationStatus.REVIEW

    return ModerationStatus.INCONSISTENT


# ==========================================
# 8. API ENDPOINTLERİ
# ==========================================

@app.get("/health")
def health_check():

    return {
        "status": "ok",
        "service": "ai-service"
    }


@app.post(
    "/moderate-photo",
    response_model=ModerationResponse
)
async def moderate_photo(
    request: PhotoValidationRequest
):

    # Fotoğrafı indir
    image_data = await download_image(
        str(request.photo_url)
    )

    # Görseli hazırla
    prepared_image = await run_in_threadpool(
        prepare_image,
        image_data
    )

    # Gemini değerlendirmesi
    try:

        result = await run_in_threadpool(
            analyze_photo,
            prepared_image,
            request.category
        )

    except (ValidationError, ValueError):

        raise HTTPException(
            status_code=502,
            detail={
                "code": "AI_INVALID_RESPONSE",
                "message": "AI servisinden geçersiz bir değerlendirme cevabı alındı."
            }
        )

    except errors.ClientError as exc:

        if exc.code == 429:

            raise HTTPException(
                status_code=429,
                detail={
                    "code": "AI_RATE_LIMIT",
                    "message": "AI servisinin kullanım limiti aşıldı."
                }
            )

        raise HTTPException(
            status_code=502,
            detail={
                "code": "AI_CLIENT_ERROR",
                "message": "AI servisine gönderilen istek işlenemedi."
            }
        )

    except errors.ServerError:

        raise HTTPException(
            status_code=503,
            detail={
                "code": "AI_PROVIDER_UNAVAILABLE",
                "message": "AI sağlayıcısına şu anda erişilemiyor."
            }
        )

    except Exception:

        raise HTTPException(
            status_code=500,
            detail={
                "code": "AI_INTERNAL_ERROR",
                "message": "AI servisi içerisinde beklenmeyen bir hata oluştu."
            }
        )

    # Moderasyon durumunu belirle
    status = determine_moderation_status(
    result=result
    )

    # Backend'e sonucu döndür
    return ModerationResponse(
    guven_skoru=result.guven_skoru,
    aciklama=result.aciklama,
    moderation_status=status,
    model=GEMINI_MODEL
    )