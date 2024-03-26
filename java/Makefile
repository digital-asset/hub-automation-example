# Copyright (c) 2024 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
# SPDX-License-Identifier: 0BSD

VERSION := $(shell mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
BUILD_OUTPUT := target/hub-example-automation-$(VERSION)-jar-with-dependencies.jar

NAME := hub-example-automation

TARGET := $(NAME)-$(VERSION).jar


# SRC := $(wildcard src/*.java)
SRC := $(call must-shell,find . -type f -and -name "*.java" -o -name "*.xml" -o -name "*.conf")

all: $(TARGET) daml-build daml-codegen

$(TARGET): $(BUILD_OUTPUT)
	cp $< $@

$(BUILD_OUTPUT): $(SRC) pom.xml
	mvn install

daml-build:
	(cd ../example-model && daml build && cd ../java)

daml-codegen:
	(cd ../example-model && daml codegen java && cd ../java)

daml-upload-dar:
	(cd ../example-model && daml ledger upload-dar && cd ../java)

daml-start:
	(cd ../example-model && daml start && cd ../java)

clean:
	rm -rf dist
	rm -f $(TARGET)
