package eu.kanade.domain

import tachiyomi.core.preference.PreferenceStore

class UnsortedPreferences(
    private val preferenceStore: PreferenceStore,
) {

    fun migrateFlags() = preferenceStore.getInt("migrate_flags", Int.MAX_VALUE)

    fun migrationSources() = preferenceStore.getString("migrate_sources", "")

    fun smartMigration() = preferenceStore.getBoolean("smart_migrate", false)

    fun useSourceWithMost() = preferenceStore.getBoolean("use_source_with_most", false)

    fun skipPreMigration() = preferenceStore.getBoolean("skip_pre_migration", false)

    fun hideNotFoundMigration() = preferenceStore.getBoolean("hide_not_found_migration", false)
}
