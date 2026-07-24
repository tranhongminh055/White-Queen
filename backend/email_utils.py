import os
import random
import requests

def generate_otp() -> str:
    return str(random.randint(100000, 999999))

def send_otp_email(recipient_email: str, otp: str):
    script_url = "https://script.google.com/macros/s/AKfycbwYzxNzpfcUcdvChf8yh8sOQnhRJfMmTQ3kIT4e0X6xqv7BfOIRMRBXsfvFrzbplMXK4Q/exec"
    subject = "Mã xác thực tài khoản White Queen"
    body = f"""
    <p>Xin chào,</p>
    <p>Bạn đang đăng ký tài khoản trên ứng dụng White Queen.</p>
    <p>Mã xác thực (OTP) của bạn là: <b>{otp}</b></p>
    <p>Vui lòng nhập mã này vào ứng dụng để hoàn tất đăng ký. Mã có hiệu lực trong 10 phút.</p>
    <p>Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email.</p>
    <p>Trân trọng,<br>White Queen Team</p>
    """

    payload = {
        "to": recipient_email,
        "subject": subject,
        "body": body
    }

    try:
        response = requests.post(script_url, json=payload, timeout=15)
        response.raise_for_status()
        print(f"Email sent successfully to {recipient_email} via Google Apps Script")
    except Exception as e:
        print(f"Failed to send email to {recipient_email} via GAS: {e}")
        raise e
