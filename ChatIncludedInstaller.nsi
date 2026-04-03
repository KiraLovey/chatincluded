; ChatIncluded Installer
; Built with NSIS

;--------------------------------
; General

Name "ChatIncluded"
OutFile "ChatIncluded-Setup.exe"
Icon "logo.ico"
InstallDir "$APPDATA\casterlabs-caffeinated\plugins"
RequestExecutionLevel user
ShowInstDetails nevershow
BrandingText " "

;--------------------------------
; Interface settings

!include "MUI2.nsh"
!include "nsDialogs.nsh"
!include "LogicLib.nsh"
!include "WinMessages.nsh"

!define MUI_ICON "logo.ico"
!define MUI_UNICON "logo.ico"

; Dark theme for MUI2 built-in pages (instfiles)
!define MUI_BGCOLOR "0A0A0F"
!define MUI_TEXTCOLOR "F0F0F0"

;--------------------------------
; Version info

VIProductVersion "1.1.0.0"
VIAddVersionKey "ProductName"     "ChatIncluded"
VIAddVersionKey "ProductVersion"  "1.1.0"
VIAddVersionKey "FileDescription" "ChatIncluded Installer"
VIAddVersionKey "FileVersion"     "1.1.0"
VIAddVersionKey "LegalCopyright"  "KiraLovey"

;--------------------------------
; Pages

Page custom WelcomePage
Page custom CheckCasterlabsPage
Page custom PluginsFolderPage PluginsFolderPageLeave
Page instfiles "" "" InstFilesLeave
Page custom SuccessPage

; Uninstaller pages
UninstPage instfiles

;--------------------------------
; Variables

Var CasterlabsFound
Var CasterlabsExe
Var InstallSucceeded
Var PluginsPath
Var PluginsDialog
Var PluginsPathField
Var AbortCalled

;--------------------------------
; GUI init — hide branding strip and colour outer window on startup

Function .onGUIInit
    GetDlgItem $0 $HWNDPARENT 1028
    ShowWindow $0 ${SW_HIDE}
    SetCtlColors $HWNDPARENT "F0F0F0" "0A0A0F"
FunctionEnd

;--------------------------------
; X button handler.
; Calling Quit here is what the user expects; NSIS 3 handles this without
; infinite-looping as long as install is already complete (post-instfiles).

Function .onUserAbort
    ${If} $AbortCalled == "1"
        Return
    ${EndIf}
    StrCpy $AbortCalled "1"
    Quit
FunctionEnd

;--------------------------------
; Welcome page

Var WelcomeDialog
Var WelcomeTitle
Var WelcomeTagline

Function WelcomePage
    nsDialogs::Create 1018
    Pop $WelcomeDialog
    ${If} $WelcomeDialog == error
        Abort
    ${EndIf}

    SetCtlColors $HWNDPARENT "F0F0F0" "0A0A0F"
    SetCtlColors $WelcomeDialog "F0F0F0" "0A0A0F"

    ; Hide Back — not needed on first page
    GetDlgItem $0 $HWNDPARENT 3
    ShowWindow $0 ${SW_HIDE}

    ${NSD_CreateLabel} 0 10u 100% 18u "ChatIncluded Installer"
    Pop $WelcomeTitle
    SetCtlColors $WelcomeTitle "FFFFFF" "0A0A0F"
    CreateFont $0 "Segoe UI" 18 700
    SendMessage $WelcomeTitle ${WM_SETFONT} $0 0

    ${NSD_CreateLabel} 0 33u 100% 12u "Break the language barrier, live."
    Pop $WelcomeTagline
    SetCtlColors $WelcomeTagline "AAAAAA" "0A0A0F"
    CreateFont $1 "Segoe UI" 10 400
    SendMessage $WelcomeTagline ${WM_SETFONT} $1 0

    ${NSD_CreateLabel} 0 48u 100% 10u "Version 1.1.0 Beta"
    Pop $0
    SetCtlColors $0 "666666" "0A0A0F"
    CreateFont $1 "Segoe UI" 9 400
    SendMessage $0 ${WM_SETFONT} $1 0

    ${NSD_CreateLabel} 10u 64u 280u 1u ""
    Pop $0
    SetCtlColors $0 "333333" "333333"

    ${NSD_CreateLabel} 10u 71u 280u 45u "This installer will add ChatIncluded to your Casterlabs Caffeinated plugins folder. Make sure Casterlabs is installed before continuing."
    Pop $0
    SetCtlColors $0 "CCCCCC" "0A0A0F"
    CreateFont $1 "Segoe UI" 9 400
    SendMessage $0 ${WM_SETFONT} $1 0

    nsDialogs::Show
