from pymongo.mongo_client import MongoClient
from datetime import datetime, timezone
from ..authentication.token.access_token import JWTGenerator
class TransactionController():
    
    def __init__(self, database_entity:MongoClient) -> None:
        self._transaction_collection = database_entity['COMP4521']["transaction"]
        self.token_generator = JWTGenerator()


    async def insert_transaction(self, user_id: str , payload: dict) -> dict:
        user_payload = self.token_generator.verify_jwt_token(token)
        user_id = str(user_payload['user_id'])
    
        transaction_doc = {"token": user_id, **payload, "datetime":  datetime.now(timezone.utc)}
        result = self._transaction_collection.insert_one(transaction_doc)
        return {"status": 200, "transaction_id": str(result.inserted_id)}
    

    async def get_transactions_by_user(self, token: str) -> list[dict]:

        user_payload = self.token_generator.verify_jwt_token(token)
        user_id = str(user_payload['user_id'])
        cursor = self._transaction_collection.find({"user_id": user_id}).sort("datetime", -1)
        transactions = list(cursor)
        for transaction in transactions:
            transaction["transaction_id"] = str(transaction["_id"])
            transaction.pop("_id", None)
            if "datetime" in transaction:
                transaction["datetime"] = transaction["datetime"].isoformat()

        return transactions