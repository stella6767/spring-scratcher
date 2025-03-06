package freeapp.me.flywayjooq.dto

class PageImpl<T>(
    val page: Long,
    val pageSize: Long,
    val totalCount: Long,
    val data: T
) {

    val totalPage = (totalCount + pageSize - 1) / pageSize

    override fun toString(): String {
        return "PageImpl(page=$page, pageSize=$pageSize, totalCount=$totalCount, data=$data, totalPage=$totalPage)"
    }


}