FunctionEnd

;--------------------------------
; Check Casterlabs page
; Scans all known install locations. If found, skips this page and proceeds.
; If not found, shows a message box, opens casterlabs.co, then quits.

Function CheckCasterlabsPage
    StrCpy $CasterlabsFound "0"
    StrCpy $CasterlabsExe ""

    ; --- Known correct paths (updater is the real launcher) ---
    ${If} ${FileExists} "$PROGRAMFILES\Casterlabs Caffeinated\Casterlabs-Caffeinated-Updater.exe"
        StrCpy $CasterlabsFound "1"
        StrCpy $CasterlabsExe "$PROGRAMFILES\Casterlabs Caffeinated\Casterlabs-Caffeinated-Updater.exe"
    ${EndIf}
    ${If} $CasterlabsFound == "0"
    ${AndIf} ${FileExists} "$PROGRAMFILES64\Casterlabs Caffeinated\Casterlabs-Caffeinated-Updater.exe"
        StrCpy $CasterlabsFound "1"
        StrCpy $CasterlabsExe "$PROGRAMFILES64\Casterlabs Caffeinated\Casterlabs-Caffeinated-Updater.exe"
    ${EndIf}

    ; --- Scan LOCALAPPDATA\casterlabs* (root and app subdir) ---
    ${If} $CasterlabsFound == "0"
        FindFirst $1 $2 "$LOCALAPPDATA\casterlabs*"
        detect_la_loop:
            ${If} $2 == ""
                Goto detect_la_done
            ${EndIf}
            ${If} ${FileExists} "$LOCALAPPDATA\$2\app\Casterlabs-Caffeinated.exe"
                StrCpy $CasterlabsFound "1"
                StrCpy $CasterlabsExe "$LOCALAPPDATA\$2\app\Casterlabs-Caffeinated.exe"
                Goto detect_la_done
            ${EndIf}
            ${If} ${FileExists} "$LOCALAPPDATA\$2\Casterlabs-Caffeinated.exe"
                StrCpy $CasterlabsFound "1"
                StrCpy $CasterlabsExe "$LOCALAPPDATA\$2\Casterlabs-Caffeinated.exe"
                Goto detect_la_done
            ${EndIf}
            ${If} ${FileExists} "$LOCALAPPDATA\$2\Casterlabs Caffeinated.exe"
                StrCpy $CasterlabsFound "1"
                StrCpy $CasterlabsExe "$LOCALAPPDATA\$2\Casterlabs Caffeinated.exe"
                Goto detect_la_done
            ${EndIf}
            FindNext $1 $2
            Goto detect_la_loop
        detect_la_done:
        FindClose $1
    ${EndIf}

    ; --- Scan APPDATA\casterlabs* (root and app subdir) ---
    ${If} $CasterlabsFound == "0"
        FindFirst $1 $2 "$APPDATA\casterlabs*"
        detect_ap_loop:
            ${If} $2 == ""
                Goto detect_ap_done
            ${EndIf}
            ${If} ${FileExists} "$APPDATA\$2\app\Casterlabs-Caffeinated.exe"
                StrCpy $CasterlabsFound "1"
                StrCpy $CasterlabsExe "$APPDATA\$2\app\Casterlabs-Caffeinated.exe"
                Goto detect_ap_done
            ${EndIf}
            ${If} ${FileExists} "$APPDATA\$2\Casterlabs-Caffeinated.exe"
                StrCpy $CasterlabsFound "1"
                StrCpy $CasterlabsExe "$APPDATA\$2\Casterlabs-Caffeinated.exe"
                Goto detect_ap_done
            ${EndIf}
            ${If} ${FileExists} "$APPDATA\$2\Casterlabs Caffeinated.exe"
                StrCpy $CasterlabsFound "1"
                StrCpy $CasterlabsExe "$APPDATA\$2\Casterlabs Caffeinated.exe"
                Goto detect_ap_done
            ${EndIf}
            FindNext $1 $2
            Goto detect_ap_loop
        detect_ap_done:
        FindClose $1
    ${EndIf}

    ; --- Program Files fallback ---
    ${If} $CasterlabsFound == "0"
    ${AndIf} ${FileExists} "$PROGRAMFILES64\Casterlabs Caffeinated\Casterlabs Caffeinated.exe"
        StrCpy $CasterlabsFound "1"
        StrCpy $CasterlabsExe "$PROGRAMFILES64\Casterlabs Caffeinated\Casterlabs Caffeinated.exe"
    ${EndIf}

    ; Found — skip this page and continue to install
    ${If} $CasterlabsFound == "1"
        Abort
    ${EndIf}

    ; Not found — tell the user, open casterlabs.co, then cancel
    MessageBox MB_OK|MB_ICONEXCLAMATION \
        "Casterlabs Caffeinated is not installed.$\r$\n$\r$\nChatIncluded requires Casterlabs to run. Please install Casterlabs first, then run this installer again.$\r$\n$\r$\ncasterlabs.co will open in your browser."
    ExecShell "open" "https://casterlabs.co"
    Quit
