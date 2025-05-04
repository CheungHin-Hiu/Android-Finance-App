from abc import ABC, abstractmethod
from ..token.encryption import hash_password, verify_password
from ..token.access_token import JWTGenerator
from pymongo.mongo_client import MongoClient
class AuthController(ABC):


    def __init__(self, database_entity:MongoClient) -> None:
        super().__init__()
        self.token_generator = JWTGenerator()
        self._user_credential_collection = database_entity["COMP4521"]["credential"]
        # print(self._user_credential_collection)


class LoginController(AuthController):

    def __init__(self, database_entity: MongoClient) ->None:
        super().__init__(database_entity)
        

    async def verify_credential(self, request_entity: dict):
        username = request_entity.get('username')
        password = request_entity.get('password')
        token = request_entity.get('token')

        if not (username and password) and not token:
            return {'status': 400, 'error':'must have username and password or token as input'}


        # check for token first (without username and password)
        if token != None:
            decoded_payload = self.token_generator.verify_jwt_token(token=token)
            if not decoded_payload:
                return { 'status':200, 'username': username, 'token': token}
        
        # check for username and password if token expired 
        try:
            user_information = self._user_credential_collection.find_one({"username": username}) # need to chaneg to the db controller get function
           
        except Exception as e:
    
            return {'status':400, 'error': 'Invalid Login Credential'}

       

        try:         
            user_id = user_information.get('_id')
            hashed_password = user_information.get('password')
            isPwdCorrect = verify_password(password, hashed_password)
            
        except Exception as e:

            return {"status": 500, 'error': 'internal server error'}

        if not isPwdCorrect:
            return {'status':400, "error": 'Invalid Password'} 
        
        token_generator_payload = {
            'user_id': str(user_id),
            'username': str(username)
        }
        try:
            new_token = self.token_generator.create_jwt_token(token_generator_payload)
        except Exception as e:
       
             return {"status": 500, 'error': 'internal server error'}

        return { 'status':200, 'user_id': str(user_id),'username': username, 'token': new_token}


        
class RegisterController(AuthController):
    def __init__(self, database_entity) ->None:
        super().__init__(database_entity)


    async def register_credential (self, request_entity: dict):
        username = request_entity.get('username')
        password = request_entity.get('password')

        if not( username and password ): 
            return {'status': 400, 'error': 'must have username and password as input'}
        
        try:
            isUserExist = self._user_credential_collection.find_one({"username": username}) # update to the find function for that db
            if(isUserExist):
                return {'status': 400, 'error':'username already exist'}

        except Exception as e:
            return {"status": 500, 'error': 'internal server error'}


        
        hashed_password = hash_password(password)
        try:
            user_information = {
                "username": username,
                "password": hashed_password
            }
            user_inserted =self._user_credential_collection.insert_one(user_information) # update to the insert function for that db controller
            user_id = user_inserted.inserted_id
        except Exception as e:
            print(e)
            return {"status": 500, 'error': 'internal server error'}
        
        token_generator_payload = {
            'user_id': str(user_id),
            'username': str(username)
        }
        new_token = self.token_generator.create_jwt_token(token_generator_payload)
        return {'status': 200, 'user_id': str(user_id),'username': username, 'token':new_token}

        
