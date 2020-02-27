**Revolut Backend Code challenge**
This task is implementation of REST API for transaction management

**General Usage**
       
Get all accounts (GET /`api/account`)
Get accounts by id (GET `/api/account?id={id}`)
Create account (POST `/api/account`) and get created account in response

Get all transactions (GET `/api/transaction`)
Get transactions by id (GET `/api/transaction?id={id}`)
Create transaction between  (POST `/api/transaction`) two accounts 
Body example
 ` {
        "fromId": "7d81494c-efe2-443f-826e-aa5c8ed0b656",
        "toId": "ad4e947d-f126-4d1c-90e1-c4407f3efd6f",
        "amount": 255.0,
        "currency": "EUR"
  }`
  
  
**Running**
`./gradlew build
java -jar build/libs/transaction-managment-task-1.0.0-SNAPSHOT.jar
`  

