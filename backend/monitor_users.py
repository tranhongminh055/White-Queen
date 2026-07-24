import os
import sys
import asyncio
from motor.motor_asyncio import AsyncIOMotorClient
from dotenv import load_dotenv

load_dotenv()

# Fix for Windows CMD Emoji printing
if hasattr(sys.stdout, "reconfigure"):
    sys.stdout.reconfigure(encoding="utf-8")  # type: ignore
MONGODB_URL = os.getenv("MONGODB_URL", "mongodb://localhost:27017")
DATABASE_NAME = os.getenv("DATABASE_NAME", "white_queen_db")


async def monitor_user_activity():
    print("==================================================")
    print("🟢 ĐANG THEO DÕI ĐĂNG NHẬP / ĐĂNG XUẤT REAL-TIME 🟢")
    print("==================================================")
    print(f"Đang kết nối tới database: {DATABASE_NAME}...")

    client = AsyncIOMotorClient(MONGODB_URL)
    db = client[DATABASE_NAME]

    print("Đã kết nối thành công! Bắt đầu lắng nghe sự kiện...\n")

    try:
        # Watch the users collection for any update operations
        async with db["users"].watch(
            [{"$match": {"operationType": "update"}}]
        ) as stream:
            async for change in stream:
                updated_fields = change["updateDescription"]["updatedFields"]
                document_id = change["documentKey"]["_id"]

                # We need to fetch the username to display it nicely
                user = await db["users"].find_one({"_id": document_id})
                username = user.get("username", "Unknown") if user else "Unknown"

                if "last_login" in updated_fields:
                    login_time = updated_fields["last_login"]
                    print(
                        f"✅ [ĐĂNG NHẬP] User '{username}' vừa online lúc {login_time}"
                    )

                if "last_logout" in updated_fields:
                    logout_time = updated_fields["last_logout"]
                    print(
                        f"🔴 [ĐĂNG XUẤT] User '{username}' vừa offline lúc {logout_time}"
                    )

    except Exception as e:
        print(f"Lỗi: {e}")
        print(
            "Lưu ý: Tính năng Change Streams yêu cầu MongoDB Replica Set (Atlas Cloud đã hỗ trợ mặc định)."
        )


if __name__ == "__main__":
    try:
        asyncio.run(monitor_user_activity())
    except KeyboardInterrupt:
        print("\n[HỆ THỐNG] Đã dừng chạy (do bạn bấm phím thoát hoặc Ctrl+C).")
