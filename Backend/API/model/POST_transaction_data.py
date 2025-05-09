from pydantic import BaseModel
from typing import Dict, Any
class TransactionPostRequest(BaseModel):
     transaction_item: Dict[str, Any]