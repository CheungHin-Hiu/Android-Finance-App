import pytest
import bcrypt
from services.authentication.token.encryption import hash_password, verify_password


def test_hash_password_returns_bytes():
    password = "mysecretpassword"
    hashed = hash_password(password)
    assert isinstance(hashed, bytes)

def test_verify_password_correct_password():
    password = "mysecretpassword"
    hashed = hash_password(password)
    assert verify_password(password, hashed) is True

def test_verify_password_wrong_password():
    password = "mysecretpassword"
    wrong_password = "wrongpassword"
    hashed = hash_password(password)
    assert verify_password(wrong_password, hashed) is False
