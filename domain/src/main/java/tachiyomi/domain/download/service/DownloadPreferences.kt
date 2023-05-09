package tachiyomi.domain.download.service

import tachiyomi.core.preference.PreferenceStore
import tachiyomi.core.provider.FolderProvider

class DownloadPreferences(
    private val folderProvider: FolderProvider,
    private val preferenceStore: PreferenceStore,
) {

    fun downloadsDirectory() = preferenceStore.getString("download_directory", folderProvider.path())

    fun downloadOnlyOverWifi() = preferenceStore.getBoolean("pref_download_only_over_wifi_key", false)

    fun saveChaptersAsCBZ() = preferenceStore.getBoolean("save_chapter_as_cbz", true)

    fun splitTallImages() = preferenceStore.getBoolean("split_tall_images", false)

    fun autoDownloadWhileReading() = preferenceStore.getInt("auto_download_while_reading", 0)

    fun removeAfterReadSlots() = preferenceStore.getInt("remove_after_read_slots", -1)

    fun removeAfterMarkedAsRead() = preferenceStore.getBoolean("pref_remove_after_marked_as_read_key", false)

    fun removeBookmarkedChapters() = preferenceStore.getBoolean("pref_remove_bookmarked", false)

    fun removeExcludeCategories() = preferenceStore.getStringSet("remove_exclude_categories", emptySet())

    fun downloadNewChapters() = preferenceStore.getBoolean("download_new", false)

    fun downloadNewChapterCategories() = preferenceStore.getStringSet("download_new_categories", emptySet())

    fun downloadNewChapterCategoriesExclude() = preferenceStore.getStringSet("download_new_categories_exclude", emptySet())
}
