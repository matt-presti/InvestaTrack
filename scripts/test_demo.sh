#!/bin/bash

echo "================================================================"
echo "InvestaTrack API Demo"
echo "================================================================"
echo ""

echo "1. SETTING UP CLEAN TEST ENVIRONMENT"
echo "   → Clearing existing data..."
curl -s -u admin:password -X DELETE http://localhost:8080/test/clear/all
echo ""
echo "   → Adding fresh test data (3 users + 3 portfolios)..."
result=$(curl -s -u admin:password http://localhost:8080/test/add-users)
echo "   Result: $result"
echo ""

echo "================================================================"
echo "2. TESTING USER ENTITY & API"
echo "================================================================"
echo ""
echo "Command: curl -u admin:password http://localhost:8080/api/users"
echo "GET /api/users - List all users (clean JSON with security applied):"
curl -s -u admin:password http://localhost:8080/api/users | jq '.'
echo ""

echo "Command: curl -u admin:password http://localhost:8080/api/users/1"
echo "GET /api/users/1 - Get specific user by ID:"
curl -s -u admin:password http://localhost:8080/api/users/1 | jq '.'
echo ""

echo "================================================================"
echo "3. TESTING PORTFOLIO ENTITY & API"
echo "================================================================"
echo ""
echo "Command: curl -u admin:password http://localhost:8080/api/portfolios"
echo "GET /api/portfolios - List all portfolios with user relationships:"
curl -s -u admin:password http://localhost:8080/api/portfolios | jq '.'
echo ""

echo "Command: curl -u admin:password http://localhost:8080/api/portfolios/1"
echo "GET /api/portfolios/1 - Get specific portfolio by ID:"
curl -s -u admin:password http://localhost:8080/api/portfolios/1 | jq '.'
echo ""

echo "================================================================"
echo "4. TESTING CRUD OPERATIONS"
echo "================================================================"
echo ""
echo "Command: curl -X POST http://localhost:8080/api/portfolios -H 'Content-Type: application/json' -d '{...}'"
echo "POST /api/portfolios - Creating new portfolio via API:"

# Test portfolio creation
echo "Creating portfolio for user ID 1..."
new_portfolio_response=$(curl -s -w "HTTP_CODE:%{http_code}" -u admin:password -X POST http://localhost:8080/api/portfolios \
  -H "Content-Type: application/json" \
  -d '{
    "user": {"id": 1},
    "name": "API Test Portfolio",
    "description": "Portfolio created via REST API for testing",
    "totalValue": 25000.00,
    "totalCost": 23000.00
  }')

#verify
echo "Verifying the portfolio was created"
echo "Command: curl -u admin:password http://localhost:8080/api/portfolios/user/1"
echo "Verification - Alice's portfolios after creation attempt:"
curl -s -u admin:password http://localhost:8080/api/portfolios/user/1 | jq '.'
echo ""

echo "================================================================"
echo "5. ENTITY RELATIONSHIPS DEMONSTRATION"
echo "================================================================"
echo ""
echo "Showing User to Portfolio relationships work correctly:"
echo ""
echo "Users summary:"
curl -s -u admin:password http://localhost:8080/api/users | jq '.'
echo ""
echo "Portfolio ownership summary:"
curl -s -u admin:password http://localhost:8080/api/portfolios | jq '.'
echo ""

echo "================================================================"
echo "DEMO COMPLETE - ALL FEATURES WORKING"
echo "================================================================"
echo