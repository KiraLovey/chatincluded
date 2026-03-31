#!/usr/bin/env bash
# Produces ChatIncluded-installer.sh — a self-contained single-file installer
# for macOS and Linux testers.
#
# Usage (run from the project root):
#   chmod +x build-installer.sh && ./build-installer.sh
#
# Requires: base64 (standard on macOS and Linux; available in Git Bash on Windows)

set -euo pipefail

JAR="chatincluded-1.0.0.jar"
TEMPLATE="install.sh"
OUTPUT="ChatIncluded-installer.sh"

# ── Sanity checks ─────────────────────────────────────────────────────────────
[ -f "$JAR" ]      || { echo "Error: $JAR not found. Run 'mvn package' first."; exit 1; }
[ -f "$TEMPLATE" ] || { echo "Error: $TEMPLATE not found."; exit 1; }

# ── Verify the template has the payload marker ────────────────────────────────
grep -q '^__PAYLOAD_BELOW__' "$TEMPLATE" || \
    { echo "Error: $TEMPLATE is missing the __PAYLOAD_BELOW__ marker."; exit 1; }

# ── Strip any existing payload from the template (idempotent rebuilds) ────────
MARKER_LINE=$(grep -n '^__PAYLOAD_BELOW__' "$TEMPLATE" | head -1 | cut -d: -f1)
head -n "$MARKER_LINE" "$TEMPLATE" > "$OUTPUT"

# ── Append base64-encoded JAR ─────────────────────────────────────────────────
base64 "$JAR" >> "$OUTPUT"

# ── Make it executable (best-effort — no-op on Windows-mounted filesystems) ───
chmod +x "$OUTPUT" 2>/dev/null || true

# ── Summary ───────────────────────────────────────────────────────────────────
JAR_SIZE=$(wc -c < "$JAR" | tr -d ' ')
OUT_SIZE=$(wc -c < "$OUTPUT" | tr -d ' ')
echo ""
echo "Built: $OUTPUT"
echo "  JAR size      : $(( JAR_SIZE / 1024 )) KB"
echo "  Installer size: $(( OUT_SIZE / 1024 )) KB"
echo ""
echo "Send ChatIncluded-installer.sh to testers. They run:"
echo "  chmod +x ChatIncluded-installer.sh && ./ChatIncluded-installer.sh"
echo ""
