# ChatIncluded 🌐

**ChatIncluded** is a plugin for [Casterlabs Caffeinated](https://casterlabs.co) that automatically translates multilingual chat messages in real time and posts the translation back to chat — no commands needed, no manual copy-pasting.

It works seamlessly across **Twitch**, **Kick**, and **YouTube** simultaneously, making it perfect for multi-streamers with international audiences. Trovo and TikTok support coming soon.

🌐 **[chatincluded.live](https://chatincluded.live)** — Commands, language codes, and full documentation

> ⚠️ **Beta** — Core functionality is stable and actively tested. Feedback welcome!

---

## Features

- 🔤 **Auto-translation** — Detects viewer language automatically and translates to your language in real time
- 💬 **Two-way conversation** — @mention a viewer and ChatIncluded translates your reply into their language
- 🤖 **Smart emote handling** — Skips emotes and emoji on all platforms so they never get mistranslated
- 🛡️ **Bot exclusion list** — Prevents bot messages from triggering translations
- 🌍 **Multi-platform** — Twitch, Kick, and YouTube simultaneously
- 📢 **Chat commands** — Viewers can request translations, set language preferences, and more
- 🔁 **Loop prevention** — ChatIncluded never re-translates its own output
- ⚙️ **Fully configurable** — Every feature has a settings toggle

---

## Requirements

| Tool | Purpose | Download |
|------|---------|----------|
| [Casterlabs Caffeinated](https://casterlabs.co) | The streaming app this plugin runs inside | casterlabs.co |
| [Java JDK 17+](https://adoptium.net) | Required to build the plugin | adoptium.net |
| [Apache Maven](https://maven.apache.org/download.cgi) | Build tool | maven.apache.org |
| [DeepL API account](https://www.deepl.com/pro-api) | Powers the translations | deepl.com |

> A **free** DeepL API account gives you 500,000 characters/month — plenty for most streamers.

---

## Installation

### Step 1 — Get a DeepL API Key

1. Sign up at [deepl.com/pro-api](https://www.deepl.com/pro-api)
2. Go to your **Account** page and copy your **Authentication Key**
3. Note whether you're on **Free** or **Pro** — this determines which server endpoint is used
   - Free keys use `api-free.deepl.com`
   - Pro keys use `api.deepl.com`

### Step 2 — Install the Casterlabs SDK

Casterlabs ships its plugin SDK bundled inside the app. Register it with Maven once:

```powershell
mvn install:install-file "-Dfile=$env:APPDATA\casterlabs-caffeinated\app\Caffeinated.jar" "-DgroupId=co.casterlabs.caffeinated" "-DartifactId=plugin_sdk" "-Dversion=1.2" "-Dpackaging=jar"
```

You only need to run this once per machine.

### Step 3 — Build the Plugin

```powershell
cd C:\Your\Project\ChatIncluded
mvn clean package
```

You should see `BUILD SUCCESS`. The JAR will be at `target\chatincluded-1.0.0.jar`.

### Step 4 — Install the Plugin

```powershell
Copy-Item "target\chatincluded-1.0.0.jar" "$env:APPDATA\casterlabs-caffeinated\plugins\" -Force
```

Restart Casterlabs completely.

### Step 5 — Configure the Plugin

1. In Casterlabs, go to **Widgets & Alerts**
2. Click the **+** button → **Other** → **ChatIncluded Settings**
3. Go to the **DeepL API** tab and enter your API key and plan
4. Set your **Target Language Code** in the Language tab (e.g. `EN` for English)
5. Settings save automatically — no save button needed

---

## Settings Reference

### General
| Setting | Default | Description |
|---------|---------|-------------|
| Enable ChatIncluded | On | Master on/off switch for the entire plugin |
| Show sender username in translations | On | Adds `@username:` to translation output for busy chats |

### DeepL API
| Setting | Description |
|---------|-------------|
| DeepL API Key | Your API key from deepl.com |
| API Plan | Free or Pro — must match your actual DeepL account type |

### Language
| Setting | Default | Description |
|---------|---------|-------------|
| Target Language Code | EN | The language to translate incoming messages into (e.g. `EN`, `ES`, `FR`) |

### Performance
| Setting | Default | Description |
|---------|---------|-------------|
| Translation Cooldown (ms) | 500 | Minimum time between translation requests |
| Burst Limit | 5 | Max translations allowed per cooldown window |
| Deduplication Window (seconds) | 10 | Prevents the same message being translated twice across platforms |
| Minimum Message Length | 5 | Skips messages shorter than this to avoid single-word misdetections |

### Two-Way Conversation
| Setting | Default | Description |
|---------|---------|-------------|
| Enable Two-Way Translation | On | Translates streamer @mentions back into the viewer's language |
| Remember viewer language for (minutes) | 30 | How long to remember a viewer's language after their last message |

### Commands
| Setting | Description |
|---------|-------------|
| !chatincluded response message | The message posted when viewers type `!chatincluded`. Fully customizable per streamer |
| !speak access level | Who can use `!speak`: Streamer / Mod / Subscriber / Everyone |

### Bot Exclusions
| Setting | Description |
|---------|-------------|
| Excluded usernames | Comma-separated list of usernames to ignore (e.g. `fossabot, streamelements, nightbot`). Case-insensitive |

### Platforms
| Setting | Default | Description |
|---------|---------|-------------|
| Enable on Twitch | On | Toggle translations for Twitch chat |
| Enable on Kick | On | Toggle translations for Kick chat |
| Enable on YouTube | On | Toggle translations for YouTube chat |

---

## Chat Commands

| Command | Access | Description |
|---------|--------|-------------|
| `!chatincluded` | Everyone | Posts your customizable plugin info message with a link to chatincluded.live |
| `!languages` | Everyone | Posts the most common language codes and a link to the full DeepL list |
| `!setlang <code>` | Everyone | Pins your preferred language for the session. Confirmation sent in both English and your language. Example: `!setlang ES` |
| `!translate <code>` | Everyone | Translates the most recent chat message into the specified language |
| `!translate <code> <message>` | Everyone | Translates the provided text into the specified language |
| `!translate <code>` *(as a reply)* | Everyone | Translates the specific message you replied to |
| `!speak <message>` | Configurable | Translates the streamer's message into every language active in the session |

> **!setlang tip:** Viewers only need to use this if short messages are being misdetected. ChatIncluded automatically registers their language the first time it translates one of their messages.

---

## How Two-Way Conversation Works

ChatIncluded remembers the language of every viewer it has translated. When you @mention a viewer in your reply, it automatically translates your message into their language:

```
chatincluded:  hola como estas
kira_lovey:    [ES->EN] @chatincluded: Hello how are you
kira_lovey:    @chatincluded Thanks for watching!
kira_lovey:    @chatincluded [EN->ES] ¡Gracias por ver!
```

Viewers can also use `!setlang` to manually pin their language, which is useful when short messages get misdetected.

---

## Language Codes

| Code | Language | | Code | Language |
|------|----------|-|------|----------|
| `EN` | English | | `KO` | Korean |
| `ES` | Spanish | | `ZH` | Chinese (simplified) |
| `FR` | French | | `RU` | Russian |
| `DE` | German | | `AR` | Arabic |
| `PT` | Portuguese | | `NL` | Dutch |
| `IT` | Italian | | `PL` | Polish |
| `JA` | Japanese | | `SV` | Swedish |
| `TR` | Turkish | | `ID` | Indonesian |

Full list: [DeepL Supported Languages](https://developers.deepl.com/docs/resources/supported-languages)

> **Common mistakes:** Use `ID` not `IN` for Indonesian, `ZH` not `CN` for Chinese, `JA` not `JP` for Japanese. ChatIncluded will suggest the correct code if you use a common wrong one.

---

## Rebuilding After Updates

```powershell
mvn clean package
Copy-Item "target\chatincluded-1.0.0.jar" "$env:APPDATA\casterlabs-caffeinated\plugins\" -Force
```

Restart Casterlabs. Your settings are preserved automatically between restarts.

---

## Troubleshooting

**Plugin shows in the list but won't load**
Make sure you ran `mvn clean package` after any changes and fully restarted Casterlabs (not just reloaded).

**"Authorization header is missing API key"**
Make sure you created a ChatIncluded Settings widget instance and entered your API key. Check that your API Plan matches your actual DeepL account type (Free vs Pro).

**Emotes are being translated**
Update to the latest version. ChatIncluded uses Casterlabs' fragment API to skip emotes on all platforms.

**Single words being mistranslated**
Increase the Minimum Message Length in the Performance settings tab. The default of 5 characters filters most single words.

**Translations work but replies don't appear in chat**
The account Casterlabs is signed in as may not have chat permission on that platform. YouTube requires a minimum account age before posting in chat.

**Two-way @mention reply not triggering**
ChatIncluded needs to have previously translated at least one message from that viewer in the current session. Have the viewer send a message in their language first, or have them use `!setlang` to register manually.

**`!setlang IN` returns an error**
`IN` is not a valid DeepL code. Use `ID` for Indonesian. ChatIncluded will suggest the correct code automatically.

**Occasional "GOAWAY received" error**
A normal HTTP/2 network hiccup affecting only that one message. The plugin continues working normally.

---

## Roadmap

- [x] Twitch, Kick, YouTube support
- [x] Smart emote handling on all platforms
- [x] Two-way conversation via @mentions
- [x] Full command system (!chatincluded, !translate, !setlang, !languages, !speak)
- [x] Bot exclusion list
- [x] Language code validation with suggestions
- [x] Bilingual !setlang confirmation
- [x] Attribution toggle
- [x] Minimum message length filter
- [ ] Trovo support
- [ ] TikTok support
- [ ] chatincluded.live full website with documentation
- [ ] Persistent viewer language preferences across streams

---

## Security

- Your DeepL API key is stored in Casterlabs' internal settings — ChatIncluded never writes it to any external file
- Avoid sharing log files from `%appdata%\casterlabs-caffeinated\logs\` as they may contain sensitive information during debug sessions
- If your API key is ever exposed, regenerate it immediately at [deepl.com/account](https://www.deepl.com/account)

---

## Built With

- [Casterlabs Caffeinated Plugin SDK](https://casterlabs.co) v1.2
- [DeepL API](https://www.deepl.com/pro-api)
- [Gson](https://github.com/google/gson) v2.11.0

---

## Contributing

ChatIncluded is in active beta. If you find a bug or have a feature request, please [open an issue](https://github.com/KiraLovey/chatincluded/issues). Pull requests are welcome.

---

*Made with ❤️ for multilingual streaming communities.*
