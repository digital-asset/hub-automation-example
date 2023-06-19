# Example Python Bot

This repository has a simple skeleton example of a Python automation that can be run in Daml Hub. This can be copied and then adjusted to your application.

Note that the Daml model in `example-model` is for reference and does not need to be copied when using this code as a template.

### To build
`make all`

Running this will use [poetry](https://python-poetry.org/docs/#installing-with-the-official-installer) to build and package a `.tar.gz` file with the bot. It will then copy the file with the correct version and name (as set in the `Makefile`).

### To run locally
`DAML_LEDGER_PARTY="party::1234" poetry run python3 -m bot` will run the bot. Since `localhost:6865` is set as a default, you do not need to set the ledger URL, however `DAML_LEDGER_PARTY` must be set to a party that is allocated on the Daml ledger you are testing with - which will always be slightly different on Canton ledgers due to fingerprinting. `run_local.sh` can be used to dynamically fetch the `Alice` party and start the bot with it.

## Structure
A Hub Pythonbot should always module named `bot` as it is run on Hub with `python3 -m bot`.

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

### ____init__.py__
The file with the main portion of the code (here `pythonbot_example.py`) can have any name, but must be imported in `__init__.py` where the `main` function should be called. This file will be run when the bot is initialized:

```python
from .pythonbot_example import main
from asyncio import run

run(main())
```

## External Connectivity
Python Automaations running on ledgers owned by Enterprise users can connect to services running on the internet outside of Daml Hub. The outgoing IP address is dynamically set.

For incoming connections, Daml Hub provides a webhookk URL of`http://{ledgerId}.daml.app/pythonbot/{instanceId}/`. This link can be copied from the Status Page for the running instance.

If you would like to accept traffic to that endpoint, you can run an `aiohttp` (or other Python) webserver running on the default `0.0.0.0:8080`. A request pointed directly to the webhook URL will be routed to the root `/` of your server.


## Bot code
Python automation running in Daml Hub generally use the [Dazl library](https://github.com/digital-asset/dazl-client) to react to incoming Daml contracts.

### Package IDs:
Dazl recognizes template names in the format of `package_id:ModuleName.NestedModule:TemplateName`:

```python
package_id="d36d2d419030b7c335eeeb138fa43520a81a56326e4755083ba671c1a2063e76"

# Define the names of our templates for later reuse
class Templates:
    User = f"{package_id}:User:User"
    Alias = f"{package_id}:Alias:Alias"
```
The "Package ID" specifies the dar that we want to follow contracts from. If this is not included, Dazl will stream _all_ templates that have the same name. Including the package ID to use will make the bot not react to templates from models that are on the ledger that we are not interested in that may have the same template name, especially older versions of a model.

The Package ID of a dar can be found by running `daml damlc -- inspect /path/to/dar | grep "package"`

### Main method:

After defining the templates, the example bot in this repository sets up a stream that runs forever, and sends a log message when a contract is created or deleted, or when the stream has reached the current state of the ledger. If the contract that was created was a Notification, it will automatically exercise the `Acknowledge` choice:
```python
    # Start up a dazl connection
    async with connect(url=url, act_as=Party(party)) as conn:

        # Stream both of our templates forever
        async with conn.stream_many([Templates.User, Templates.Alias]) as stream:
             async for event in stream.events():
                if isinstance(event, CreateEvent):
                    logging.info(f"Noticed a {event.contract_id.value_type} contract: {event.payload}")

                    if str(event.contract_id.value_type) == Templates.Notification:
                        await conn.exercise(event.contract_id, "Acknowledge", {})

                elif isinstance(event, ArchiveEvent):
                     logging.info(f"Noticed that a {event.contract_id.value_type} contract was deleted")
                elif isinstance(event, Boundary):
                    logging.info(f"Up to date on the current state of the ledger at offset: {event.offset}")
```

`stream.events()` will yield a `CreateEvent` when a contract is created, `ArchiveEvent` when a contract is archived, and a `Boundary` event once the stream has caught up to the current end of the ledger.

The `Boundary` can be helpful helpful when starting a stream on ledger that already has data, since the stream will stream the current state of the ledger. The boundary event has an 'offset' parameter that can be passed to 'conn.stream_many' and only begin the stream from that point.

`conn.exercise` was used in this example, but `create_and_exercise`, `exercise_by_key` and `create` commands are also available.

`dazl` also  has `query`/`query_many` which will continue the program once the query is through instead of continuing to stream. Commands can also be defined and later submitted together with other commands as a single transaction. The following example queries for all current `Notification` templates, then submits all Acknowledge commands together:
```python

    commands = []

   # Query only `Notification` contracts and build a list of "Acknowledge" commands
   async with conn.query(Templates.Notification) as stream:
       async for event in stream.creates():
           commands.append(ExerciseCommand(event.contract_id, "Acknowledge", {}))

   await conn.submit(commands)

```

# Sample code

**This repo contains sample code to help you get started with DAml. Please bear
in mind that it is provided for illustrative purposes only, and as such may not
be production quality and/or may not fit your use-cases. You may use the
contents of this repo in parts or in whole according to the BSD0 license:**

> Copyright © 2022 Digital Asset (Switzerland) GmbH and/or its affiliates
>
> Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted.
>
> THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
