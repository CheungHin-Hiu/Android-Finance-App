from pymongo.mongo_client import MongoClient
from bson import ObjectId
from datetime import datetime
from ..authentication.token.access_token import JWTGenerator
from ..finance.currency_conversion import currency_conversion
from services.finance.finance_data_scraper import get_finance_data
class AssetController:

    def __init__(self, database_entity: MongoClient):
        self._transaction_collection = database_entity['COMP4521']["assets"]
        self.token_generator = JWTGenerator()
    
    async def get_asset(self, token, target_currency):
        user_payload = self.token_generator.verify_jwt_token(token)
        user_id = str(user_payload['user_id'])

        assets = list(self._transaction_collection.find({"user_id": user_id}))
        # print(f"token {token},  currency {currency}")
        
        usd_to_target_currency =  await currency_conversion("USD", target_currency.upper(), 1) 
        for asset in assets:

            asset["id"] = str(asset["_id"])
            if asset['category'].upper() == 'CURRENCY':
                asset["converted_amount"] = float(await currency_conversion(asset["type"].upper(), target_currency, asset["amount"]))
            elif asset["category"].upper() == 'STOCK' :
                result = await get_finance_data(stocks=[asset['type'].upper()])
                result = result['stock'][asset['type']][0]["Close"] * int(asset["amount"]) * usd_to_target_currency
                # print(result * usd_to_target_currency)
                asset["converted_amount"] = float(result)
                
            elif asset["category"].upper() == "CRYPTO":
                result = await get_finance_data(cryptos=[asset['type'].upper()])
                # result
                result = result['crypto'][asset['type'] + "-USD" ][0]["Close"] * int(asset["amount"]) * usd_to_target_currency
                asset["converted_amount"] = float(result)
            
            else:
                asset["converted_amount"] = float(await currency_conversion(asset["type"].upper(), target_currency, asset["amount"]))
            asset.pop("_id", None)
            asset.pop("user_id", None)
        return {"assets": assets}
    
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
            return {"status": 200}
        
        return {"status": 200, "message": "Asset Deleted"}
