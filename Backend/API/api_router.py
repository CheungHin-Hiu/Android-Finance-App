import fastapi 


# import request/response schema models
from ..API.model.GET_finance_data import RequestFinanceData



class APIRouteDefintion:
    def __init__(self):
        pass

    # endpoint: _____/finance, method: GET
    async def GET_finance_data(self):
    
