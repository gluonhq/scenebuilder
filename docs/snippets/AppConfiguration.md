# SceneBuilder Configuration

## Data Storage

| Location                | Responsible Classes                                        | Description | 
| :---------------------- | :--------------------------------------------------------- | :---------- |
| applicationDataFolder   | `c.o.j.scenebuilder.app.AppPlatform`                       | Message box and user library folders are located here |
| messageBoxFolder        | `c.o.j.scenebuilder.app.AppPlatform`                       | Contents of message box |
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

### LogsFolder

| Version | Location |
| ------- | ---------------------------------- |
| <= 17   | `%USERPROFILE%\.scenebuilder\logs`       |
| >= 18   | `%USERPROFILE%\.scenebuilder-18.0.0\logs` |

### Preferences

Node Structure until version 17:

Root Node:	`com.oracle.javafx.scenebuilder.app.preferences`
 * SB_2.0 (`IMPORTED_GLUON_JARS`, `LAST_SENT_TRACKING_INFO_DATE`, `RECENT_ITEMS`, `REGISTRATION_EMAIL`, `REGISTRATION_HASH`, `REGISTRATION_OPT_IN`)
   * ARTIFACTS
   * DOCUMENTS (separate node for each document's settings)
   * REPOSITIRIES
   
Structure with versions >= 18.0.0:

Root Node:	`com.oracle.javafx.scenebuilder.app.preferences`
 * SB_18.0.0 
   * ARTIFACTS
   * DOCUMENTS
   * REPOSITIRIES
