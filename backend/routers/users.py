from fastapi import APIRouter, Depends, HTTPException
from motor.motor_asyncio import AsyncIOMotorDatabase
import crud
import schemas
from database import get_database
from bson.errors import InvalidId

router = APIRouter(
    prefix="/users",
    tags=["users"],
)

import datetime
from email_utils import generate_otp, send_otp_email

@router.post("/send-otp")
async def send_otp(request: schemas.SendOTPRequest, db: AsyncIOMotorDatabase = Depends(get_database)):
    # Check if email is already registered
    db_user = await crud.get_user_by_email(db, email=request.email)
    if db_user:
        raise HTTPException(status_code=400, detail="Email already registered")
        
    otp = generate_otp()
    # Save OTP to database with expiration (e.g., 10 minutes)
    await db["otps"].update_one(
        {"email": request.email},
        {"$set": {"otp": otp, "created_at": datetime.datetime.now(datetime.timezone.utc)}},
        upsert=True
    )
    
    # Send email
    send_otp_email(request.email, otp)
    return {"message": "OTP sent successfully"}

@router.post("/", response_model=schemas.UserResponse)
async def create_user(user: schemas.UserCreate, db: AsyncIOMotorDatabase = Depends(get_database)):
    db_user = await crud.get_user_by_email(db, email=user.email)
    if db_user:
        raise HTTPException(status_code=400, detail="Email already registered")
    
    db_user_by_name = await crud.get_user_by_username(db, username=user.username)
    if db_user_by_name:
        raise HTTPException(status_code=400, detail="Username already registered")
        
    # Verify OTP
    otp_record = await db["otps"].find_one({"email": user.email})
    if not otp_record or otp_record.get("otp") != user.otp:
        raise HTTPException(status_code=400, detail="Invalid or expired OTP")
        
    # Check expiration (10 minutes)
    if (datetime.datetime.now(datetime.timezone.utc) - otp_record.get("created_at").replace(tzinfo=datetime.timezone.utc)).total_seconds() > 600:
        raise HTTPException(status_code=400, detail="OTP has expired")
        
    # Create user
    created_user = await crud.create_user(db=db, user=user)
    
    # Delete OTP after successful registration
    await db["otps"].delete_one({"email": user.email})
    
    return created_user
@router.post("/login", response_model=schemas.UserResponse)
async def login_user(login_data: schemas.UserLogin, db: AsyncIOMotorDatabase = Depends(get_database)):
    import hashlib
    # Find user by either username or email
    db_user = await db["users"].find_one({
        "$or": [
            {"username": login_data.username},
            {"email": login_data.username}
        ]
    })
    
    if not db_user:
        raise HTTPException(status_code=404, detail="Username or email not found")
        
    hashed_password = hashlib.sha256(login_data.password.encode()).hexdigest()
    if db_user.get("password_hash") != hashed_password:
        raise HTTPException(status_code=401, detail="Incorrect password")
        
    # Update last_login
    await db["users"].update_one(
        {"_id": db_user["_id"]},
        {"$set": {"last_login": datetime.datetime.now(datetime.timezone.utc)}}
    )
    db_user["last_login"] = datetime.datetime.now(datetime.timezone.utc)
        
    return db_user

@router.post("/logout")
async def logout_user(logout_data: schemas.UserLogout, db: AsyncIOMotorDatabase = Depends(get_database)):
    # Find user
    db_user = await db["users"].find_one({
        "$or": [
            {"username": logout_data.username},
            {"email": logout_data.username}
        ]
    })
    
    if not db_user:
        raise HTTPException(status_code=404, detail="User not found")
        
    # Update last_logout
    await db["users"].update_one(
        {"_id": db_user["_id"]},
        {"$set": {"last_logout": datetime.datetime.now(datetime.timezone.utc)}}
    )
    
    return {"message": "Logged out successfully"}

@router.get("/{user_id}", response_model=schemas.UserResponse)
async def read_user(user_id: str, db: AsyncIOMotorDatabase = Depends(get_database)):
    try:
        db_user = await crud.get_user(db, user_id=user_id)
    except InvalidId:
        raise HTTPException(status_code=400, detail="Invalid user ID format")
        
    if db_user is None:
        raise HTTPException(status_code=404, detail="User not found")
    return db_user
