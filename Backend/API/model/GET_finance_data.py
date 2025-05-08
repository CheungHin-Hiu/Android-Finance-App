from pydantic import BaseModel
from typing import Any
from datetime import time

# Define request schema for GET /finance
class RequestFinanceData(BaseModel):
    currency: list | None = None
    stock: list | None = None
    crypto: list | None = None

# Define response schema for GET /finance
class ResponseFinanceData(BaseModel):
    timeRetrieved: time
    currency: dict[str, Any] | None = None
    stock: list[dict[str, Any]] | None = None
    crypto: list[dict[str, Any]] | None = None

    