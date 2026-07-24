from fastapi import APIRouter, Depends
from motor.motor_asyncio import AsyncIOMotorDatabase
import crud
import schemas
from database import get_database

router = APIRouter(
    prefix="/chat",
    tags=["chat"],
)

@router.post("/", response_model=schemas.ChatMessageResponse)
async def chat(request: schemas.ChatMessageCreate, db: AsyncIOMotorDatabase = Depends(get_database)):
    # TODO: Connect to OpenAI, Gemini, or a local LLM here
    # For now, we return a mock response
    user_message = request.message
    bot_reply = f"White Queen AI received: '{user_message}'. (This is a mock response, implement LLM integration here!)"
    
    # Save the interaction to MongoDB
    chat_record = await crud.create_chat_message(db=db, chat=request, reply=bot_reply)
    
    return chat_record

@router.get("/history/{user_id}")
async def get_chat_history(user_id: str, skip: int = 0, limit: int = 50, db: AsyncIOMotorDatabase = Depends(get_database)):
    history = await crud.get_chat_history(db, user_id=user_id, skip=skip, limit=limit)
    # We might need to map ObjectIds to strings if returning a list, but our Pydantic model handles it.
    return history
