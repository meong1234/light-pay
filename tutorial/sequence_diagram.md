# Logic Sequence Diagram
 This script can be shown as Sequence Diagram on `https://mermaidjs.github.io/mermaid-live-editor`

 ### Register Customer
```mermaid
sequenceDiagram
    participant Customer
    participant Gateway
    participant Wallets
    participant Accounts

    Customer->>Gateway: Register Customer
    Gateway->>Accounts: Create Customer Account
    Accounts->>Gateway:Customer Account Created
    Gateway->>Wallets: Create Wallet
    Wallets->>Gateway: Wallet Created
    Gateway->>Customer: Customer Registered
```

### Register Merchant
```mermaid
sequenceDiagram
    participant Admin
    participant Gateway
    participant Wallets
    participant Accounts

    Admin->>Gateway: Register Merchant
    Gateway->>Accounts: Create Merchant Account
    Accounts->>Gateway:Merchant Account Created
    Gateway->>Wallets: Create Wallet
    Wallets->>Gateway: Wallet Created
    Gateway->>Admin: Merchant Registered
```

### Topup
```mermaid
sequenceDiagram
    participant Bank
    participant Gateway
    participant Wallets
    participant Transactions

    Bank->>Gateway: Topup

    Gateway->>Wallets: Get Wallet by UserID
    Wallets->>Gateway: WalletID

    Gateway->>Transactions: Initiate Topup Transaction
    Transactions->>Gateway: Topup Transaction Initiated

    Gateway->>Wallets: Topup Wallet
    Wallets->>Gateway: Wallet Topup Success 

    Gateway->>Transactions: Completing Topup Transaction
    Transactions->>Gateway: Topup Transaction Completed

    Gateway->>Bank: Topup Success
```

### Pay
```mermaid
sequenceDiagram
    participant Merchant
    participant Gateway
    participant Wallets
    participant Transactions

    Merchant->>Gateway: Pay

    Gateway->>Wallet: Get Wallet by MerchantID
    Wallet->>Gateway: Merchant WalletID

    Gateway->>Wallet: Get Wallet by CustomerID
    Wallet->>Gateway: Customer WalletID

    Gateway->>Transactions: initiate Pay Transaction
    Transactions->>Gateway: Pay Transaction Initiated

    Gateway->>Wallet: Transfer Balance
    Wallet->>Gateway:  Balance Transferred

    Gateway->>Transactions: completting Pay Transaction
    Transactions->>Gateway: Pay Transaction Completed

    Gateway->>Merchant: Payment Success
```

 # Common Sequence Diagram

 ### Create Entity Flow
```mermaid
sequenceDiagram
    participant Client
    participant Service
    participant Entity
    participant Repository
    participant Database
    
   Client->>Service: Create

   Service->>Entity: Initiate new Entity
   Entity->>Service: Entity

   Service->>Repository: Insert Entity

   Repository->>Database: SQL [Insert into xx table]
   Database->>Repository: Data inserted

   Repository->>Service: Entity Inserted

   Service->>Client: Created
```

  ### Find Entity Flow
```mermaid
sequenceDiagram
    participant Client
    participant Service
    participant Repository
    participant Mapper
    participant Entity
    participant Database
    
   Client->>Service: getDataByID

   Service->>Repository: Find Entity by ID

   Repository->>Database: SQL [Select * from xx table]
   Database->>Repository: Database Data return

   Repository->>Mapper: Convert Database Data to Entity
   Mapper->>Entity: Initiate Entity from Data
   Entity->>Mapper: Entity
   Mapper->>Repository: Entity

   Repository->>Service: Entity

   Service->>Client: Data
```