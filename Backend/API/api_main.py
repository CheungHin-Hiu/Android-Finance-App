from fastapi import FastAPI
from fastapi import APIRouter
from fastapi.middleware.cors import CORSMiddleware

from ..API.api_router import APIRouteDefintion
app = FastAPI()

# Cors settings (later config)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"], 
    allow_credentials=True, 
    allow_methods=["*"],  
    allow_headers=["*"],  
)

api_router = APIRouter()
APIRouteDefintion(api_router)







