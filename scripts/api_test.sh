#!/bin/bash

# Output file
OUTPUT_FILE="scripts/test_output.txt"

# Clear previous output
echo "Clearing old test data..."
curl -s -X DELETE http://localhost:8080/test/clear > /dev/null

echo "Seeding test data..."
LOAD_RESULT=$(curl -s http://localhost:8080/test/load)
echo "===== INVESTATRACK API TEST RESULTS =====" > "$OUTPUT_FILE"
echo "$(date)" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"
echo "Load Result:" >> "$OUTPUT_FILE"
echo "$LOAD_RESULT" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"

# Helper function
fetch_and_log() {
    local label=$1
    local url=$2
    RESPONSE=$(curl -s "$url")

    if [[ -z "$RESPONSE" || "$RESPONSE" == "[]" ]]; then
        echo "$label: (empty response)" >> "$OUTPUT_FILE"
        echo "" >> "$OUTPUT_FILE"
        return
    fi

    echo "$label:" >> "$OUTPUT_FILE"
    if command -v jq &> /dev/null; then
        echo "$RESPONSE" | jq '.' >> "$OUTPUT_FILE"
    else
        echo "$RESPONSE" >> "$OUTPUT_FILE"
    fi
    echo "" >> "$OUTPUT_FILE"
}

# === TRANSACTION VERIFICATION (NEW) ===
echo "=== TRANSACTION VERIFICATION ===" >> "$OUTPUT_FILE"
fetch_and_log "All Transactions (Raw Data)" http://localhost:8080/test/transactions

# === POSITION VERIFICATION (NEW) ===
echo "=== POSITION VERIFICATION ===" >> "$OUTPUT_FILE"
fetch_and_log "All Positions (Current Holdings)" http://localhost:8080/test/positions

# === PORTFOLIO ANALYSIS ===
echo "=== PORTFOLIO ANALYSIS ===" >> "$OUTPUT_FILE"
fetch_and_log "Portfolio Summary (High Level)" http://localhost:8080/test/summary

# === STOCK DATA ===
echo "=== STOCK DATA ===" >> "$OUTPUT_FILE"
fetch_and_log "All Stocks" http://localhost:8080/test/stocks

# === PORTFOLIO TRANSACTION SUMMARIES ===
echo "=== PORTFOLIO TRANSACTION SUMMARIES ===" >> "$OUTPUT_FILE"
fetch_and_log "Portfolio 1 Transaction Summary" http://localhost:8080/test/portfolio-summary/1
fetch_and_log "Portfolio 2 Transaction Summary" http://localhost:8080/test/portfolio-summary/2

# === USER DATA ===
echo "=== USER DATA ===" >> "$OUTPUT_FILE"
fetch_and_log "User 1 Details" http://localhost:8080/api/users/1
fetch_and_log "User 2 Details" http://localhost:8080/api/users/2


# === TRANSACTION VALIDATION ===
echo "=== TRANSACTION VALIDATION ===" >> "$OUTPUT_FILE"
echo "Planned Test Transactions:" >> "$OUTPUT_FILE"
echo "1. Alice buys 10 AAPL @ 190.23 (fee: 4.95)" >> "$OUTPUT_FILE"
echo "2. Alice buys 5 MSFT @ 310.00 (fee: 2.00)" >> "$OUTPUT_FILE"
echo "3. Alice sells 2 MSFT @ 310.00 (fee: 1.00)" >> "$OUTPUT_FILE"
echo "4. Bob buys 5 GOOGL @ 125.45 (fee: 3.95)" >> "$OUTPUT_FILE"
echo "5. Bob buys 2 AAPL @ 190.23 (fee: 2.50)" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"
echo "Total Expected: 5 transactions across 2 portfolios" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"



echo "Test complete. Results saved to ${OUTPUT_FILE}"

