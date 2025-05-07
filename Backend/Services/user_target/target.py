from pymongo.mongo_client import MongoClient
from datetime import datetime, timezone


class TargetController:

    def __init__(self, database_entity: MongoClient) -> None:
        self._target_collection = database_entity["COMP4521"]["target"]

    async def insert_target(
        self, user_id: str, target_type: str, amount: float, currency: str
    ) -> dict:
        # check if required fields are present in payload
        if not user_id or not target_type or not amount or not currency:
            return {"status": 400, "message": "Missing required fields"}

        target_doc = {
            "user_id": user_id,
            "target_type": target_type,
            "amount": amount,
            "currency": currency,
            "datetime": datetime.now(timezone.utc),
        }

        result = self._target_collection.insert_one(target_doc)
        return {"status": 200, "target_id": str(result.inserted_id)}

    async def get_targets_by_user(self, user_id: str) -> list[dict]:
        cursor = self._target_collection.find({"user_id": user_id}).sort(
            "creation_date", -1
        )
        targets = list(cursor)

        latest_targets = {}

        for target in targets:
            target_type = target["target_type"]
            if target_type not in latest_targets:
                target["target_id"] = str(target["_id"])
                target["creation_date"] = target["creation_date"].isoformat()
                latest_targets[target_type] = target

        return latest_targets
