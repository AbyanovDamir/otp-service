#!/bin/bash
cd docker
echo "🐳 Starting OTP Service..."
docker compose up -d
echo ""
echo "✅ Service started!"
echo "   API: http://localhost:8080"
echo "   pgAdmin: http://localhost:5050 (admin@otp-service.com / admin123)"
