# Copyright (c) 2023 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
# SPDX-License-Identifier: BD0

import logging
import os

import dazl
from dazl import connect
from dazl.ledger import CreateEvent, ArchiveEvent, Boundary, ExerciseCommand
from dazl.prim import Party


dazl.setup_default_logger(logging.INFO)

# This is the Package ID of the dar we want to follow contracts from.

# If this is not included, Dazl will stream _all_ templates that have the same name.
# Speifying exactly which package ID to use will make the bot not react to templates
# from models that are on the ledger that we are not interested in that
# may have the same template name, especially older versions of a model.

# This can be found by running `daml damlc -- inspect /path/to/dar | grep "package"`

package_id="d36d2d419030b7c335eeeb138fa43520a81a56326e4755083ba671c1a2063e76"

# Define the names of our templates for later reuse
class Templates:
    User = f"{package_id}:User:User"
    Alias = f"{package_id}:Alias:Alias"
    Notification = f"{package_id}:Notification:Notification"

async def main():
    logging.info("Starting up bot...")

    # These environment variables will always be set in a pythonbot running in hub, but adding defaults
    # here can be useful for running locally or making a typechecker happy

    # The URL path to the ledger you would like to connect to
    url = os.getenv('DAML_LEDGER_URL') or "localhost:6865"

    # The party that is running the automation. The party will _only_ be able to see and operate on
    # contracts that this party has access to via signatory or observer!
    party = os.getenv('DAML_LEDGER_PARTY') or "party"

    # Start up a dazl connection
    async with connect(url=url, act_as=Party(party)) as conn:

        # Stream both of our templates forever
        async with conn.stream_many([Templates.User, Templates.Alias, Templates.Notification]) as stream:

            # stream.events() will yield 'CreateEvent's when a contract is created, 'ArchiveEvent's
            # when a contract is archived and 'Boundary' events once the stream has caught up to the current
            # end of the ledger.

            # The Boundary can be helpful helpful when starting a stream on ledger that already has data,
            # since the stream will stream the current state of the ledger. The boundary event has an 'offset'
            # parameter that can be passed to 'conn.stream_many' and only begin the stream from that point

            async for event in stream.items():
               if isinstance(event, CreateEvent):
                   logging.info(f"Noticed a {event.contract_id.value_type} contract: {event.payload}")

                   # When the contract that was of the Notification template is created, automatically exercise the "Acknowledge" choice
                   if str(event.contract_id.value_type) == Templates.Notification:
                       await conn.exercise(event.contract_id, "Acknowledge", {})

               elif isinstance(event, ArchiveEvent):
                    logging.info(f"Noticed that a {event.contract_id.value_type} contract was deleted")
               elif isinstance(event, Boundary):
                   logging.info(f"Up to date on the current state of the ledger at offset: {event.offset}")
