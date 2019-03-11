#!/bin/bash

MAX=10
echo -n "{\"message\":\""
i=1
while [ $i -le $MAX ]; do
  echo  -n "abcd" 
  let i=i+1
done
echo "\"}"