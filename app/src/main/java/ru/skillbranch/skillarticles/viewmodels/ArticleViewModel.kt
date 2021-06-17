package ru.skillbranch.skillarticles.viewmodels

import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.ArticleData
import ru.skillbranch.skillarticles.data.ArticlePersonalInfo
import ru.skillbranch.skillarticles.data.repositories.ArticleRepository
import ru.skillbranch.skillarticles.extensions.data.toAppSettings
import ru.skillbranch.skillarticles.extensions.data.toArticlePersonalInfo
import ru.skillbranch.skillarticles.extensions.format
import java.text.FieldPosition

class ArticleViewModel(private val articleId: String): BaseViewModel<ArticleState> (ArticleState()) {
    private val repository = ArticleRepository


    init {
        subscribeOnDataSource(getArticleData()){ article, state->
            article ?: return@subscribeOnDataSource null
            state.copy(
                shareLink = article.shareLink,
                title = article.title,
                category = article.category,
                categoryIcon = article.categoryIcon,
                date = article.date.format()
            )
        }

        subscribeOnDataSource(getArticleContent()) {content, state ->
            content ?: return@subscribeOnDataSource null
            state.copy(
                isLoadingContent = false,
                content = content
            )
        }

        subscribeOnDataSource(getArticlePersonalInfo()) {info, state ->
            info ?: return@subscribeOnDataSource null
            state.copy(
                isBookmark = info.isBookmark,
                isLike = info.isLike
            )
        }

        subscribeOnDataSource(repository.getAppSettings()) {settings, state ->
           state.copy(
                isDarkMode = settings.isDarkMode,
                isBigText = settings.isBigText
            )
        }

    }
    private fun getArticleContent(): LiveData<List<Any>?> {
        return repository.loadArticleContent(articleId)
    }

    private fun getArticleData(): LiveData<ArticleData?> {
        return repository.getArticle(articleId)
    }

    private fun getArticlePersonalInfo(): LiveData<ArticlePersonalInfo?> {
        return repository.loadArticlePersonalInfo(articleId)
    }

    fun handleNightMode(){
        val settings = currentState.toAppSettings()
        repository.updateSettings(settings.copy(isDarkMode = !settings.isDarkMode))
    }

    fun handleUpText(){
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = true))
    }

    fun handleDownText(){
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = false))
    }



    fun handleLike(){
        val toggleLike: () -> Unit = {
            val info = currentState.toArticlePersonalInfo()
            repository.updateArticlePersonalInfo(info.copy(isLike = !info.isLike))
        }

        toggleLike()

        val msg = if(currentState.isLike) Notify.TextMessage("Mark is liked")
        else {
            Notify.ActionMessage(
                "Don't like it anymore", //message
                "No, still like it", //action label on snackbar
                toggleLike //handle fun
            )
        }

        notify(msg)

    }

    fun handleBookmark(){
        val personalInfo = currentState.toArticlePersonalInfo()
        repository.updateArticlePersonalInfo(personalInfo.copy(isBookmark = !personalInfo.isBookmark))

        val message = if (currentState.isBookmark) Notify.TextMessage("Add to bookmarks")
        else Notify.TextMessage("Remove from bookmarks")

        notify(message)
    }

    fun handleShare(){
        val msg =  "Share is not implemented"
        notify(Notify.ErrorMessage(msg, "OK", null))
    }

    fun handleToggleMenu(){
        updateState {it.copy(isShowMenu = !it.isShowMenu)  }
    }

}

data class ArticleState(
    val isAuth: Boolean = false, //пользователь авторизован
    val isLoadingContent: Boolean =true, //контент загружается
    val isLoadingReviews: Boolean =true, //отзывы загружаются
    val isLike: Boolean =false,
    val isBookmark: Boolean =false,
    val isShowMenu: Boolean =false, //отображается меню
    val isBigText: Boolean =false,
    val isDarkMode: Boolean =false,
    val isSearch: Boolean =false, //режим поиска
    val searchQuery: String? = null, //поисковый запрос
    val searchResults: List<Pair<Int, Int>> = emptyList(), //результаты поиска (стартовая и конечная позиция)
    val searchPosition: Int = 0, //текущая позиция найденного результата
    val shareLink: String? = null, //ссылка Share
    val title: String? = null, //заголовок статьи
    val category: String? = null,
    val categoryIcon: Any? = null,
    val date: String? = null, // дата публикации
    val author: Any? = null, //автор
    val poster: String? = null, //обложка
    val content: List<Any> = emptyList(),
    val reviews: List<Any> = emptyList()   //комментарии
)