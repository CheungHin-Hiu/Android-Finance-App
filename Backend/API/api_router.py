import fastapi 
# import request/response schema models
from API.model.GET_finance_data import RequestFinanceData
from API.model.GET_login_request import LoginRequest
from API.model.GET_register_request import RegisterRequest
from services.authentication.controller.auth_controller import LoginController, RegisterController
from services.finance.finance_data_scraper import get_batch_yahoo_stock_data
from pymongo.mongo_client import MongoClient


class APIRouteDefintion:
    def __init__(self, router: fastapi.APIRouter, database_client: MongoClient):
        self.router = router
        self.database_client = database_client
        self.login_controller = LoginController(database_entity=database_client)
        self.register_controller = RegisterController(database_entity=database_client)

        # route defintion
        self.router.add_api_route("/login", self._get_login_operation, methods=["GET"])
        self.router.add_api_route("/register", self._get_register_operation, methods=["GET"])
        self.router.add_api_route("/finance", self._get_finance_data_operation, methods=["GET"])
   

    # endpoint: _____/login, method: GET
    async def _get_login_operation(self, request_entity: LoginRequest):
        login_payload = request_entity.model_dump()
        return await self.login_controller.verify_credential(login_payload)


    # endpoint: _____/register, method: GET
    async def _get_register_operation(self, request_entity: RegisterRequest ):
        register_load = request_entity.model_dump()
        return await self.register_controller.register_credential(register_load)
    



    # endpoint: _____/finance, method: GET
    async def _get_finance_data_operation(self, request_entity: RequestFinanceData):
        requested_items = request_entity.model_dump()
        
        return await get_batch_yahoo_stock_data(requested_items['stock'])