FunctionEnd

;--------------------------------
; Plugins folder picker page
; Pre-fills with the default path. User can browse or paste the correct folder.
; The leave function validates the path exists before proceeding.

Function PluginsFolderPage
    nsDialogs::Create 1018
    Pop $PluginsDialog
    ${If} $PluginsDialog == error
        Abort
    ${EndIf}

    SetCtlColors $HWNDPARENT "F0F0F0" "0A0A0F"
    SetCtlColors $PluginsDialog "F0F0F0" "0A0A0F"

    ; Hide Back
    GetDlgItem $0 $HWNDPARENT 3
    ShowWindow $0 ${SW_HIDE}

    ; Title
    ${NSD_CreateLabel} 0 4u 100% 12u "Select Plugins Folder"
    Pop $0
    SetCtlColors $0 "FFFFFF" "0A0A0F"
    CreateFont $0 "Segoe UI" 15 700
    SendMessage $0 ${WM_SETFONT} $0 0

    ; Instructions
    ${NSD_CreateLabel} 10u 19u 280u 36u "Open Casterlabs, go to Settings > Plugins, and click 'Open Plugins Folder'. Copy that path and paste it below, or use Browse."
    Pop $0
    SetCtlColors $0 "CCCCCC" "0A0A0F"
    CreateFont $1 "Segoe UI" 9 400
    SendMessage $0 ${WM_SETFONT} $1 0

    ; Path label
    ${NSD_CreateLabel} 10u 59u 280u 10u "Plugins folder path:"
    Pop $0
    SetCtlColors $0 "AAAAAA" "0A0A0F"
    CreateFont $1 "Segoe UI" 9 400
    SendMessage $0 ${WM_SETFONT} $1 0

    ; Path text field — pre-filled with default
    ${If} $PluginsPath == ""
        StrCpy $PluginsPath "$APPDATA\casterlabs-caffeinated\plugins"
    ${EndIf}
    ${NSD_CreateText} 10u 70u 215u 12u "$PluginsPath"
    Pop $PluginsPathField
    SetCtlColors $PluginsPathField "F0F0F0" "1A1A2E"

    ; Browse button
    ${NSD_CreateButton} 230u 69u 60u 14u "Browse..."
    Pop $0
    GetFunctionAddress $1 OnBrowsePluginsFolder
    nsDialogs::OnClick $0 $1

    nsDialogs::Show
FunctionEnd

Function OnBrowsePluginsFolder
    ${NSD_GetText} $PluginsPathField $PluginsPath
    nsDialogs::SelectFolderDialog "Select the Casterlabs plugins folder" "$PluginsPath"
    Pop $0
    ${If} $0 != error
        StrCpy $PluginsPath $0
        ${NSD_SetText} $PluginsPathField $0
    ${EndIf}
