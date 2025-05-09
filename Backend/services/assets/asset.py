from pymongo.mongo_client import MongoClient
from bson import ObjectId
from datetime import datetime
from ..authentication.token.access_token import JWTGenerator

class AssetController:

    def __init__(self, database_entity: MongoClient):
        self._transaction_collection = database_entity['COMP4521']["assets"]
        self.token_generator = JWTGenerator()
    
    async def get_asset(self, token):
        user_payload = self.token_generator.verify_jwt_token(token)
        user_id = str(user_payload['user_id'])

        assets = list(self._transaction_collection.find({"user_id": user_id}))
        
        for asset in assets:
            
            asset["id"] = str(asset["_id"])
            asset.pop("_id", None)
            asset.pop("user_id", None)
        return assets
    
    async def add_asset(self, token, asset):
        user_payload = self.token_generator.verify_jwt_token(token)
        user_id = str(user_payload['user_id'])

        now = datetime.now()
        asset_doc = {
            "user_id": user_id,
            "category": asset.get("category"),
            "type": asset.get("type"),
            "amount": asset.get("amount"),
            "created_at": now,
            "updated_at": now
        }

        result = self._transaction_collection.insert_one(asset_doc)
        return {"status": 200, "id": str(result.inserted_id)}
    
    async def modify_asset(self, token, new_asset):
        user_payload = self.token_generator.verify_jwt_token(token)
        user_id = str(user_payload['user_id'])
        
        
        update_data= new_asset.dict(exclude_unset=True)
        asset_id = update_data.get("id")
        update_data.pop("id", None)
        update_data["updated_at"] = datetime.now()

        

        result = self._transaction_collection.update_one(
            {"_id": ObjectId(asset_id), "user_id": user_id},
            {"$set": update_data}
        )

        if result.matched_count == 0:
            return {"status": 400, "message": "Asset not found or unauthorized"}
        
        return {"status": 200, "message": "Asset updated successfully"}
    
    async def del_asset(self, token, asset_id):
        user_payload = self.token_generator.verify_jwt_token(token)
        user_id = str(user_payload['user_id'])

        result = self._transaction_collection.delete_one(
            {"_id": ObjectId(asset_id), "user_id": user_id}
        )

        if result.deleted_count == 0:
            raise  {{"status": 200}}
        
        return {{"status": 200, "message": "Asset Deleted"}}
