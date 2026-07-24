import smtplib
import os
import random
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart

def generate_otp() -> str:
    return str(random.randint(100000, 999999))

def send_otp_email(recipient_email: str, otp: str):
    sender_email = os.getenv("SMTP_EMAIL")
    sender_password = os.getenv("SMTP_PASSWORD")
    
    if not sender_email or not sender_password or sender_email == "your_email@gmail.com" or sender_password == "your_app_password":
        print(f"MOCK EMAIL SENT: OTP for {recipient_email} is {otp}")
        return

    subject = "Mã xác thực tài khoản White Queen"
    body = f"""
    Xin chào,
    
    Bạn đang đăng ký tài khoản trên ứng dụng White Queen.
    Mã xác thực (OTP) của bạn là: {otp}
    
    Vui lòng nhập mã này vào ứng dụng để hoàn tất đăng ký. Mã có hiệu lực trong 10 phút.
    Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email.
    
    Trân trọng,
    White Queen Team
    """

    msg = MIMEMultipart()
    msg['From'] = f"White Queen App <{sender_email}>"
    msg['To'] = recipient_email
    msg['Subject'] = subject
    msg.attach(MIMEText(body, 'plain'))

    try:
        server = smtplib.SMTP('smtp.gmail.com', 587, timeout=10)
        server.starttls()
        server.login(sender_email, sender_password)
        text = msg.as_string()
        server.sendmail(sender_email, recipient_email, text)
        server.quit()
        print(f"Email sent successfully to {recipient_email}")
    except Exception as e:
        print(f"Failed to send email to {recipient_email}: {e}")
        raise e
