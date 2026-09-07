import asyncio
from motor.motor_asyncio import AsyncIOMotorClient
import os
from dotenv import load_dotenv

load_dotenv(dotenv_path="backend/.env")
MONGODB_URL = os.getenv("MONGODB_URL")

async def main():
    client = AsyncIOMotorClient(MONGODB_URL)
    db = client["white_queen_db"]
    user = await db.users.find_one({"email": "vitconcuaminhtran@gmail.com"})
    print("User found:", user)
    
asyncio.run(main())
