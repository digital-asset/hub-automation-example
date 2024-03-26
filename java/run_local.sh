#!/bin/bash
USER="alice"
DAML_APPLICATION_ID=alice DAML_LEDGER_ID=sandbox DAML_LEDGER_URL=localhost:6865 DAML_USER_ID=alice java -jar target/hub-example-automation-0.0.1-jar-with-dependencies.jar