FunctionEnd

Function PluginsFolderPageLeave
    ${NSD_GetText} $PluginsPathField $PluginsPath

    ; Reject empty path
    ${If} $PluginsPath == ""
        MessageBox MB_OK|MB_ICONEXCLAMATION \
            "Please enter the path to your Casterlabs plugins folder.$\r$\n$\r$\nIn Casterlabs: Settings > Plugins > Open Plugins Folder."
        Abort
    ${EndIf}

    ; Reject path that doesn't exist
    ${IfNot} ${FileExists} "$PluginsPath\*.*"
        MessageBox MB_OK|MB_ICONEXCLAMATION \
            "The folder '$PluginsPath' was not found.$\r$\n$\r$\nOpen Casterlabs, go to Settings > Plugins, and click 'Open Plugins Folder'. Copy that path and try again."
        Abort
    ${EndIf}
FunctionEnd

;--------------------------------
; Install section

Section "Install"
    StrCpy $InstallSucceeded "0"

    ; Remove any previously installed version of the plugin (handles version renames)
    FindFirst $1 $2 "$PluginsPath\chatincluded-*.jar"
    cleanup_old_loop:
        ${If} $2 == ""
            Goto cleanup_old_done
        ${EndIf}
        Delete "$PluginsPath\$2"
        FindNext $1 $2
        Goto cleanup_old_loop
    cleanup_old_done:
    FindClose $1

    ; Install the plugin JAR into the user-confirmed folder
    SetOutPath "$PluginsPath"
    ClearErrors
    File "chatincluded-1.1.0.jar"
    IfErrors install_file_failed

    StrCpy $InstallSucceeded "1"
    Goto install_files_done

    install_file_failed:
        MessageBox MB_OK|MB_ICONEXCLAMATION \
            "ChatIncluded could not be copied to:$\r$\n$\r$\n  $PluginsPath$\r$\n$\r$\nIf Casterlabs is open, close it and run the installer again.$\r$\nIf the problem persists, check that you have write access to that folder."
        Abort

    install_files_done:

    ; Write uninstaller to its own directory
    CreateDirectory "$LOCALAPPDATA\ChatIncluded"
    WriteUninstaller "$LOCALAPPDATA\ChatIncluded\Uninstall.exe"

    ; Register in Windows Add/Remove Programs (HKCU — no admin needed)
    WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\ChatIncluded" \
        "DisplayName" "ChatIncluded"
    WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\ChatIncluded" \
        "UninstallString" '"$LOCALAPPDATA\ChatIncluded\Uninstall.exe"'
    WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\ChatIncluded" \
        "DisplayVersion" "1.1.0"
    WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\ChatIncluded" \
        "Publisher" "KiraLovey"
    WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\ChatIncluded" \
        "DisplayIcon" "$LOCALAPPDATA\ChatIncluded\Uninstall.exe"
    WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\ChatIncluded" \
        "URLInfoAbout" "https://chatincluded.live"
    WriteRegDWORD HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\ChatIncluded" \
        "NoModify" 1
    WriteRegDWORD HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\ChatIncluded" \
        "NoRepair" 1

    ; Save the plugins path so the uninstaller can find the JAR later
    WriteRegStr HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\ChatIncluded" \
        "PluginsPath" "$PluginsPath"
SectionEnd

;--------------------------------
; Uninstall section

Section "Uninstall"
    MessageBox MB_YESNO "Remove ChatIncluded from your Casterlabs plugins?" IDYES uninstall_proceed
    Abort
    uninstall_proceed:

    ; Read the plugins path saved at install time
    ReadRegStr $0 HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\ChatIncluded" "PluginsPath"
    ${If} $0 == ""
        StrCpy $0 "$APPDATA\casterlabs-caffeinated\plugins"
    ${EndIf}

    ; Remove any version of the plugin JAR
    FindFirst $1 $2 "$0\chatincluded-*.jar"
    uninstall_jar_loop:
        ${If} $2 == ""
            Goto uninstall_jar_done
        ${EndIf}
        Delete "$0\$2"
        FindNext $1 $2
        Goto uninstall_jar_loop
    uninstall_jar_done:
    FindClose $1

    ; Remove the uninstaller and its directory
    Delete "$LOCALAPPDATA\ChatIncluded\Uninstall.exe"
    RMDir "$LOCALAPPDATA\ChatIncluded"

    ; Remove Add/Remove Programs entry
    DeleteRegKey HKCU "Software\Microsoft\Windows\CurrentVersion\Uninstall\ChatIncluded"
