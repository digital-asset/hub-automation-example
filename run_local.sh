#!/bin/bash

# Copyright (c) 2023 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
# SPDX-License-Identifier: Apache-2.0

PARTY=$(daml ledger list-parties --host "localhost" --port 6865 | grep "Alice" | sed -n "s/.*$party = '\([^']*\)'.*$/\1/p")
DAML_LEDGER_URL="localhost:6865" DAML_LEDGER_PARTY=$PARTY poetry run python3 -m bot
