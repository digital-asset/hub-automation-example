# Copyright (c) 2024 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
# SPDX-License-Identifier: 0BSD

VERSION := $(shell mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
BUILD_OUTPUT := target/hub-example-javabot-$(VERSION)-jar-with-dependencies.jar

NAME := hub-example-javabot

TARGET := $(NAME)-$(VERSION).jar


SRC := $(wildcard src/*.java)

all: $(TARGET)

$(TARGET): $(BUILD_OUTPUT)
	cp $< $@

$(BUILD_OUTPUT): $(SRC) pom.xml
	mvn install

clean:
	rm -rf dist
	rm -f $(TARGET)
