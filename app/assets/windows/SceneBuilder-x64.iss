;This file will be executed next to the application bundle image
;I.e. current directory will contain folder SceneBuilder with application files
[Setup]
AppId={{com.oracle.javafx.scenebuilder.app}}
AppName=Scene Builder
AppVersion=VERSION
AppVerName=SceneBuilder VERSION
AppPublisher=Gluon
AppComments=Scene Builder
AppCopyright=Copyright (c) 2012, 2014, Oracle and/or its affiliates. Copyright (c) 2015, 2016, Gluon and/or its affiliates.
;AppPublisherURL=http://java.com/
;AppSupportURL=http://java.com/
;AppUpdatesURL=http://java.com/

;Set default to: C:\Program Files instead of User\AppData:
DefaultDirName={localappdata}\SceneBuilder
DisableStartupPrompt=Yes

;Enable dir page to allow custom location:
DisableDirPage=No
DisableProgramGroupPage=Yes
DisableReadyPage=Yes
DisableFinishedPage=Yes
DisableWelcomePage=Yes
DefaultGroupName=Gluon
;Optional License
LicenseFile=LICENSE
;WinXP or above
MinVersion=0,5.1 
OutputBaseFilename=SceneBuilder-VERSION
Compression=lzma
SolidCompression=yes
PrivilegesRequired=lowest
SetupIconFile=SceneBuilder\SceneBuilder.ico
UninstallDisplayIcon={app}\SceneBuilder.ico
UninstallDisplayName=SceneBuilder
WizardImageStretch=No
WizardSmallImageFile=SceneBuilder-setup-icon.bmp   
ArchitecturesInstallIn64BitMode=x64


[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "SceneBuilder\SceneBuilder.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "SceneBuilder\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\SceneBuilder"; Filename: "{app}\SceneBuilder.exe"; IconFilename: "{app}\SceneBuilder.ico"; Check: returnTrue()
Name: "{commondesktop}\SceneBuilder"; Filename: "{app}\SceneBuilder.exe";  IconFilename: "{app}\SceneBuilder.ico"; Check: returnFalse()


[Run]
Filename: "{app}\SceneBuilder.exe"; Parameters: "-Xappcds:generatecache"; Check: returnFalse()
Filename: "{app}\SceneBuilder.exe"; Description: "{cm:LaunchProgram,SceneBuilder}"; Flags: nowait postinstall skipifsilent; Check: returnTrue()
Filename: "{app}\SceneBuilder.exe"; Parameters: "-install -svcName ""SceneBuilder"" -svcDesc ""Scene Builder"" -mainExe ""SceneBuilder.exe""  "; Check: returnFalse()

[UninstallRun]
Filename: "{app}\SceneBuilder.exe "; Parameters: "-uninstall -svcName SceneBuilder -stopOnUninstall"; Check: returnFalse()

[Code]
function returnTrue(): Boolean;
begin
  Result := True;
end;

function returnFalse(): Boolean;
begin
  Result := False;
end;

function InitializeSetup(): Boolean;
begin
// Possible future improvements:
//   if version less or same => just launch app
//   if upgrade => check if same app is running and wait for it to exit
//   Add pack200/unpack200 support? 
  Result := True;
end;  
