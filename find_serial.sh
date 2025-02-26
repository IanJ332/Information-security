#!/bin/bash

# Define the path to the linear.exe file using Cygwin's path format
linear_path="/cygdrive/c/Users/User/Downloads/linear.exe"

for i in {111111..999999}
do
    echo "Trying serial number: $i"
    output=$("$linear_path" $i)
    if [[ $output == *"Serial number is correct"* ]]; then
        echo "Correct serial number is: $i"
        break
    fi
done
