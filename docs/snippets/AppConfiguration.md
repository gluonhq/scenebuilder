# SceneBuilder Configuration

## Data Storage

| Location                | Responsible Classes                                        | Description | 
| :---------------------- | :--------------------------------------------------------- | :---------- |
| applicationDataFolder   | `c.o.j.scenebuilder.app.AppPlatform`                       | Message box and user library folders are located here |
| userLibraryFolder       | `c.o.j.scenebuilder.app.AppPlatform`                       | May contain JAR file with a JavaFX controls inside |
| logsFolder              | `c.o.j.scenebuilder.app.AppPlatform`                       | Here the `scenebuilder-x.y.z.log` file is stored, usually inside the users profiles directory |
| Java Preferences        | `c.o.j.scenebuilder.app.preferences.PreferencesController` | Standardized persistent storage of application settings |


### ApplicationDataFolder

| Platform | until Version 17     | Version 18 and later        |
| -------- | -------------------- |---------------------------- |
| Windows  | `%APPDATA%\Scene Builder` | `%APPDATA%\Scene Builder-18.0.1` |
| Linux    | tbd.                 |  tbd.                       |
| MacOS    | tbd.                 |  tbd.                       |

### UserLibraryFolder

| Location |
| -------------------------- |
| `applicationDataFolder/Library`  |

### MessageBoxFolder

| Location |
| ---------------------- |
| `applicationDataFolder/MB`  |

### Log File

| Version | Location |
| ------- | ---------------------------------- |
| <= 17   | `%USERPROFILE%\.scenebuilder\logs\scenebuilder.log`       |
| >= 18   | `%USERPROFILE%\.scenebuilder\logs\scenebuilder-18.0.0.log` |

### Preferences

Scene Builder stores its preferences in following root node:

| Node | Description |
|------|-------------|
| `com.oracle.javafx.scenebuilder.app.preferences` | root node |
| `com.oracle.javafx.scenebuilder.app.preferences.SB_2.0` | Preferences for all versions <= 17 |
| `com.oracle.javafx.scenebuilder.app.preferences.SB_18.0.0` | Preferences for all versions > 17 |

Each node may hold following child nodes:

| Child Node | Description |
|------------|-------------|
| `ARTIFACTS` | tbd.       |
| `DOCUMENTS` | one child node per recent document |
| `REPOSITORIES` | tbd.       |

SB_2.0 preference keys:
* `IMPORTED_GLUON_JARS`, `LAST_SENT_TRACKING_INFO_DATE`, `RECENT_ITEMS`, `REGISTRATION_EMAIL`, `REGISTRATION_HASH`, `REGISTRATION_OPT_IN`

SB_18.0.0 preference keys:
* same like 2.0 and:
* `PERFORM_IMPORT` = `true|false`  , if missing or true, app preferences will be imported
* `IMPORT_USER_LIBRARY` = `true|false`  , if missing or true, user library directory will be imported
