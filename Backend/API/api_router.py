import fastapi 
# import request/response schema models
from API.model.GET_finance_data import RequestFinanceData
from API.model.GET_login_request import LoginRequest
from API.model.GET_register_request import RegisterRequest
from API.model.GET_transactions import TransactionsGetRequest
# from API.model.POST_assest_by_user import tra
from API.model.POST_assest_by_user import InsertAssetRequest
from API.model.PUT_asset_update import UpdateAssetRequest
from API.model.DELETE_asset_by_item import DeleteAssetRequest
from API.model.POST_transaction_data import TransactionPostRequest
# from API.model.POST_transaction_item import TransactionPostRequest
from API.model.POST_target import TargetPostRequest

from services.authentication.controller.auth_controller import LoginController, RegisterController
from services.finance.finance_data_scraper import get_finance_data
from services.finance.currency_conversion import currency_conversion, items_currency_conversion
from services.transaction.transaction import TransactionController
from services.user_target.target import TargetController
from services.assets.asset import AssetController

from pymongo.mongo_client import MongoClient

from fastapi import HTTPException

class APIRouteDefintion:
    def __init__(self, router: fastapi.APIRouter, database_client: MongoClient):
        self.router = router
        self.database_client = database_client
        self.login_controller = LoginController(database_entity=database_client)
        self.register_controller = RegisterController(database_entity=database_client)
        self.transaction_controller = TransactionController(database_entity= database_client)
        self.target_controller = TargetController(database_entity= database_client)
        self.assets_controller = AssetController(database_entity=database_client)

        # route defintion
        self.router.add_api_route("/login", self._get_login_operation, methods=["POST"])
        self.router.add_api_route("/register", self._get_register_operation, methods=["POST"])

        self.router.add_api_route("/finance", self._get_finance_data_operation, methods=["POST"])
        self.router.add_api_route("/finance/USD{to_currency}", self._get_usd_conversion_rate, methods=["GET"])

        self.router.add_api_route("/transaction/{token}/{currency}",  self._get_transactions_by_user, methods=["GET"])
        self.router.add_api_route("/transaction/{token}",  self._post_transaction_data, methods=["POST"])

        self.router.add_api_route("/asset/{token}/{currency}", self._get_assest_by_user, methods=["GET"])
        self.router.add_api_route("/asset/{token}", self._add_assest_by_user, methods=["POST"])
        self.router.add_api_route("/asset/{token}", self._modify_assest_by_item, methods=["PUT"])
        self.router.add_api_route("/asset/{token}", self._delete_asset_by_item, methods=["DELETE"])
        
        self.router.add_api_route("/target/{token}/{currency}", self._get_targets_by_user, methods=["GET"])
        self.router.add_api_route("/target", self._insert_target, methods=["POST"]) 
        self.router.add_api_route("/target/{token}", self._delete_target_by_user, methods=["DELETE"])


    # endpoint: _____/login, method: GET
    async def _get_login_operation(self, request_entity: LoginRequest):
        login_payload = request_entity.model_dump()
        return await self.login_controller.verify_credential(login_payload)

    # endpoint: _____/register, method: GET
    async def _get_register_operation(self, request_entity: RegisterRequest ):
        register_load = request_entity.model_dump()
        return await self.register_controller.register_credential(register_load)
    

    # endpoint: _____/finance, method: POST
    async def _get_finance_data_operation(self, request_entity: RequestFinanceData):
        requested_items = request_entity.model_dump()
        finance_data_response = await get_finance_data(
            currencies=requested_items['currency'], stocks=requested_items['stock'], cryptos=requested_items['crypto']
        )
        return finance_data_response
    
    # endpoint: _____/finance/USD{to_currency}, method: GET
    async def _get_usd_conversion_rate(self, to_currency: str):
        finance_data = await get_finance_data()
        conversion_rates = finance_data["currency"]
        # check if conversion rate exists
        if f"USD{to_currency.upper()}=X" not in conversion_rates:
            raise HTTPException(status_code=404, detail="Conversion rate not found")
        conversion_rate = conversion_rates[f"USD{to_currency.upper()}=X"]
        return conversion_rate


    # endpoint: _____/transaction, method: GET
    async def _get_transactions_by_user(self, token:str, currency: str):
        return await self.transaction_controller.get_transactions_by_user(token, currency)
       
    # endpoint: _____/transaction, method: POST
    async def _post_transaction_data(self, token, request_entity: TransactionPostRequest ):
        transaction_item = request_entity.model_dump()
        return await self.transaction_controller.insert_transaction(token, transaction_item)


    # endpoint: _____/target, method: POST
    async def _insert_target(self, request_entity: TargetPostRequest):
        target_item = request_entity.model_dump()
        return await self.target_controller.insert_target(
            target_item['token'], target_item['target_type'], target_item['amount'], target_item['currency']
        )
    
    # endpoint: _____/target, method: GET
    async def _get_targets_by_user(self, token: str, currency: str):
        # check if token and currency are provided
        if not token or not currency:
            raise HTTPException(status_code=400, detail='Bad Request')
        
        targets = await self.target_controller.get_targets_by_user(token)

        # raise error if no targets found
        if (targets['status'] != 200):
            raise HTTPException(status_code=targets['status'], detail=targets['message'])
        
        return await items_currency_conversion(targets['targets'], currency.upper())

    # endpoint: _____/target, method: DELETE
    async def _delete_target_by_user(self, token: str):
        if not token:
            raise HTTPException(status_code=400, detail='Bad Request')
        return await self.target_controller.delete_target_by_user(token)


    async def _get_assest_by_user(self, token: str,currency:str):
        if not token:
            return {"status": 400}
        return await self.assets_controller.get_asset(token, currency)
    
    async def _add_assest_by_user(self, token:str, request_entity: InsertAssetRequest):
        if not token:
            return {"status": 400}
        assets = request_entity.model_dump()
        return await self.assets_controller.add_asset(token, assets)
    
    async def _modify_assest_by_item(self, token:str, request_entity: UpdateAssetRequest):
        if not token:
            return {"status": 400}
    
        return await self.assets_controller.modify_asset(token, request_entity)
    
    async def _delete_asset_by_item(self, token:str, request_entity:DeleteAssetRequest ):
        if not token:
            return {"status": 400}
        asset_id = request_entity.model_dump().get("id")
        return await self.assets_controller.del_asset(token, asset_id)
