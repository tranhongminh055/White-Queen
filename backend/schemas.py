from pydantic import BaseModel, EmailStr, Field
from typing import Optional, Any
from datetime import datetime
from bson import ObjectId

class PyObjectId(ObjectId):
    @classmethod
    def __get_pydantic_core_schema__(
        cls, _source_type: Any, _handler: Any
    ) -> Any:
        from pydantic_core import core_schema
        return core_schema.json_or_python_schema(
            json_schema=core_schema.str_schema(),
            python_schema=core_schema.union_schema([
                core_schema.is_instance_schema(ObjectId),
                core_schema.chain_schema([
                    core_schema.str_schema(),
                    core_schema.no_info_plain_validator_function(cls.validate),
                ])
            ]),
            serialization=core_schema.plain_serializer_function_ser_schema(
                lambda x: str(x)
            ),
        )

    @classmethod
    def validate(cls, v):
        if not ObjectId.is_valid(v):
            raise ValueError("Invalid ObjectId")
        return ObjectId(v)

# ==========================================
# User Schemas
# ==========================================
class UserBase(BaseModel):
    username: str
    email: EmailStr

class UserCreate(UserBase):
    password: str
    otp: str

class SendOTPRequest(BaseModel):
    email: EmailStr

class UserLogin(BaseModel):
    username: str
    password: str

class UserLogout(BaseModel):
    username: str

class UserResponse(UserBase):
    id: PyObjectId = Field(default_factory=PyObjectId, alias="_id")
    created_at: datetime
    last_login: Optional[datetime] = None
    last_logout: Optional[datetime] = None

    class Config:
        populate_by_name = True
        json_encoders = {ObjectId: str}

# ==========================================
# ChatMessage Schemas
# ==========================================
class ChatMessageBase(BaseModel):
    message: str

class ChatMessageCreate(ChatMessageBase):
    user_id: str

class ChatMessageResponse(ChatMessageBase):
    id: PyObjectId = Field(default_factory=PyObjectId, alias="_id")
    user_id: str
    reply: str
    timestamp: datetime

    class Config:
        populate_by_name = True
        json_encoders = {ObjectId: str}
