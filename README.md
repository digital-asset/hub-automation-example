# Example Python Bot

This repository has a simple skeleton example of a Python automation that can be run in Daml Hub. This can be copied and then adjusted to your application.

## To build
`make all`

This will use `poetry` to build and package a `.tar.gz` file with the bot. It will then copy the file with the correct version and name (as set in the `Makefile`). 

## To run locally
`DAML_LEDGER_PARTY="party::1234" poetry run python3 -m bot` will run the bot. Since `localhost:6865` is set as a default, you do not need to set the ledger URL, however `DAML_LEDGER_PARTY` must be set to a party that is allocated on the Daml ledger you are testing with - which will always be slightly different on Canton ledgers due to fingerprinting. `run_local.sh` can be used to dynamically fetch the `Alice` party and start the bot with it.

## Structure
A Hub Pythonbot should always module named `bot` as it is run on Hub with `pooetry run python3 -m bot`.

### pyproject.toml
```toml
[tool.poetry]
name = "bot"
version = "0.1.0"
description = "Example of a Daml Hub Pythonbot"
authors = ["Digital Asset"]

[tool.poetry.dependencies]
python = "^3.7"
dazl = "^7.3.1"
```

### Directory Structure
```
├── bot
│   ├── __init__.py
│   └── pythonbot_example.py
├── poetry.lock
├── pyproject.toml
```

The file with the main portion of the code (here `pythonbot_example.py`) can have any name, but must be imported in `__init__.py` where the `main` function should be called. This file will be run when the bot is initialized:

### __init__.py
```python
from .pythonbot_example import main
from asyncio import run

run(main())
```

## External Connectivity
Python Automaations running on ledgers owned by Enterprise users can connect to services running on the internet outside of Daml Hub. The outgoing IP address is dynamically set.

For incoming connections, Daml Hub provides a webhookk URL of`http://{ledgerId}.daml.app/pythonbot/{instanceId}/`. This link can be copied from the Status Page for the running instance. 

If you would like to accept traffic to that endpoint, you can run an `aiohttp` (or other Python) webserver running on the default `0.0.0.0:8080`. A request pointed directly to the webhook URL will be routed to the root `/` of your server.
