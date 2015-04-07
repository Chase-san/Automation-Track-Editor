SRC_PATH = src
OBJ_PATH = obj
ARTIFACT = trackedit
EXE = .exe

ifndef BUILD

######################### BOOTSTRAP

.PHONY: debug release clean style

debug:
	@make --no-print-directory BUILD=1 DEBUG=1 STATIC=1 -f Makefile

release:
	@make --no-print-directory BUILD=1 RELEASE=1 STATIC=1 -f Makefile

clean:
	@echo Cleaning up.
	@rm -rf $(OBJ_PATH) $(ARTIFACT)$(EXE) $(ARTIFACT)d$(EXE) *.res

style:
	@echo Styling.
	@astyle -A2 -T -p -U -xe -k3 -W3 -j -n -S -Y -xC120 $(wildcard $(SRC_PATH)/*.c) $(wildcard $(SRC_PATH)/*.h)

#else BUILD
else

######################### MAIN MAKEFILE

CC = clang
CFLAGS = -std=c11 -Wall -Wextra -Wpointer-arith -Wwrite-strings -Wuninitialized -pedantic -Wno-unused-parameter
LDFLAGS = -static-libgcc -lmingw32

ifdef STATIC
LDFLAGS += -static
LDFLAGS += -lm -ldinput8 -ldxguid -ldxerr8 -luser32 -lgdi32 -lwinmm -limm32 -lole32 -loleaut32 -lshell32 -lversion -luuid
endif

ifdef RELEASE
TARGET = $(ARTIFACT)$(EXE)
CFLAGS += -g0 -O3
LDFLAGS += -Wl,--subsystem,windows
else
TARGET = $(ARTIFACT)d$(EXE)
CFLAGS += -g3 -DDEBUG
endif

#########################
SRCS = $(wildcard $(SRC_PATH)/*.c)
OBJS = $(SRCS:$(SRC_PATH)/%.c=$(OBJ_PATH)/%.o)
ifdef DEBUG
OBJS = $(SRCS:$(SRC_PATH)/%.c=$(OBJ_PATH)/%d.o)
endif
DEPS = $(OBJS:.o=.d)
RCS = $(wildcard $(SRC_PATH)/*.rc)
RES = $(RCS:$(SRC_PATH)/%.rc=$(OBJ_PATH)/%.res)

vpath %.c $(SRC_PATH)
vpath %.rc $(SRC_PATH)

.PHONY: build

build: $(OBJ_PATH) $(TARGET)
ifdef RELEASE
	@echo Stripping $(TARGET)
	@strip -s $(TARGET)
endif

$(OBJ_PATH):
	@mkdir -p $(OBJ_PATH)

$(TARGET): $(OBJS) $(RES)
	@echo Building $@
	@$(CC) $(OBJS) $(RES) $(LDFLAGS) -o $@

ifdef RELEASE
$(OBJ_PATH)/%.o: %.c
else
$(OBJ_PATH)/%d.o: %.c
endif
	@echo Compiling $<
	@$(CC) -c $(CFLAGS) -MMD -o $@ $<


$(OBJ_PATH)/%.res: %.rc
	@echo Building $<
	@windres $< -O coff -o $@

-include $(DEPS)

#endif BUILD
endif