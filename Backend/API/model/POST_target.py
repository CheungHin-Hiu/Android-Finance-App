from pydantic import BaseModel

class TargetPostRequest(BaseModel):
    token: str
    target_type: str
    amount: float
    currency: str