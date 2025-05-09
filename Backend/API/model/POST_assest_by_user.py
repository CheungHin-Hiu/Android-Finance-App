from pydantic import BaseModel
from typing import Optional

class InsertAssetRequest(BaseModel):
    category: str
    type: str
    amount: float
