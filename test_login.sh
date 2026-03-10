#!/bin/bash
curl -X POST -d "firstname=test&lastname=test&username=testuser&password=Password1!&role=USER" http://localhost:8080/register -c cookies.txt
curl -X POST -d "username=testuser&password=Password1!" http://localhost:8080/login -b cookies.txt -c cookies.txt -L
