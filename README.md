# Example Python Bot

## To build
`make all`

## External Connectivity
Python Automaations running on ledgers owned by Enterprise users can connect to services running on the internet outside of Daml Hub.

For incoming connections, Daml Hub provides a webhookk URL of`http://{ledgerId}.daml.app/pythonbot/{instanceId}/`. This link can be copied from the Status Page for the running instance. If you would like to accept traffic to that endpoint, you can run an `aiohttp` (or other Python) webserver running on the default `0.0.0.0:8080`. A request pointed directly to the webhook URL will be routed to the root `/` of your server.
