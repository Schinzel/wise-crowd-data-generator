# Next steps
## How o handle data such as prices and asset data
But lets have the point of view that the real live data in the future might be like this
Users
Transactions
Holdings
Are uploaded to us from the bank. We most likely clean the data and could well put it in a database. For example a postgres.

Price series and Asset data are likely a services. For example a API at Morningstar

So do you think the data providers should be interfaces. For example ITransactions or IUsers that we send to the arguments to for example IInvestorSegmenter?

## First implementation of ICustomerQualifier
A list of qualifier filter

## First implementation of ICustomerQualifier
A set of IRankers IRank or something like that
