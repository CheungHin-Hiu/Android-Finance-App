import pytest
import time
from datetime import timedelta, datetime
from services.authentication.token.access_token import JWTGenerator  

@pytest.fixture
def jwt_generator():
    return JWTGenerator()

def test_create_jwt_token_returns_string(jwt_generator):
    payload = {"user_id": 123, "username": "123123"}
    token = jwt_generator.create_jwt_token(payload)
    assert isinstance(token, str) or isinstance(token, bytes)  

def test_verify_jwt_token_valid(jwt_generator):
    payload = {"user_id": 123, "username": "123123"}
    token = jwt_generator.create_jwt_token(payload)
    decoded = jwt_generator.verify_jwt_token(token)
    assert decoded is not False
    assert decoded.get("user_id") == 123

def test_verify_jwt_token_invalid(jwt_generator):
    invalid_token = "this.is.an.invalid.token"
    result = jwt_generator.verify_jwt_token(invalid_token)
    assert result is False
