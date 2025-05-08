from pymongo.mongo_client import MongoClient
from datetime import datetime, timezone


class TargetController:

    def __init__(self, database_entity: MongoClient) -> None:
        self._target_collection = database_entity["COMP4521"]["target"]

    async def insert_target(
        self, user_id: str, target_type: str, amount: float, currency: str
    ) -> dict:
        # check if required fields are present in payload
        if not user_id or not target_type or amount==None or not currency:
            return {"status": 400, "message": "Missing required fields"}

        target_doc = {
            "user_id": user_id,
            "target_type": target_type,
            "amount": amount,
            "currency": currency.upper(),
            "datetime": datetime.now(timezone.utc),
        }

        result = self._target_collection.replace_one(
            {"user_id": user_id, "target_type": target_type},
            target_doc,
            upsert=True,
        )
        # check if target is added/updated
        if result.modifiedCount == 0 and result.upsertedId == None: 
            return {"status": 500, "message": "Failed to update target"}
        
        return {"status": 200}


    async def get_targets_by_user(self, user_id: str) -> dict:
        cursor = self._target_collection.find({"user_id": user_id})
        targets = list(cursor)

        if not targets:
            return {"status": 404, "message": "No targets found for the user"}
        
        for target in targets:
            target['_id'] = str(target['_id'])  # Convert ObjectId to string
            target['datetime'] = target['datetime'].isoformat()  # Convert datetime to ISO format
        
        return {"status": 200, "targets": targets}


    async def delete_target_by_user(self, user_id: str) -> dict:
        result = self._target_collection.delete_many({"user_id": user_id})

        if result.deleted_count == 0:
            return {"status": 404, "message": "No targets found for the user"}
        
        return {"status": 200, "message": f"Deleted {result.deleted_count} targets for user {user_id}"}
