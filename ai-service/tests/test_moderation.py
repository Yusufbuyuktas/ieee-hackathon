
import pytest

from main import (
    PhotoValidationResponse,
    ReportCategory,
    ModerationStatus,
    determine_moderation_status
)


# ==========================================
# 1. TUTARLI FOTOĞRAF TESTİ
# ==========================================

def test_approved_report():

    result = PhotoValidationResponse(
        tutarli=True,
        guven_skoru=0.95,
        aciklama="Fotoğrafta ölü balıklar var."
    )

    status = determine_moderation_status(
        result=result,
        category=ReportCategory.BALIK_OLUMU
    )

    assert status == ModerationStatus.APPROVED


# ==========================================
# 2. TUTARSIZ FOTOĞRAF TESTİ
# ==========================================

def test_inconsistent_report():

    result = PhotoValidationResponse(
        tutarli=False,
        guven_skoru=0.95,
        aciklama="Fotoğrafta ölü balık yok."
    )

    status = determine_moderation_status(
        result=result,
        category=ReportCategory.BALIK_OLUMU
    )

    assert status == ModerationStatus.INCONSISTENT


# ==========================================
# 3. DÜŞÜK GÜVEN TESTİ
# ==========================================

def test_low_confidence_report():

    result = PhotoValidationResponse(
        tutarli=True,
        guven_skoru=0.45,
        aciklama="Görsel belirsiz."
    )

    status = determine_moderation_status(
        result=result,
        category=ReportCategory.BULANIK_SU
    )

    assert status == ModerationStatus.REVIEW


# ==========================================
# 4. KÖTÜ KOKU TESTİ
# ==========================================

def test_bad_smell_report():

    result = PhotoValidationResponse(
        tutarli=True,
        guven_skoru=0.99,
        aciklama="Su görülüyor."
    )

    status = determine_moderation_status(
        result=result,
        category=ReportCategory.KOTU_KOKU
    )

    assert status == ModerationStatus.REVIEW


# ==========================================
# 5. DİĞER KATEGORİSİ TESTİ
# ==========================================

def test_other_category_report():

    result = PhotoValidationResponse(
        tutarli=True,
        guven_skoru=0.95,
        aciklama="Çevresel gözlem."
    )

    status = determine_moderation_status(
        result=result,
        category=ReportCategory.DIGER
    )

    assert status == ModerationStatus.REVIEW


# ==========================================
# 6. SINIR DEĞERİ TESTİ
# ==========================================

def test_exact_confidence_threshold():

    result = PhotoValidationResponse(
        tutarli=True,
        guven_skoru=0.80,
        aciklama="Görsel tutarlı."
    )

    status = determine_moderation_status(
        result=result,
        category=ReportCategory.BALIK_OLUMU
    )

    assert status == ModerationStatus.APPROVED


# ==========================================
# 7. SINIRIN ALTINDAKİ DEĞER TESTİ
# ==========================================

def test_below_confidence_threshold():

    result = PhotoValidationResponse(
        tutarli=False,
        guven_skoru=0.79,
        aciklama="Görsel tutarsız."
    )

    status = determine_moderation_status(
        result=result,
        category=ReportCategory.BALIK_OLUMU
    )

    assert status == ModerationStatus.REVIEW


# ==========================================
# 8. GEÇERSİZ GÜVEN GÖSTERGESİ TESTİ
# ==========================================

@pytest.mark.parametrize(
    "invalid_score",
    [-0.1, 1.1]
)
def test_invalid_confidence_score(
    invalid_score
):

    from pydantic import ValidationError

    with pytest.raises(ValidationError):

        PhotoValidationResponse(
            tutarli=True,
            guven_skoru=invalid_score,
            aciklama="Test"
        )