# ChatIncluded 🌐
### Break the language barrier, live.

**ChatIncluded** is a plugin for [Casterlabs Caffeinated](https://casterlabs.co) that automatically translates multilingual chat messages in real time and posts the translation back to chat — no commands needed, no manual copy-pasting.

It works seamlessly across **Twitch**, **Kick**, and **YouTube** simultaneously, making it perfect for multi-streamers with international audiences. Trovo and TikTok support coming soon.

🌐 **[chatincluded.live](https://chatincluded.live)** — Full documentation, commands reference, and download guide

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

Before installing ChatIncluded, you need four things:

| Tool | What it is | Download |
|------|-----------|----------|
| [Casterlabs Caffeinated](https://casterlabs.co) | The streaming app ChatIncluded runs inside | casterlabs.co |
| [Java JDK 17+](https://adoptium.net) | The programming language ChatIncluded is built with | adoptium.net |
| [Apache Maven](https://maven.apache.org/download.cgi) | The build tool that compiles the plugin | maven.apache.org |
| [DeepL API account](https://www.deepl.com/pro-api) | The translation service ChatIncluded uses | deepl.com |

> A **free** DeepL API account gives you 500,000 characters/month — plenty for most streamers.

---

## Installation Guide

> **New to terminals?** A terminal is a text-based way to talk to your computer. On Windows, press **Windows key + R**, type `powershell`, and press Enter. This opens PowerShell — you type a command and press Enter to run it. That's all there is to it!

### Step 1 — Download Casterlabs Caffeinated

ChatIncluded runs as a plugin inside Casterlabs. If you don't have it yet:

1. Go to [casterlabs.co](https://casterlabs.co) and download Caffeinated
2. Install and sign in with your streaming accounts
3. Come back here once Casterlabs is running

### Step 2 — Get a DeepL API Key

1. Go to [deepl.com/pro-api](https://www.deepl.com/pro-api) and create a free account
2. After signing in, click your account name → **Account**
3. Scroll down to find your **Authentication Key** and copy it — you'll need it later
4. Note whether you signed up for **Free** or **Pro**:
   - Free accounts use `api-free.deepl.com`
   - Pro accounts use `api.deepl.com`

### Step 3 — Install Java

1. Go to [adoptium.net](https://adoptium.net) and click the **Latest LTS Release** download button
2. Run the installer using all the default options
3. Open PowerShell and confirm it worked:
   ```powershell
   java -version
   ```
   You should see something like `openjdk version "21.x.x"`

### Step 4 — Install Maven

1. Go to [maven.apache.org/download.cgi](https://maven.apache.org/download.cgi)
2. Download the **Binary zip archive** (the file ending in `-bin.zip`)
3. Extract it to `C:\Program Files\Maven`
4. Add Maven to your PATH:
   - Press **Windows key**, search **Environment Variables**, open it
   - Under **System Variables**, find **Path** → click **Edit**
   - Click **New** and add `C:\Program Files\Maven\bin`
   - Click OK on all windows
5. Close and reopen PowerShell, then confirm:
   ```powershell
   mvn -version
   ```

### Step 5 — Install Git

1. Go to [git-scm.com/download/win](https://git-scm.com/download/win) and download the installer
2. Run it using all the default options
3. Close and reopen PowerShell after installing

### Step 6 — Download ChatIncluded

Open PowerShell and run these commands one at a time:

```powershell
cd C:\
mkdir ChatIncluded
cd ChatIncluded
git clone https://github.com/KiraLovey/chatincluded.git .
```

This downloads all the ChatIncluded files to `C:\ChatIncluded`.

### Step 7 — Register the Casterlabs SDK

```powershell
mvn install:install-file "-Dfile=$env:APPDATA\casterlabs-caffeinated\app\Caffeinated.jar" "-DgroupId=co.casterlabs.caffeinated" "-DartifactId=plugin_sdk" "-Dversion=1.2" "-Dpackaging=jar"
```

You should see `BUILD SUCCESS`. You only need to do this once per computer.

### Step 8 — Build the Plugin

```powershell
cd C:\ChatIncluded
mvn clean package
```

When it finishes you should see `BUILD SUCCESS`.

### Step 9 — Install the Plugin

```powershell
Copy-Item "target\chatincluded-1.0.0.jar" "$env:APPDATA\casterlabs-caffeinated\plugins\" -Force
```

**Fully close and reopen Casterlabs** (make sure it's not just minimized to the system tray).

### Step 10 — Configure the Plugin

1. In Casterlabs, go to **Widgets & Alerts**
2. Click the **+** button → **Other** → **ChatIncluded Settings**
3. Go to the **DeepL API** tab and enter your API key and plan type
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

To update, open PowerShell and run:

```powershell
cd C:\ChatIncluded
git pull
mvn clean package
Copy-Item "target\chatincluded-1.0.0.jar" "$env:APPDATA\casterlabs-caffeinated\plugins\" -Force
```

Then restart Casterlabs. Your settings are preserved automatically.

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

## Troubleshoded

**Plugin shows in the list but won't load**
Make sure you ran `mvn clean package`, copied the JAR, and fully restarted Casterlabs.

**"Authorization header is missing API key"**
Make sure you created a ChatIncluded Settings widget and entered your API key. Check that API Plan matches your DeepL account type.

**Emotes are being translated**
Make sure you are on the latest version. Run `git pull` then rebuild.

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
- [ ] Trovo support
- [ ] TikTok support
- [ ] chatincluded.live full website with documentation
- [ ] Persistent viewer language preferences across streams

---

## Security

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
