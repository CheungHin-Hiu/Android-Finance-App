from pydantic import BaseModel

class TransactionsGetRequest(BaseModel):
    token: str