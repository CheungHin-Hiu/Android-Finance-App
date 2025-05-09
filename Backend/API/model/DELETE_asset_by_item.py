from pydantic import BaseModel
from typing import Optional

class DeleteAssetRequest(BaseModel):
    id: str
