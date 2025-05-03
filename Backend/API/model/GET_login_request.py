from pydantic import BaseModel

class LoginRequest(BaseModel):
    username: str
    password: str
    access_token: str =None