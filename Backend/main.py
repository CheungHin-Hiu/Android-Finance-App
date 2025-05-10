
from fastapi import FastAPI
from fastapi import APIRouter
from fastapi.middleware.cors import CORSMiddleware
from pymongo.mongo_client import MongoClient
from pymongo.server_api import ServerApi
from dotenv import load_dotenv

import os

load_dotenv()
MONGO_URL = os.getenv("MONGO_URL")
print(MONGO_URL)

from API.api_router import APIRouteDefintion
app = FastAPI()


import sys
print(sys.executable)

print("started")
# Cors settings (later config)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"], 
    allow_credentials=True, 
    allow_methods=["*"],  
    allow_headers=["*"],  
)


client = MongoClient(MONGO_URL, server_api = ServerApi('1'))
try:
    client.admim.command("ping")
    print("<Mongo client> Mongo client creation successfully")
except Exception as e:
    print(e)



api_router = APIRouter()
APIRouteDefintion(api_router, client)
app.include_router(api_router)

