from pydantic import BaseModel
from typing import Optional

class UpdateAssetRequest(BaseModel):
    id: str
    category: Optional[str]
    type: Optional[str]
    amount: Optional[float]
