import jwt
import os
from dotenv import load_dotenv
from datetime import datetime, timedelta

import jwt.exceptions


class JWTGenerator:
    def __init__ (self) -> None:
        load_dotenv()
        self._secret_key = os.getenv('JWT_SECRET_KEY')
        self._algorithm =  os.getenv('JWT_ALGORITHM')

    def create_jwt_token(self, payload: dict, ttl: timedelta = None ):
        
        payload['exp'] =  datetime.now() + timedelta(hours=1) 
        if ttl is not None:
            payload['exp'] =  datetime.now() + ttl
        token = jwt.encode(payload, self._secret_key, self._algorithm)
        return token

    def verify_jwt_token(self, token: str):
        try: 
            decoded = jwt.decode(token, self._secret_key,self._algorithm)
            return decoded
        
        except jwt.exceptions.ExpiredSignatureError:
            print("Token has expired")
            return False
        
        except jwt.exceptions.InvalidTokenError:
            print("'Invalid token'")
            return False
        
        except Exception as e:
            print(e)
            return False
            