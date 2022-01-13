package ads

import com.shujia.common.grid.Geography
import com.shujia.common.SparkTool
import org.apache.spark.sql.expressions.{UserDefinedFunction, Window}
import org.apache.spark.sql._

/**
  *
  * 市游客计算
  */
object ADSCityTouristMskDay extends SparkTool {
  override def run(spark: SparkSession): Unit = {
    import spark.implicits._
    import org.apache.spark.sql.functions._
    /**
      * 1、读取数据
      *
      */

    //停留表
    val stayPoint: DataFrame = spark
      .table("dws.dws_staypoint_msk_d")
      .where($"day_id" === day_id)

    //用户画像表
    val usertag: DataFrame = spark
      .table("dim.dim_usertag_msk_m ")
      .where($"month_id" === month_id)

    //行政区配置表
    val adminCode: DataFrame = spark.table("dim.dim_admincode")

    /**
      * 游客计算条件
      * 1、市内停留时间大于3小时
      * 2、最远出游距离大于10KM
      *
      */

    //计算两个网格距离的函数
    val calculateLength: UserDefinedFunction = udf((grid1: String, grid2: String) => {
      Geography.calculateLength(grid1.toLong, grid2.toLong)
    })


    //关联行政区配置表获取停留点市编号
    val tourist: DataFrame = stayPoint
      .join(adminCode.hint("broadcast"), "county_id")
      //计算用户在同一个市内的停留时间
      .withColumn("d_stay_time", sum($"duration") over Window.partitionBy($"mdn", $"city_id"))
      //过滤停留时间小于3小时的用户
      .filter($"d_stay_time" >= 180)
      //关联用户画像表获取常住地网格
      .join(usertag.hint("broadcast"), "mdn")
      //计算每一个停留点到常住地的距离
      .withColumn("distance", calculateLength($"resi_grid_id", $"grid_id"))
      //获取用户在市内最远的出游距离
      .withColumn("d_max_distance", max($"distance") over Window.partitionBy($"mdn", $"city_id"))
      //过滤最远距离小于10km的用户
      .filter($"d_max_distance" > 10000)
      //整理数据
      .select($"mdn", $"resi_county_id" as "source_county_id", $"city_id" as "d_city_id", round($"d_stay_time" / 60, 2) as "d_stay_time", round($"d_max_distance" / 1000, 2) as "d_max_distance")
      .distinct()


    tourist.write
      .format("csv")
      .option("sep", "\t")
      .mode(SaveMode.Overwrite)
      .save(s"/daas/motl/ads/ads_city_tourist_msk_d/day_id=$day_id")


    //增加分区
    spark.sql(s"alter table ads.ads_city_tourist_msk_d add if not exists partition(day_id='$day_id')")

  }
}
