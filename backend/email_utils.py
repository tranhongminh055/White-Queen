import os
import random
import requests

def generate_otp() -> str:
    return str(random.randint(100000, 999999))

def send_otp_email(recipient_email: str, otp: str):
    resend_api_key = "re_JhonCSyr_67wAo3TFLdZeac79JDtzsiN9"
    subject = "Mã xác thực tài khoản White Queen"
    body = f"""
    <p>Xin chào,</p>
    <p>Bạn đang đăng ký tài khoản trên ứng dụng White Queen.</p>
    <p>Mã xác thực (OTP) của bạn là: <b>{otp}</b></p>
    <p>Vui lòng nhập mã này vào ứng dụng để hoàn tất đăng ký. Mã có hiệu lực trong 10 phút.</p>
    <p>Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email.</p>
    <p>Trân trọng,<br>White Queen Team</p>
    """

    headers = {
        "Authorization": f"Bearer {resend_api_key}",
        "Content-Type": "application/json"
    }
    
    payload = {
        "from": "White Queen App <onboarding@resend.dev>",
        "to": [recipient_email],
        "subject": subject,
        "html": body
    }

    try:
        response = requests.post("https://api.resend.com/emails", json=payload, headers=headers, timeout=15)
        response.raise_for_status()
        print(f"Email sent successfully to {recipient_email} via Resend")
    except Exception as e:
        print(f"Failed to send email to {recipient_email} via Resend: {e}")
        if 'response' in locals() and hasattr(response, 'text'):
            print(f"Resend error details: {response.text}")
        raise e
