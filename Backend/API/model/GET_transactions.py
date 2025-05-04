from pydantic import BaseModel

class TransactionsGetRequest(BaseModel):
    user_id: str