# BlockChain
Cryptocurrency exchange system implemented
Objective - Make a program/code for an order matching-engine that takes buy & sell orders
as inputs, matches them on a “price-priority” basis and update user’s balances.
Requisites - Use & code a balanced binary tree like AVL Tree, Red-Black Tree or others if you
think it’s better. Furthermore, implement the following functions in the program and indicate
their time complexities :
1) Add order - Takes as input a buy or sell order with quantity & price details, adds it to the
order book & returns the order id
2) Cancel All - Removes all existing orders
3) Cancel order - Removes order with a particular id from the order-book
4) Execute order - Takes as input an existing order id, matches it against the best available
price & updates balances
5) Get Lowest Sell - Returns the lowest available sell price in the order book
6) Get Highest Buy - Returns the highest available buy price in the order book
