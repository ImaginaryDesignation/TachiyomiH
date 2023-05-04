package tachiyomi.domain.backup.service

import tachiyomi.core.preference.PreferenceStore
import tachiyomi.core.provider.FolderProvider

class BackupPreferences(
    private val folderProvider: FolderProvider,
    private val preferenceStore: PreferenceStore,
) {

    fun backupsDirectory() = preferenceStore.getString("backup_directory", folderProvider.path())

    fun numberOfBackups() = preferenceStore.getInt("backup_slots", 2)

    fun backupInterval() = preferenceStore.getInt("backup_interval", 12)

    fun showAutoBackupNotifications() = preferenceStore.getBoolean("show_auto_backup_notifications", true)

    fun backupLastTimestamp() = preferenceStore.getLong("backup_last_timestamp", 0L)
}
