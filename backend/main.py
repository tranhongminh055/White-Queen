from fastapi import FastAPI
from dotenv import load_dotenv
import logging

from routers import users, chat

load_dotenv()

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

from contextlib import asynccontextmanager
from database import connect_to_mongo, close_mongo_connection

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup
    logger.info("Connecting to MongoDB...")
    await connect_to_mongo()
    logger.info("Successfully connected to MongoDB.")
    yield
    # Shutdown
    logger.info("Disconnecting from MongoDB...")
    await close_mongo_connection()

app = FastAPI(title="White Queen AI Backend", lifespan=lifespan)

# Include routers
app.include_router(users.router)
app.include_router(chat.router)

@app.get("/")
def read_root():
    return {"message": "Welcome to White Queen AI API - MongoDB Architecture"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