SectionEnd

Function InstFilesLeave
FunctionEnd

;--------------------------------
; Success page

Var SuccessDialog
Var SuccessTitle
Var SuccessText
Var OpenButton

Function SuccessPage
    nsDialogs::Create 1018
    Pop $SuccessDialog
    ${If} $SuccessDialog == error
        Abort
    ${EndIf}

    ; Colour both the outer NSIS frame and the inner dialog
    SetCtlColors $HWNDPARENT "F0F0F0" "0A0A0F"
    SetCtlColors $SuccessDialog "F0F0F0" "0A0A0F"

    ; Rename native Next button to "Close" — exits cleanly on the last page
    GetDlgItem $0 $HWNDPARENT 1
    SendMessage $0 ${WM_SETTEXT} 0 "STR:Close"

    ; Hide Back
    GetDlgItem $0 $HWNDPARENT 3
    ShowWindow $0 ${SW_HIDE}

    ; Hide Cancel — X button is handled by .onUserAbort above
    GetDlgItem $0 $HWNDPARENT 2
    ShowWindow $0 ${SW_HIDE}

    ${If} $InstallSucceeded == "1"
        ; Title
        ${NSD_CreateLabel} 0 4u 100% 12u "ChatIncluded Installed!"
        Pop $SuccessTitle
        SetCtlColors $SuccessTitle "FFFFFF" "0A0A0F"
        CreateFont $0 "Segoe UI" 15 700
        SendMessage $SuccessTitle ${WM_SETFONT} $0 0

        ; Instructions
        ${NSD_CreateLabel} 10u 19u 280u 46u "ChatIncluded is now in your Casterlabs plugins.$\r$\n$\r$\nIn Casterlabs: Widgets & Alerts > + > Other >$\r$\nChatIncluded Settings, then enter your DeepL key."
        Pop $SuccessText
        SetCtlColors $SuccessText "CCCCCC" "0A0A0F"
        CreateFont $1 "Segoe UI" 9 400
        SendMessage $SuccessText ${WM_SETFONT} $1 0

        ; Prompt
        ${NSD_CreateLabel} 10u 68u 280u 10u "Open Casterlabs now?"
        Pop $0
        SetCtlColors $0 "F0F0F0" "0A0A0F"
        CreateFont $2 "Segoe UI" 10 600
        SendMessage $0 ${WM_SETFONT} $2 0

        ; "Open Casterlabs" — launches and then clicks the native Close button
        ${NSD_CreateButton} 10u 81u 100u 12u "Open Casterlabs"
        Pop $OpenButton
        GetFunctionAddress $0 OpenCasterlabs
        nsDialogs::OnClick $OpenButton $0
    ${Else}
        ; Title
        ${NSD_CreateLabel} 0 4u 100% 12u "Installation Failed"
        Pop $SuccessTitle
        SetCtlColors $SuccessTitle "FF4444" "0A0A0F"
        CreateFont $0 "Segoe UI" 15 700
        SendMessage $SuccessTitle ${WM_SETFONT} $0 0

        ; Error detail
        ${NSD_CreateLabel} 10u 19u 280u 56u "ChatIncluded could not be installed.$\r$\n$\r$\nClose Casterlabs if it is running, then run this installer again. If the problem continues, check that you have write access to the plugins folder."
        Pop $SuccessText
        SetCtlColors $SuccessText "FF8888" "0A0A0F"
        CreateFont $1 "Segoe UI" 9 400
        SendMessage $SuccessText ${WM_SETFONT} $1 0
    ${EndIf}

    nsDialogs::Show
FunctionEnd

;--------------------------------
; Open Casterlabs — searches the same wildcard dirs at runtime so it works
; even when detection set $CasterlabsExe to a non-existent path.

