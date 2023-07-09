package tachiyomi.domain.manga.interactor

import tachiyomi.domain.manga.model.Manga
import tachiyomi.domain.manga.repository.MangaRepository

class GetFavoritesAndReadMangaNotInLibrary(
    private val mangaRepository: MangaRepository,
) {

    suspend fun await(): List<Manga> {
        return mangaRepository.getFavoritesAndReadMangaNotInLibrary()
    }
}
