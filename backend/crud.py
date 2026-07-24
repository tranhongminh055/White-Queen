from motor.motor_asyncio import AsyncIOMotorDatabase
import schemas
import hashlib
from bson import ObjectId
from datetime import datetime, timezone

# ==========================================
# User CRUD (MongoDB)
# ==========================================
async def get_user(db: AsyncIOMotorDatabase, user_id: str):
    user = await db["users"].find_one({"_id": ObjectId(user_id)})
    return user

async def get_user_by_email(db: AsyncIOMotorDatabase, email: str):
    user = await db["users"].find_one({"email": email})
    return user

async def get_user_by_username(db: AsyncIOMotorDatabase, username: str):
    user = await db["users"].find_one({"username": username})
    return user

async def create_user(db: AsyncIOMotorDatabase, user: schemas.UserCreate):
    # Hash password (simple SHA-256 for example, use bcrypt in production)
    hashed_password = hashlib.sha256(user.password.encode()).hexdigest()
    user_dict = user.model_dump()
    user_dict.pop("password")
    user_dict["password_hash"] = hashed_password
    user_dict["created_at"] = datetime.now(timezone.utc)
    
    result = await db["users"].insert_one(user_dict)
    created_user = await db["users"].find_one({"_id": result.inserted_id})
    return created_user

# ==========================================
# ChatMessage CRUD (MongoDB)
# ==========================================
async def create_chat_message(db: AsyncIOMotorDatabase, chat: schemas.ChatMessageCreate, reply: str):
    chat_dict = chat.model_dump()
    chat_dict["reply"] = reply
    chat_dict["timestamp"] = datetime.now(timezone.utc)
    
    result = await db["chat_messages"].insert_one(chat_dict)
    created_chat = await db["chat_messages"].find_one({"_id": result.inserted_id})
    return created_chat

async def get_chat_history(db: AsyncIOMotorDatabase, user_id: str, skip: int = 0, limit: int = 50):
    cursor = db["chat_messages"].find({"user_id": user_id}).skip(skip).limit(limit)
    return await cursor.to_list(length=limit)
