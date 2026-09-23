
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
    HttpUrl,
    ValidationError
)

from google import genai
from google.genai import types


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
    "gemini-3.6-flash"
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
        "upload.wikimedia.org,images.unsplash.com"
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

    photo_url: HttpUrl

    category: ReportCategory


class PhotoValidationResponse(BaseModel):

    tutarli: bool

    guven_skoru: float = Field(
        ge=0.0,
        le=1.0
    )

    aciklama: str

class ModerationResponse(BaseModel):

    tutarli: bool

    guven_skoru: float = Field(
        ge=0.0,
        le=1.0
    )

    aciklama: str

    moderation_status: ModerationStatus

# ==========================================
# 3. FOTOĞRAF İNDİRME
# ==========================================

async def download_image(
    photo_url: str
) -> bytes:

    parsed_url = urlparse(photo_url)

    if (
        parsed_url.scheme != "https"
        or parsed_url.hostname not in ALLOWED_IMAGE_HOSTS
        or parsed_url.port not in (None, 443)
    ):

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

2. Görselin seçilen kategoriyle tutarlı
   olup olmadığını değerlendir.

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
   kesin görsel onay verme.

7. Görsel belirsizse bunu açıklamada
   belirt ve güven göstergesini düşür.

8. Fotoğraf tamamen alakasızsa
   tutarli alanını false yap.

9. guven_skoru alanında 0 ile 1
   arasında bir değerlendirme göstergesi
   üret. Bu değer bilimsel olarak
   kalibre edilmiş olasılık değildir.

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
    result: PhotoValidationResponse,
    category: ReportCategory
) -> ModerationStatus:

    # Fotoğraftan doğrudan doğrulanamayan
    # veya belirsiz kategoriler

    if category in (
        ReportCategory.KOTU_KOKU,
        ReportCategory.DIGER
    ):

        return ModerationStatus.REVIEW

    # Modelin güven göstergesi düşükse
    # insan incelemesine yönlendir

    if result.guven_skoru < 0.80:

        return ModerationStatus.REVIEW

    # Yüksek güven göstergesiyle
    # tutarlı olarak değerlendirilmişse

    if result.tutarli:

        return ModerationStatus.APPROVED

    # Yüksek güven göstergesiyle
    # tutarsız olarak değerlendirilmişse

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
            detail="AI değerlendirme cevabı geçersiz."
        )

    except Exception:

        raise HTTPException(
            status_code=503,
            detail="AI servisine şu anda erişilemiyor."
        )

    # Moderasyon durumunu belirle
    status = determine_moderation_status(
        result=result,
        category=request.category
    )

    # Backend'e sonucu döndür
    return ModerationResponse(
        tutarli=result.tutarli,
        guven_skoru=result.guven_skoru,
        aciklama=result.aciklama,
        moderation_status=status
    )