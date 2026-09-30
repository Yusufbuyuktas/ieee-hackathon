import pytest

from main import (
    PhotoValidationResponse,
    ModerationStatus,
    determine_moderation_status
)


# ==========================================
# 1. APPROVED TESTİ
# ==========================================

def test_approved_report():

    result = PhotoValidationResponse(
        guven_skoru=0.95,
        aciklama="Fotoğrafta ölü balıklar görülüyor."
    )

    status = determine_moderation_status(
        result=result
    )

    assert status == ModerationStatus.APPROVED


# ==========================================
# 2. REVIEW TESTİ
# ==========================================

def test_review_report():

    result = PhotoValidationResponse(
        guven_skoru=0.65,
        aciklama="Görsel kısmen kategoriyi destekliyor."
    )

    status = determine_moderation_status(
        result=result
    )

    assert status == ModerationStatus.REVIEW


# ==========================================
# 3. INCONSISTENT TESTİ
# ==========================================

def test_inconsistent_report():

    result = PhotoValidationResponse(
        guven_skoru=0.30,
        aciklama="Görsel seçilen kategoriyi desteklemiyor."
    )

    status = determine_moderation_status(
        result=result
    )

    assert status == ModerationStatus.INCONSISTENT


# ==========================================
# 4. APPROVED SINIR DEĞERİ
# ==========================================

def test_approved_threshold():

    result = PhotoValidationResponse(
        guven_skoru=0.80,
        aciklama="Test"
    )

    status = determine_moderation_status(
        result=result
    )

    assert status == ModerationStatus.APPROVED


# ==========================================
# 5. REVIEW ÜST SINIRI
# ==========================================

def test_review_upper_boundary():

    result = PhotoValidationResponse(
        guven_skoru=0.79,
        aciklama="Test"
    )

    status = determine_moderation_status(
        result=result
    )

    assert status == ModerationStatus.REVIEW


# ==========================================
# 6. REVIEW ALT SINIRI
# ==========================================

def test_review_lower_boundary():

    result = PhotoValidationResponse(
        guven_skoru=0.50,
        aciklama="Test"
    )

    status = determine_moderation_status(
        result=result
    )

    assert status == ModerationStatus.REVIEW


# ==========================================
# 7. INCONSISTENT ÜST SINIRI
# ==========================================

def test_inconsistent_boundary():

    result = PhotoValidationResponse(
        guven_skoru=0.49,
        aciklama="Test"
    )

    status = determine_moderation_status(
        result=result
    )

    assert status == ModerationStatus.INCONSISTENT


# ==========================================
# 8. GEÇERSİZ GÜVEN SKORU
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
            guven_skoru=invalid_score,
            aciklama="Test"
        )