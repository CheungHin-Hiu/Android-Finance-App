import fastapi 
# import request/response schema models
from API.model.GET_finance_data import RequestFinanceData
from API.model.GET_login_request import LoginRequest
from API.model.GET_register_request import RegisterRequest
from API.model.GET_transactions import TransactionsGetRequest
# from API.model.POST_transaction_item import TransactionPostRequest
from services.authentication.controller.auth_controller import LoginController, RegisterController
# from services.finance.finance_data_scraper import get_batch_yahoo_stock_data
from services.transaction.transaction import TransactionController

from pymongo.mongo_client import MongoClient


class APIRouteDefintion:
    def __init__(self, router: fastapi.APIRouter, database_client: MongoClient):
        self.router = router
        self.database_client = database_client
        self.login_controller = LoginController(database_entity=database_client)
        self.register_controller = RegisterController(database_entity=database_client)
        self.transaction_controller = TransactionController(database_entity= database_client)

        # route defintion
        self.router.add_api_route("/login", self._get_login_operation, methods=["POST"])
        self.router.add_api_route("/register", self._get_register_operation, methods=["POST"])
        # self.router.add_api_route("/finance", self._get_finance_data_operation, methods=["GET"])
        self.router.add_api_route("/transaction",  self._get_transactions_by_user, methods=["GET"])
        # self.router.add_api_route("/transaction",  self._post_transaction_data, methods=["POST"])
    # endpoint: _____/login, method: GET
    async def _get_login_operation(self, request_entity: LoginRequest):
        login_payload = request_entity.model_dump()
        return await self.login_controller.verify_credential(login_payload)


    # endpoint: _____/register, method: GET
    async def _get_register_operation(self, request_entity: RegisterRequest ):
        register_load = request_entity.model_dump()
        return await self.register_controller.register_credential(register_load)
    



    # endpoint: _____/finance, method: GET
    # async def _get_finance_data_operation(self, request_entity: RequestFinanceData):
    #     requested_items = request_entity.model_dump()
    #     # can add currency here 

    #     return await get_batch_yahoo_stock_data(requested_items['stock'])
    
    # endpoint: _____/transaction, method: POST
    # async def _post_transaction_data(self, request_entity: TransactionPostRequest ):
    #     transaction_item = request_entity.model_dump()
    #     return await self.transaction_controller.insert_transaction(transaction_item['user_id'], transaction_item['transaction_item'])

    # endpoint: _____/transaction, method: GET
    async def _get_transactions_by_user(self, request_entity: TransactionsGetRequest ):
        transaction_item = request_entity.model_dump()
        return await self.transaction_controller.get_transactions_by_user(transaction_item['user_id'])

