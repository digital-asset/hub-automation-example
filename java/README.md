Copyright (c) 2024 Digital Asset (Switzerland) GmbH and/or its affiliates. All Rights Reserved. SPDX-License-Identifier: 0BSD

# Sample code

**This repo contains sample code to help you get started with Daml. Please bear
in mind that it is provided for illustrative purposes only, and as such may not
be production quality and/or may not fit your use-cases. You may use the
contents of this repo in parts or in whole according to the BSD0 license:**

> Copyright © 2024 Digital Asset (Switzerland) GmbH and/or its affiliates
>
> Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted.
>
> THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.

> **This repository does not accept Pull Requests at the moment.**

# Example Jvm Bot

This repository has a simple skeleton example of a Jvm Automations that can be run in Daml Hub. This can be copied and then adjusted to your application.

Note that the Daml model in `example-model` is for reference and does not need to be copied when using this code as a template.

### To build

```sh
  mvn install
```

Running this will use [maven](https://maven.apache.org/install.html) to build and package a `.tar.gz` file with the bot. It will then copy the file with the correct version and name (as set in the `Makefile`) into the root directory - the `.tar.gz` file is what will be uploaded to Daml Hub to run the automation.

### To run locally

```sh

```


## Structure


### pom.xml
```toml
```

### Directory Structure
```
```

### `__init__.py`

```java
```

## External Connectivity
Jvm Automations running on ledgers owned by Enterprise users can connect to services running on the internet outside of Daml Hub. The outgoing IP address is dynamically set.

For incoming connections, Daml Hub provides a webhook URL of`http://{ledgerId}.daml.app/automation/{instanceId}/`. This link can be copied from the Status Page for the running instance.

If you would like to accept traffic to that endpoint, you can run a webserver (such as with `aiohttp`) running on the default `0.0.0.0:8080`. A request pointed directly to the webhook URL will be routed to the root `/` of your server.


## Bot code
Jvm Automations running in Daml Hub generally use the [Dazl library](https://github.com/digital-asset/dazl-client) to react to incoming Daml contracts.

### Package IDs
Dazl recognizes template names in the format of `package_id:ModuleName.NestedModule:TemplateName`:

```java
```
The "Package ID" is the unique identifier of the dar that we want to follow contracts from. If this is not included, Dazl will stream _all_ templates that have the same name. Including the package ID will ensure that the bot only reacts to template from the Daml model that was specified. This is particularly important when a new version of a Daml model is uploaded to the ledger, since the names of the templates may remain the same.

The Package ID of a dar can be found by running `daml damlc -- inspect /path/to/dar | grep "package"`

### Environment variables
`dazl` requires a URL of the Daml ledger to connect to as well as a `Party` to act as. These wiill always be set as environment variables in Automations running in Daml Hub, but adding defaults can help with running locally.
```java

```
`DAML_LEDGER_PARTY` will be set as the party specified when deploying the automation. Note that this party will _only_ be able to see and operate on contracts that this party has access to via signatory or observer!

### Stream

After defining the templates, the example bot in this repository sets up a stream that runs forever, and sends a log message when a contract is created or deleted, or when the stream has reached the current state of the ledger. If the contract that was created was a Notification, it will automatically exercise the `Acknowledge` choice:
```java

```

`stream.items()` will yield a `CreateEvent` when a contract is created, `ArchiveEvent` when a contract is archived, and a `Boundary` event once the stream has caught up to the current end of the ledger.

The `Boundary` can be helpful helpful when starting a stream on ledger that already has data, since the stream will stream the current state of the ledger. The boundary event has an `offset` parameter that can be passed to `conn.stream_many` and only begin the stream from that point.

`conn.exercise` was used in this example, but `create_and_exercise`, `exercise_by_key` and `create` commands are also available.

### Query
`dazl` also  has `query`/`query_many` which will continue the program once the query is finished instead of continuing to stream. Commands can also be defined and later submitted together with other commands as a single transaction. The following example queries for all current `Notification` templates, then submits all Acknowledge commands together:
```java



```
