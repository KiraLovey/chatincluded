#!/usr/bin/env bash
# ChatIncluded Installer for macOS and Linux
# Self-contained — the plugin JAR is embedded in this script.
# Usage: chmod +x ChatIncluded-installer.sh && ./ChatIncluded-installer.sh

set -euo pipefail

PLUGIN_JAR="chatincluded-1.0.0.jar"
PLUGIN_NAME="ChatIncluded"
VERSION="1.0.0"

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

# ── Banner ─────────────────────────────────────────────────────────────────────
echo ""
echo -e "${BOLD}ChatIncluded Installer — v${VERSION}${RESET}"
echo -e "${CYAN}Break the language barrier, live.${RESET}"
echo ""

# ── Detect OS ─────────────────────────────────────────────────────────────────
OS="$(uname -s)"
case "$OS" in
    Darwin) PLATFORM="macOS" ;;
    Linux)  PLATFORM="Linux" ;;
    *)      die "Unsupported platform: $OS. This installer supports macOS and Linux." ;;
esac
info "Detected platform: $PLATFORM"

# ── Extract embedded JAR to a temp file ───────────────────────────────────────
JAR_TMP="$(mktemp /tmp/chatincluded-XXXXXX.jar)"
trap 'rm -f "$JAR_TMP"' EXIT

PAYLOAD_LINE=$(awk '/^__PAYLOAD_BELOW__/{print NR + 1; exit}' "$0")
if [ -z "$PAYLOAD_LINE" ]; then
    die "Installer is corrupt — embedded JAR payload not found.\nPlease re-download ChatIncluded-installer.sh."
fi

tail -n +"$PAYLOAD_LINE" "$0" | base64 -d > "$JAR_TMP" 2>/dev/null || \
    die "Failed to extract the embedded JAR. The installer file may be corrupt.\nPlease re-download ChatIncluded-installer.sh."

info "Extracted plugin JAR."

# ── Detect Casterlabs ─────────────────────────────────────────────────────────
CASTERLABS_FOUND=0

if [ "$PLATFORM" = "macOS" ]; then
    for APP_PATH in \
        "/Applications/Casterlabs Caffeinated.app" \
        "$HOME/Applications/Casterlabs Caffeinated.app" \
        "/Applications/Casterlabs.app" \
        "$HOME/Applications/Casterlabs.app"
    do
        if [ -d "$APP_PATH" ]; then
            CASTERLABS_FOUND=1
            info "Found Casterlabs at: $APP_PATH"
            break
        fi
    done
else
    for EXEC_PATH in \
        "/usr/bin/casterlabs-caffeinated" \
        "/usr/local/bin/casterlabs-caffeinated" \
        "$HOME/.local/bin/casterlabs-caffeinated" \
        "/opt/Casterlabs Caffeinated/casterlabs-caffeinated" \
        "/opt/casterlabs-caffeinated/casterlabs-caffeinated"
    do
        if [ -f "$EXEC_PATH" ]; then
            CASTERLABS_FOUND=1
            info "Found Casterlabs at: $EXEC_PATH"
            break
        fi
    done
    if [ "$CASTERLABS_FOUND" = "0" ]; then
        for APPIMAGE_PATH in \
            "$HOME/Applications/Casterlabs"*.AppImage \
            "$HOME/Downloads/Casterlabs"*.AppImage \
            "/opt/Casterlabs"*.AppImage
        do
            if [ -f "$APPIMAGE_PATH" ]; then
                CASTERLABS_FOUND=1
                info "Found Casterlabs AppImage: $APPIMAGE_PATH"
                break
            fi
        done
    fi
fi

if [ "$CASTERLABS_FOUND" = "0" ]; then
    warn "Casterlabs Caffeinated was not detected on this system."
    warn "ChatIncluded requires Casterlabs to run. Install it at: https://casterlabs.co"
    echo ""
    read -r -p "Continue anyway? (y/N) " CONTINUE
    case "$CONTINUE" in
        [yY]|[yY][eE][sS]) echo "" ;;
        *) echo "Cancelled."; exit 0 ;;
    esac
fi

# ── Resolve default plugins folder ────────────────────────────────────────────
# Paths sourced from Casterlabs/caffeinated AppConfig.java (AppDirs.getUserDataDir)
if [ "$PLATFORM" = "macOS" ]; then
    DEFAULT_PLUGINS_DIR="$HOME/Library/Application Support/casterlabs-caffeinated/plugins"
else
    XDG_DATA="${XDG_DATA_HOME:-$HOME/.local/share}"
    DEFAULT_PLUGINS_DIR="$XDG_DATA/casterlabs-caffeinated/plugins"
fi

# ── Let the user confirm or override the plugins folder ───────────────────────
echo -e "${BOLD}Plugins folder:${RESET}"
echo "  Default: $DEFAULT_PLUGINS_DIR"
echo ""
echo "Open Casterlabs → Settings → Plugins → 'Open Plugins Folder' to confirm the path."
echo "Press Enter to use the default, or type a different path:"
read -r -p "> " USER_PATH

if [ -n "$USER_PATH" ]; then
    PLUGINS_DIR="${USER_PATH/#\~/$HOME}"
else
    PLUGINS_DIR="$DEFAULT_PLUGINS_DIR"
fi

echo ""
info "Install target: $PLUGINS_DIR"

# ── Validate / create the plugins folder ─────────────────────────────────────
if [ ! -d "$PLUGINS_DIR" ]; then
    warn "Folder does not exist: $PLUGINS_DIR"
    read -r -p "Create it? (y/N) " CREATE_DIR
    case "$CREATE_DIR" in
        [yY]|[yY][eE][sS])
            mkdir -p "$PLUGINS_DIR" || die "Failed to create folder: $PLUGINS_DIR"
            success "Created plugins folder."
            ;;
        *)
            die "Plugins folder not found and not created. Aborting."
            ;;
    esac
fi

# ── Copy the JAR ──────────────────────────────────────────────────────────────
info "Installing $PLUGIN_JAR..."
cp "$JAR_TMP" "$PLUGINS_DIR/$PLUGIN_JAR" || \
    die "Failed to copy the plugin to $PLUGINS_DIR.\nCheck that you have write access to that folder."

# ── Write uninstall record ────────────────────────────────────────────────────
RECORD_DIR="$HOME/.local/share/chatincluded"
mkdir -p "$RECORD_DIR"
cat > "$RECORD_DIR/install.conf" <<EOF
plugins_dir=$PLUGINS_DIR
jar_name=$PLUGIN_JAR
version=$VERSION
installed_at=$(date -u +"%Y-%m-%dT%H:%M:%SZ")
EOF

# ── Done ──────────────────────────────────────────────────────────────────────
echo ""
success "${BOLD}ChatIncluded ${VERSION} installed!${RESET}"
echo ""
echo -e "  In Casterlabs: ${BOLD}Widgets & Alerts → + → Other → ChatIncluded Settings${RESET}"
echo -e "  Enter your DeepL API key to start translating."
echo ""
echo -e "  To uninstall, run: ${CYAN}./uninstall.sh${RESET}  (or re-run this installer)"
echo ""

exit 0
# ── Embedded payload (base64-encoded JAR) — do not edit below this line ───────
__PAYLOAD_BELOW__
