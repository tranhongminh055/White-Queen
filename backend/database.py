import os
from motor.motor_asyncio import AsyncIOMotorClient
from dotenv import load_dotenv

load_dotenv()

MONGODB_URL = os.getenv("MONGODB_URL", "mongodb://localhost:27017")
DATABASE_NAME = os.getenv("DATABASE_NAME", "white_queen_db")

from typing import Optional

class Database:
    client: Optional[AsyncIOMotorClient] = None
    db = None

db_state = Database()

async def connect_to_mongo():
    db_state.client = AsyncIOMotorClient(MONGODB_URL)
    db_state.db = db_state.client[DATABASE_NAME]

async def close_mongo_connection():
    if db_state.client:
        db_state.client.close()

async def get_database():
    return db_state.db
