package com.datastax.alexott.demos

import com.datastax.oss.driver.api.core.CqlIdentifier
import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.mapper.annotations.*
import java.net.InetSocketAddress


@Entity
@CqlName("app_category_agg")
data class AppCategoryAggData(
    @PartitionKey var category: String,
    @CqlName("app_count") var appCount: Int? = null,
    @CqlName("sp_count") var spCount: Int? = null,
    @CqlName("subscriber_count") var subscriberCount: Int? = null,
    @CqlName("window_revenue") var windowRevenue: Long? = null,
    @CqlName("top_apps") var topApps: List<Map<String, Int>>? = null
) {
    constructor() : this("")
}

@Dao
interface AppCategoryAggDao {
    @Insert
    fun insert(appCatAgg: AppCategoryAggData)

    @Select
    fun findByCategory(appCat: String): AppCategoryAggData?
}

@Mapper
interface AppCategoryMapper {
    @DaoFactory
    fun appCategoryDao(@DaoKeyspace keyspace: CqlIdentifier?): AppCategoryAggDao?
}

object KtTestObjMapper {
    @JvmStatic
    fun main(args: Array<String>) {
        val session = CqlSession.builder()
                .addContactPoint(InetSocketAddress("10.101.34.176", 9042))
                .build()

        // get mapper - please note that we need to use AppCategoryMapperBuilder
        // that is generated by annotation processor
        val mapper: AppCategoryMapper = AppCategoryMapperBuilder(session).build()

        val dao: AppCategoryAggDao? = mapper.appCategoryDao(CqlIdentifier.fromCql("test"))

        val appObj = AppCategoryAggData("kotlin2",
                10, 11, 12, 34,
                listOf(mapOf("t2" to 2)))
        dao?.insert(appObj)

        val obj2 = dao?.findByCategory("test")
        println("Object from =$obj2")

        session.close()

    }

}