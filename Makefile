VERSION := $(shell grep version pyproject.toml | head -n 1 | awk -F\" '{print $$2}')
BUILD_OUTPUT := dist/bot-$(VERSION).tar.gz

NAME := hub-example-bot

TARGET := $(NAME)-$(VERSION).tar.gz


SRC := $(wildcard bot/*.py)

all: $(TARGET)

$(TARGET): $(BUILD_OUTPUT)
	cp $< $@

$(BUILD_OUTPUT): $(SRC) pyproject.toml poetry.lock
	poetry build

clean:
	rm -rf dist
	rm -f $(TARGET)
