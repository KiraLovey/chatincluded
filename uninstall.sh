#!/usr/bin/env bash
# ChatIncluded Uninstaller for macOS and Linux

set -euo pipefail

PLUGIN_JAR="chatincluded-1.0.0.jar"
RECORD_DIR="$HOME/.local/share/chatincluded"
RECORD_FILE="$RECORD_DIR/install.conf"

# ── Colours ────────────────────────────────────────────────────────────────────
if [ -t 1 ]; then
    BOLD='\033[1m'; RESET='\033[0m'
    GREEN='\033[0;32m'; RED='\033[0;31m'; YELLOW='\033[0;33m'; CYAN='\033[0;36m'
else
    BOLD=''; RESET=''; GREEN=''; RED=''; YELLOW=''; CYAN=''
fi

info()    { echo -e "${CYAN}${BOLD}→${RESET} $*"; }
success() { echo -e "${GREEN}${BOLD}✓${RESET} $*"; }
warn()    { echo -e "${YELLOW}${BOLD}!${RESET} $*"; }
error()   { echo -e "${RED}${BOLD}✗${RESET} $*" >&2; }
die()     { error "$*"; exit 1; }

echo ""
echo -e "${BOLD}ChatIncluded Uninstaller${RESET}"
echo ""

# ── Read install record ───────────────────────────────────────────────────────
PLUGINS_DIR=""
if [ -f "$RECORD_FILE" ]; then
    # shellcheck source=/dev/null
    PLUGINS_DIR="$(grep '^plugins_dir=' "$RECORD_FILE" | cut -d= -f2-)"
    info "Install record found. Plugins folder: $PLUGINS_DIR"
else
    warn "No install record found at $RECORD_FILE"
    echo "Please enter the path to your Casterlabs plugins folder:"
    read -r -p "> " USER_PATH
    PLUGINS_DIR="${USER_PATH/#\~/$HOME}"
fi

if [ -z "$PLUGINS_DIR" ]; then
    die "No plugins folder specified. Aborting."
fi

# ── Confirm ───────────────────────────────────────────────────────────────────
echo ""
echo -e "This will remove ${BOLD}$PLUGIN_JAR${RESET} from:"
echo -e "  ${CYAN}$PLUGINS_DIR${RESET}"
echo ""
read -r -p "Continue? (y/N) " CONFIRM
case "$CONFIRM" in
    [yY]|[yY][eE][sS]) echo "" ;;
    *) echo "Cancelled."; exit 0 ;;
esac

# ── Remove the JAR ────────────────────────────────────────────────────────────
JAR_FILE="$PLUGINS_DIR/$PLUGIN_JAR"
if [ -f "$JAR_FILE" ]; then
    rm "$JAR_FILE" || die "Failed to remove $JAR_FILE. Check your write access."
    success "Removed plugin JAR."
else
    warn "$JAR_FILE not found — may have already been removed."
fi

# ── Remove install record ─────────────────────────────────────────────────────
if [ -f "$RECORD_FILE" ]; then
    rm "$RECORD_FILE"
    rmdir "$RECORD_DIR" 2>/dev/null || true
    success "Removed install record."
fi

echo ""
success "${BOLD}ChatIncluded has been uninstalled.${RESET}"
echo ""
