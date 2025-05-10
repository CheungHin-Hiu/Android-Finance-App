from pydantic import BaseModel
from typing import Dict, Any
class TransactionPostRequest(BaseModel):
     type: str
     category_type: str
     currency_type: str
     amount: float
     date: str
