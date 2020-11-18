### Problem Statement:

Suatu hari anda terfikir untuk membangun sebuah applikasi fintech yang dapat melakukan transaksi non tunai, sebagai Minimum Viable Productnya anda ingin applikasi ini memiliki feature berikut
 
 Transaksi : 
  - customer dapat melakukan pendaftaran 
  - sebagai owner anda dapat mendaftarkan merchant baru yang dapat melakukan transaksi 
  - customer dapat melakukan pengisian balance (Topup)
  - customer dapat melakukan pembayaran kepada merchant" yang telah terdaftar 

#### Meet the actor :
 - customer: 
 - merchant: 

 - transaksi 
    - customer dapat topup
    - customer dapat transaksi ke merchant yang telah terdaftar

Some Spec: 
```
GIVEN I am unregister person
WHEN i register as customer with (name, email, phone)
THEN e-wallet should record it as new customer and return my user-id

GIVEN I am owner of Ewallet
WHEN i register new merchant with (name, email)
THEN e-wallet should record it as new merchant and return merchant-id

GIVEN I am customer of Ewallet
WHEN i topup some amount
THEN e-wallet my balance should increased

GIVEN I am customer of Ewallet
WHEN i pay merchant 
THEN my balance should decreased, and merchant balance should increased, and i get transactionsID
```
