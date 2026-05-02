#!/bin/bash
cd docker
echo "🔄 Restarting OTP Service..."
docker compose restart otp-service
echo "✅ Service restarted"
