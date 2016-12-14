# Cashier Service

The microservice implements API for the currency exchange cashier. Depends on the dynamic discovery of **__Currency Service__**

# API

| Method | Path | Description | User authenticated | Available from UI |
| --- | :--- | --- | :---: | :---: |
| GET | /exchange/[source]/[target]/[amount]/ | Returns the [amount] of the [source] currency in [target] one. | | × |