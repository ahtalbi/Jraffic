JAVA_FX_VERSION := 21.0.6
JAVA_FX_URL := https://download2.gluonhq.com/openjfx/$(JAVA_FX_VERSION)/openjfx-$(JAVA_FX_VERSION)_linux-x64_bin-sdk.zip
LIB_DIR := lib
SOURCE := src/*.java

.PHONY: all build run

all: run

$(LIB_DIR):
	mkdir $(LIB_DIR)
	curl -L -o javafx.zip $(JAVA_FX_URL)
	unzip -q javafx.zip -d $(LIB_DIR)
	rm javafx.zip

build: $(LIB_DIR)
	javac --module-path $(LIB_DIR)/javafx-sdk-$(JAVA_FX_VERSION)/lib --add-modules javafx.controls -d out $(SOURCE)

run: build
	java --module-path $(LIB_DIR)/javafx-sdk-$(JAVA_FX_VERSION)/lib --add-modules javafx.controls -cp out Main
