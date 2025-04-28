import fastapi 


# import request/response schema models
from ..API.model.GET_finance_data import RequestFinanceData



class APIRouteDefintion:
    def __init__(self, router: fastapi.APIRouter):
        self.router = router
        
        # route defintion
        self.router.add_api_route("/login", self._get_login_operation, methods=["GET"])



    # endpoint: _____/finance, method: GET
    async def _get_login_operation(self):
        pass

    # endpoint: _____/finance, method: GET
    async def _get_register_operation(self):
        pass


    # endpoint: _____/finance, method: GET
    async def _get_finance_data(self):
        pass




