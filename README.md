[README.md](https://github.com/user-attachments/files/26086318/README.md)
# ChatIncluded 🌐

**ChatIncluded** is a plugin for [Casterlabs Caffeinated](https://casterlabs.co) that automatically translates chat messages from your viewers into your chosen language — and posts the translation back to chat in real time.

No commands needed. No manual copy-pasting. It just works in the background while you stream across **Twitch**, **Kick**, and **YouTube** simultaneously. Trovo and TikTok support coming soon.

> ⚠️ **This project is currently in Beta.** Core functionality is stable but features are actively being developed. Feedback welcome!

---

## What It Does

When a viewer sends a message in another language, ChatIncluded will:

1. Detect the language automatically
2. Translate it using the [DeepL API](https://www.deepl.com) (one of the most accurate translation services available)
3. Post the translated message back to chat in the format: `[ES->EN] Hello, how are you?`

When the streamer @mentions a viewer who has been translated, ChatIncluded automatically translates the streamer's message back into the viewer's language — enabling genuine two-way conversation across language barriers.

---

## Requirements

| Tool | Purpose | Download |
|------|---------|----------|
| [Casterlabs Caffeinated](https://casterlabs.co) | The streaming app this plugin runs inside | casterlabs.co |
| [Java JDK 17+](https://adoptium.net) | Required to build the plugin | adoptium.net |
| [Apache Maven](https://maven.apache.org/download.cgi) | The build tool | maven.apache.org |
| A [DeepL API account](https://www.deepl.com/pro-api) | Powers the translations | deepl.com |

> **Note:** A free DeepL API account gives you 500,000 characters/month — more than enough for most streamers.

---

## Installation

### Step 1 — Get a DeepL API Key

1. Go to [deepl.com/pro-api](https://www.deepl.com/pro-api) and sign up for a free account
2. After signing in, go to your **Account** page
3. Copy your **Authentication Key**
4. Note whether you're on the **Free** or **Pro** plan — this affects which server endpoint is used

### Step 2 — Install the Casterlabs SDK

Casterlabs ships its plugin SDK bundled inside the app itself. Register it with Maven once using this command in PowerShell:

```powershell
mvn install:install-file "-Dfile=$env:APPDATA\casterlabs-caffeinated\app\Caffeinated.jar" "-DgroupId=co.casterlabs.caffeinated" "-DartifactId=plugin_sdk" "-Dversion=1.2" "-Dpackaging=jar"
```

You only need to do this once.

### Step 3 — Build the Plugin

```powershell
cd C:\Your\Project\ChatIncluded
mvn clean package
```

You should see `BUILD SUCCESS` and a JAR at `target\chatincluded-1.0.0.jar`.

### Step 4 — Install the Plugin

```powershell
Copy-Item "target\chatincluded-1.0.0.jar" "$env:APPDATA\casterlabs-caffeinated\plugins\" -Force
```

Restart Casterlabs completely.

### Step 5 — Configure the Plugin

1. In Casterlabs, go to **Widgets & Alerts**
2. Click the **+** button and select **Other → ChatIncluded Settings**
3. Fill in your DeepL API key and configure your preferences

---

## Settings Reference

### General
| Setting | Description |
|---------|-------------|
| Enable ChatIncluded | Master on/off switch |

### DeepL API
| Setting | Description |
|---------|-------------|
| DeepL API Key | Your API key from deepl.com |
| API Plan | Free or Pro — must match your DeepL account type |

### Language
| Setting | Description |
|---------|-------------|
| Target Language Code | The language to translate incoming messages into (e.g. `EN`, `ES`, `FR`) |

### Performance
| Setting | Default | Description |
|---------|---------|-------------|
| Translation Cooldown (ms) | 500 | Minimum time between translation requests |
| Burst Limit | 5 | Max translations per cooldown window |
| Deduplication Window (seconds) | 10 | Prevents the same message being translated twice across platforms |

### Two-Way Conversation
| Setting | Default | Description |
|---------|---------|-------------|
| Enable Two-Way Translation | On | Translates streamer @mentions back into the viewer's language |
| Remember viewer language for (minutes) | 30 | How long to remember a viewer's detected language |

### Commands
| Setting | Description |
|---------|-------------|
| !chatincluded message | The message posted when viewers type !chatincluded in chat |
| !speak access level | Who can use the !speak command (Streamer / Mods / Subscribers / Everyone) |

### Platforms
| Setting | Description |
|---------|-------------|
| Enable on Twitch | Toggle translations for Twitch chat |
| Enable on Kick | Toggle translations for Kick chat |
| Enable on YouTube | Toggle translations for YouTube chat |

---

## Chat Commands

| Command | Access | Description |
|---------|--------|-------------|
| `!chatincluded` | Everyone | Shows plugin info and a link to the full commands list |
| `!translate <code> <message>` | Everyone | Translates text into the specified language. Example: `!translate es Hello!` |
| `!setlang <code>` | Everyone | Pins your preferred language for this stream session. Example: `!setlang ES` |
| `!languages` | Everyone | Posts the most common language codes and a link to the full list |
| `!speak <message>` | Configurable | Streamer/mod types a message and ChatIncluded translates it into every language active in the session |

---

## Language Codes

| Code | Language |
|------|----------|
| `EN` | English |
| `ES` | Spanish |
| `FR` | French |
| `DE` | German |
| `PT` | Portuguese |
| `IT` | Italian |
| `JA` | Japanese |
| `KO` | Korean |
| `ZH` | Chinese (simplified) |
| `RU` | Russian |
| `AR` | Arabic |
| `NL` | Dutch |
| `PL` | Polish |
| `SV` | Swedish |
| `TR` | Turkish |

Full list: [DeepL Supported Languages](https://developers.deepl.com/docs/resources/supported-languages)

---

## Rebuilding After Updates

```powershell
mvn clean package
Copy-Item "target\chatincluded-1.0.0.jar" "$env:APPDATA\casterlabs-caffeinated\plugins\" -Force
```

Then restart Casterlabs.

---

## Troubleshooting

**Plugin shows in the list but won't load**
Rebuild with `mvn clean package`, copy the new JAR, and fully restart Casterlabs.

**"Authorization header is missing API key"**
Make sure you created a ChatIncluded Settings widget instance and entered your API key. Check that your API Plan setting matches your actual DeepL account type.

**Translations work but replies don't appear in chat**
The account Casterlabs is signed into may not have chat permissions on that platform. YouTube requires a minimum account age/activity before posting in chat.

**Occasional "GOAWAY received" error**
A normal HTTP/2 network hiccup. Only affects that one message — the plugin continues working normally.

**Messages in my language are being translated**
ChatIncluded skips translation when the detected source language matches your target language. Short messages like "lol" or "gg" can sometimes be misdetected — viewers can use `!setlang` to pin their language and avoid this.

---

## Roadmap

- [x] Twitch, Kick, YouTube support
- [x] Two-way conversation via @mentions
- [x] Chat commands (!translate, !setlang, !speak, !languages)
- [ ] Trovo support
- [ ] TikTok support
- [ ] chatincluded.gg website with full documentation
- [ ] Persistent viewer language preferences across streams

---

## Security

- Your DeepL API key is stored in Casterlabs' internal settings — it is not written to any external file by ChatIncluded
- Avoid sharing log files from `%appdata%\casterlabs-caffeinated\logs\` as they may contain sensitive information during debug sessions
- If your API key is ever exposed, regenerate it immediately at [deepl.com/account](https://www.deepl.com/account)

---

## Built With

- [Casterlabs Caffeinated Plugin SDK](https://casterlabs.co) v1.2
- [DeepL API](https://www.deepl.com/pro-api)
- [Gson](https://github.com/google/gson) v2.11.0

---

## Contributing

This project is in active beta development. If you find a bug or have a feature request, please open an issue. Pull requests are welcome.

---

*Made with ❤️ for multilingual streaming communities.*
