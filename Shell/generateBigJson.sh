#!/bin/bash

MAX=15000
echo "{\"arr\":["
i=1
while [ $i -le $MAX ]; do
  echo -ne "{\"id\":$i, \"next\":$((i+1))}"
  if [ "$i" -ne "$MAX" ]; then
    echo ","
  fi
  let i=i+1
done
echo "]}"