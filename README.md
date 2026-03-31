# ChatIncluded 🌐
### Break the language barrier, live.

**ChatIncluded** is a plugin for [Casterlabs Caffeinated](https://casterlabs.co) that automatically translates multilingual chat messages in real time and posts the translation back to chat — no commands needed, no manual copy-pasting.

It works seamlessly across **Twitch**, **Kick**, and **YouTube** simultaneously, making it perfect for multi-streamers with international audiences. Trovo and TikTok support coming soon.

🌐 **[chatincluded.live](https://chatincluded.live)** — Full documentation, commands reference, and download

> ⚠️ **Beta** — Core functionality is stable and actively tested. Feedback welcome via the [Casterlabs Discord](https://casterlabs.co/discord)!

---

## Features

- 🔤 **Auto-translation** — Detects viewer language automatically and translates to your language in real time
- 💬 **Two-way conversation** — @mention a viewer and ChatIncluded translates your reply into their language
- 🤖 **Smart emote handling** — Uses Casterlabs' message fragments to skip emotes and emoji on all platforms
- 🛡️ **Bot exclusion list** — Add your bots by username to prevent them from triggering translations
- 🌍 **Multi-platform** — Twitch, Kick, and YouTube simultaneously with cross-platform deduplication
- 📢 **Chat commands** — Viewers can request translations, set language preferences, and more
- 🔁 **Loop prevention** — ChatIncluded never re-translates its own output
- 🔔 **Update notifications** — Notifies you in the Casterlabs console when a new version is available
- ⚙️ **Fully configurable** — Every feature has a settings toggle in the Caffeinated widget panel

---

## Requirements

| Tool | What it is | Download |
|------|-----------|----------|
| [Casterlabs Caffeinated](https://casterlabs.co) | The streaming app ChatIncluded runs inside | casterlabs.co |
| [DeepL API account](https://www.deepl.com/pro-api) | The translation service ChatIncluded uses | deepl.com |

> A **free** DeepL API account gives you 500,000 characters/month — plenty for most streamers.

---

## Installation

### Step 1 — Download Casterlabs Caffeinated

ChatIncluded runs as a plugin inside Casterlabs. If you don't have it yet:

1. Go to [casterlabs.co](https://casterlabs.co) and download Caffeinated
2. Install and sign in with your streaming accounts

### Step 2 — Get a DeepL API Key

1. Go to [deepl.com/pro-api](https://www.deepl.com/pro-api) and create a free account
2. After signing in, click your account name → **Account**
3. Scroll down to find your **Authentication Key** and copy it — you'll need it in Step 4

### Step 3 — Install ChatIncluded

#### Windows
1. [**Download ChatIncluded-Setup.exe**](https://github.com/KiraLovey/chatincluded/raw/main/ChatIncluded-Setup.exe)
2. Run the installer — it detects Casterlabs automatically and drops the plugin into the right place
3. Open (or restart) Casterlabs when the installer finishes

#### macOS
1. [**Download chatincluded-1.0.0.jar**](https://github.com/KiraLovey/chatincluded/raw/main/chatincluded-1.0.0.jar) and [**install.sh**](https://github.com/KiraLovey/chatincluded/raw/main/install.sh) into the same folder
2. Open Terminal and run:
   ```bash
   chmod +x install.sh && ./install.sh
   ```
3. Follow the prompts — the script detects Casterlabs and installs into the correct plugins folder
4. Open (or restart) Casterlabs when the script finishes

#### Linux (Ubuntu and other distros)
1. [**Download chatincluded-1.0.0.jar**](https://github.com/KiraLovey/chatincluded/raw/main/chatincluded-1.0.0.jar) and [**install.sh**](https://github.com/KiraLovey/chatincluded/raw/main/install.sh) into the same folder
2. Open a terminal and run:
   ```bash
   chmod +x install.sh && ./install.sh
   ```
3. Follow the prompts — the script installs into `~/.local/share/casterlabs-caffeinated/plugins` by default
4. Open (or restart) Casterlabs when the script finishes

> **Manual install (any platform):** If you prefer, just copy `chatincluded-1.0.0.jar` directly into your Casterlabs plugins folder. In Casterlabs: Settings → Plugins → Open Plugins Folder.

### Step 4 — Configure the Plugin

1. In Casterlabs, go to **Widgets & Alerts**
2. Click the **+** button → **Other** → **ChatIncluded Settings**
3. Go to the **DeepL API** tab and enter your API key and plan type (Free or Pro)
4. Go to the **Language** tab and set your **Target Language Code** (e.g. `EN` for English)
5. Settings save automatically — there is no Save button

**That's it!** Send a message in another language from a second account to test it.

---

## Updating ChatIncluded

When a new version is available, ChatIncluded will notify you in the Casterlabs console at startup:

```
[ChatIncluded] A new version is available!
[ChatIncluded] Current version : 1.0.0
[ChatIncluded] Latest version  : 1.1.0
[ChatIncluded] Update guide    : https://chatincluded.live
```

To update, download and run the latest installer for your platform:
- **Windows** — [ChatIncluded-Setup.exe](https://github.com/KiraLovey/chatincluded/raw/main/ChatIncluded-Setup.exe)
- **macOS / Linux** — download the new `chatincluded-1.0.0.jar` and re-run `install.sh`, or copy the JAR directly into your plugins folder

The installer overwrites the existing plugin file automatically. Your settings are preserved.

---

## Settings Reference

### General
| Setting | Default | Description |
|---------|---------|-------------|
| Enable ChatIncluded | On | Master on/off switch for the entire plugin |
| Show sender username in translations | On | Adds `@username:` to translation output — helpful for busy chats |

### DeepL API
| Setting | Description |
|---------|-------------|
| DeepL API Key | Your API key from deepl.com |
| API Plan | Free or Pro — must match your actual DeepL account type |

### Language
| Setting | Default | Description |
|---------|---------|-------------|
| Target Language Code | EN | The language to translate incoming messages into |

### Performance
| Setting | Default | Description |
|---------|---------|-------------|
| Translation Cooldown (ms) | 500 | Minimum time between translation requests |
| Burst Limit | 5 | Max translations allowed per cooldown window |
| Deduplication Window (seconds) | 10 | Prevents the same message being translated twice across platforms |
| Minimum Message Length | 5 | Skips very short messages that are often misdetected |

### Two-Way Conversation
| Setting | Default | Description |
|---------|---------|-------------|
| Enable Two-Way Translation | On | Translates streamer @mentions back into the viewer's language |
| Remember viewer language for (minutes) | 30 | How long to remember a viewer's language |

### Commands
| Setting | Description |
|---------|-------------|
| !chatincluded response message | The message posted when viewers type `!chatincluded`. Fully customizable |
| !speak access level | Who can use `!speak`: Streamer / Mod / Subscriber / Everyone |

### Bot Exclusions
| Setting | Description |
|---------|-------------|
| Excluded usernames | Comma-separated usernames to ignore (e.g. `fossabot, streamelements, nightbot`). Case-insensitive |

### Platforms
| Setting | Default | Description |
|---------|---------|-------------|
| Enable on Twitch | On | Toggle translations for Twitch |
| Enable on Kick | On | Toggle translations for Kick |
| Enable on YouTube | On | Toggle translations for YouTube |

---

## Chat Commands

| Command | Who can use it | What it does |
|---------|---------------|--------------|
| `!chatincluded` | Everyone | Shows plugin info and a link to chatincluded.live |
| `!languages` | Everyone | Posts the most common language codes and a link to the full list |
| `!setlang ES` | Everyone | Pins your preferred language for the session. Sends confirmation in both English and your language. Can be changed any time. |
| `!translate ES` | Everyone | Translates the most recent chat message into Spanish |
| `!translate ES Hello!` | Everyone | Translates the provided text into Spanish |
| `!translate ES` *(as a reply)* | Everyone | Translates the specific message you replied to |
| `!speak Welcome!` | Configurable | Translates the streamer's message into every language active in the session |

> **Tip for viewers:** You don't need to use `!setlang` — ChatIncluded automatically detects your language the first time it translates one of your messages. Use `!setlang` only if short messages are being translated into the wrong language.

---

## How Two-Way Conversation Works

ChatIncluded remembers the language of every viewer it has translated. When you @mention a viewer in chat, it automatically translates your reply into their language:

```
Viewer:       hola como estas
ChatIncluded: [ES->EN] @viewer: Hello how are you
You:          @viewer Thanks for watching!
ChatIncluded: @viewer [EN->ES] ¡Gracias por ver!
```

No setup needed — it works automatically as soon as a viewer's language has been detected.

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

> **Common mistakes:** Use `ID` not `IN` for Indonesian, `ZH` not `CN` for Chinese, `JA` not `JP` for Japanese. ChatIncluded will suggest the correct code automatically.

---

## Troubleshooting

**Plugin shows in the list but won't load**
Fully close Casterlabs (including from the system tray) and reopen it. If the issue persists, re-run the installer.

**"Authorization header is missing API key"**
Make sure you created a ChatIncluded Settings widget and entered your API key. Check that API Plan matches your DeepL account type.

**Emotes are being translated**
Make sure you are on the latest version. Re-run the installer to update.

**Single words are being mistranslated**
Go to ChatIncluded Settings → Performance and increase the Minimum Message Length.

**Translations work but replies don't appear in chat**
The Casterlabs account you are signed in as may not have chat permission on that platform. YouTube requires minimum account age before posting in chat.

**Two-way @mention reply isn't working**
The viewer needs to have sent at least one message that ChatIncluded translated in the current session, or have used `!setlang`.

**`!setlang IN` says it's not supported**
Use `ID` for Indonesian. ChatIncluded will suggest the correct code automatically.

**Occasional "GOAWAY received" error**
A normal HTTP/2 network hiccup affecting only that one message. The plugin continues normally.

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
- [x] Update notifications on startup
- [x] Windows installer (ChatIncluded-Setup.exe)
- [x] macOS / Linux installer (install.sh)
- [x] chatincluded.live — website with documentation
- [ ] Code signing (in progress)
- [ ] Trovo support
- [ ] TikTok support
- [ ] Persistent viewer language preferences across streams

---

## Security

- The installer (`ChatIncluded-Setup.exe`) is signed — see [chatincluded.live/policy.html](https://chatincluded.live/policy.html) for the code signing policy
- Your DeepL API key is stored inside Casterlabs — ChatIncluded never writes it to any file
- Avoid sharing Casterlabs log files (`%appdata%\casterlabs-caffeinated\logs\`) during debug sessions
- If your API key is ever exposed, regenerate it at [deepl.com/account](https://www.deepl.com/account)

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
