package es.juanavila.liverss.application

data class PagingOptions(
    val cursor: String = "",
    val sinceId: Long = 0,
    val count: Int = 20
)

data class Page<T>(
    val data : List<T>,
    val beforeCursor : String,
    val afterCursor : String
) {
    fun hasBeforeCursor(): Boolean {
        return beforeCursor != "0"
    }
}