Function OpenCasterlabs
    StrCpy $3 ""   ; will hold the resolved exe path

    ; 1. Known correct paths — updater is the real launcher
    ${If} ${FileExists} "$PROGRAMFILES\Casterlabs Caffeinated\Casterlabs-Caffeinated-Updater.exe"
        StrCpy $3 "$PROGRAMFILES\Casterlabs Caffeinated\Casterlabs-Caffeinated-Updater.exe"
    ${EndIf}
    ${If} $3 == ""
    ${AndIf} ${FileExists} "$PROGRAMFILES64\Casterlabs Caffeinated\Casterlabs-Caffeinated-Updater.exe"
        StrCpy $3 "$PROGRAMFILES64\Casterlabs Caffeinated\Casterlabs-Caffeinated-Updater.exe"
    ${EndIf}

    ; 2. Stored path from detection
    ${If} $3 == ""
    ${AndIf} $CasterlabsExe != ""
    ${AndIf} ${FileExists} "$CasterlabsExe"
        StrCpy $3 "$CasterlabsExe"
    ${EndIf}

    ; 3. Scan LOCALAPPDATA\casterlabs* (root and app subdir)
    ${If} $3 == ""
        FindFirst $1 $2 "$LOCALAPPDATA\casterlabs*"
        open_la_loop:
            ${If} $2 == ""
                Goto open_la_done
            ${EndIf}
            ${If} ${FileExists} "$LOCALAPPDATA\$2\app\Casterlabs-Caffeinated.exe"
                StrCpy $3 "$LOCALAPPDATA\$2\app\Casterlabs-Caffeinated.exe"
                Goto open_la_done
            ${EndIf}
            ${If} ${FileExists} "$LOCALAPPDATA\$2\Casterlabs-Caffeinated.exe"
                StrCpy $3 "$LOCALAPPDATA\$2\Casterlabs-Caffeinated.exe"
                Goto open_la_done
            ${EndIf}
            ${If} ${FileExists} "$LOCALAPPDATA\$2\Casterlabs Caffeinated.exe"
                StrCpy $3 "$LOCALAPPDATA\$2\Casterlabs Caffeinated.exe"
                Goto open_la_done
            ${EndIf}
            FindNext $1 $2
            Goto open_la_loop
        open_la_done:
        FindClose $1
    ${EndIf}

    ; 4. Scan APPDATA\casterlabs* (root and app subdir)
    ${If} $3 == ""
        FindFirst $1 $2 "$APPDATA\casterlabs*"
        open_ap_loop:
            ${If} $2 == ""
                Goto open_ap_done
            ${EndIf}
            ${If} ${FileExists} "$APPDATA\$2\app\Casterlabs-Caffeinated.exe"
                StrCpy $3 "$APPDATA\$2\app\Casterlabs-Caffeinated.exe"
                Goto open_ap_done
            ${EndIf}
            ${If} ${FileExists} "$APPDATA\$2\Casterlabs-Caffeinated.exe"
                StrCpy $3 "$APPDATA\$2\Casterlabs-Caffeinated.exe"
                Goto open_ap_done
            ${EndIf}
            ${If} ${FileExists} "$APPDATA\$2\Casterlabs Caffeinated.exe"
                StrCpy $3 "$APPDATA\$2\Casterlabs Caffeinated.exe"
                Goto open_ap_done
            ${EndIf}
            FindNext $1 $2
            Goto open_ap_loop
        open_ap_done:
        FindClose $1
    ${EndIf}

    ; 5. Program Files fallback
    ${If} $3 == ""
    ${AndIf} ${FileExists} "$PROGRAMFILES64\Casterlabs Caffeinated\Casterlabs Caffeinated.exe"
        StrCpy $3 "$PROGRAMFILES64\Casterlabs Caffeinated\Casterlabs Caffeinated.exe"
    ${EndIf}

    ; Launch or show error, then close installer
    ${If} $3 != ""
        Exec '"$3"'
        SendMessage $HWNDPARENT ${WM_COMMAND} 1 0
    ${Else}
        MessageBox MB_OK "Could not find Casterlabs.$\nPlease open it manually."
    ${EndIf}
FunctionEnd
