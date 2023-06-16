import logging
import os

from aiohttp import web

from dazl import connect
from dazl.ledger.aio import Connection
from dazl.ledger import CreateEvent, ArchiveEvent
from dazl.prim import Party

import random
import string
import sys
import time
import dazl
from dazl import create, exercise, exercise_by_key
from prometheus_client import Counter

dazl.setup_default_logger(logging.INFO)

# The Package ID of the dar we want to follow contracts from

package_id="d36d2d419030b7c335eeeb138fa43520a81a56326e4755083ba671c1a2063e76"

class Templates:
    User = f"{package_id}:User:User"
    Alias = f"{package_id}:Alias:Alias"

async def main():
    logging.info("Starting up bot...")
    url = os.getenv('DAML_LEDGER_URL') or "localhost:6865"
    party = os.getenv('DAML_LEDGER_PARTY') or "alice"

    if party is None or url is None:
        logging.error("Party and URL are not defined")
        return

    async with connect(url=url, act_as=Party(party)) as conn:
        async with conn.stream_many([Templates.User, Templates.Alias])  as stream:
             async for event in stream.events():
                if isinstance(event, CreateEvent):
                    logging.info(f"Noticed a {event.contract_id.value_type} contract: {event.payload}")
                elif isinstance(event, ArchiveEvent):
                     logging.info(f"Noticed that a {event.contract_id.value_type} contract was deleted")
