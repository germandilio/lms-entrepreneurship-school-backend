#!/bin/bash

# Define variables
DURATION=60 # Duration of the test in seconds
CONCURRENCY=10 # Number of concurrent requests
HOST=

# Calculate end time
end=$((SECONDS+$DURATION))

# Define endpoints
ENDPOINT1="http://$HOST/api/v1/lessons/list?page=0&size=10"
ENDPOINT2="http://$HOST/api/v1/teams/list?page=0&size=10&sort=number,desc"

# Start load test
while [ $SECONDS -lt $end ]; do
  for ((i=1; i<=$CONCURRENCY; i++)); do
    # Alternate between endpoints
    if [[ $((i % 2)) -eq 0 ]]; then
      curl -s -o /dev/null -w "%{time_total}\n" "$ENDPOINT1" &
    else
      curl -s -o /dev/null -w "%{time_total}\n" "$ENDPOINT2" &
    fi
  done
  sleep 1
done

wait

# Print completion message
echo "Load test completed."