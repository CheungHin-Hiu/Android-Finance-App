from pydantic import BaseModel

class TargetPostRequest(BaseModel):
    user_id: str
    target_type: str
    amount: float
    currency: str