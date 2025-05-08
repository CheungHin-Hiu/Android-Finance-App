from pydantic import BaseModel

class TargetGetRequest(BaseModel):
    user_id: str