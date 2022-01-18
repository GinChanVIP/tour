package ads

import java.awt.geom.Point2D
import com.shujia.common.grid.Grid
import com.shujia.common.poly.Polygon
import com.shujia.common.SparkTool
import org.apache.spark.sql.expressions.{UserDefinedFunction, Window}
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}


/**
 * @author chengjin
 * @version 2022/1/15 21:41
 */
object AdsScenicTouristMskDay extends SparkTool{
  /**
   * 景区游客判断条件
   * 1、常住地不在进去内
   * 2、停留点出现在景区内，在景区停留时间大于半小时
   *
   */

  override def run(spark: SparkSession): Unit = {
    import spark.implicits._
    import org.apache.spark.sql.functions._

    /**
     * 读取数据
     *
     */

    //1、用户画像表
    val usertag: DataFrame = spark
      .table("dim.dim_usertag_msk_m ")
      .where($"month_id" === month_id)

    //2、停留表
    val stayPoint: DataFrame = spark
      .table("dws.dws_staypoint_msk_d")
      .where($"day_id" === day_id)

    //3、景区网格列表
    val scenicGrid: DataFrame = spark.table("dim.dim_scenic_grid")

    //行政区配置表
    val adminCode: DataFrame = spark
      .table("dim.dim_admincode")
      .select($"county_id" as "o_county_id",$"city_id" as "o_city_id")


    /**
     * 1、常住地不在景区内
     *
     */
    //关联用户画像表获取游客常住地
    stayPoint
      .join(usertag, "mdn")
      //关联景区配置表获取景区的边界
      .crossJoin(scenicGrid)
      //判断常住地是否在边界内
      .filter(!array_contains(split($"grids", ","), $"resi_grid_id"))
      //判断停留点出现在景区内
      .filter(array_contains(split($"grids", ","), $"grid_id"))
      .join(adminCode, $"resi_county_id" === $"o_county_id")
      //计算用户在景区内第一个点的数据
      .withColumn("d_arrive_time", min($"grid_first_time") over Window.partitionBy($"mdn", $"scenic_id"))
      //计算游客在景区内总的停留时间
      .withColumn("d_stay_time", sum($"duration") over Window.partitionBy($"mdn", $"scenic_id"))
      //在景区停留时间大于半小时
      .filter($"d_stay_time" >= 30)
      .select($"mdn", $"o_city_id" as "source_city_id", $"scenic_id", $"scenic_name", $"d_arrive_time", $"d_stay_time")
      .distinct()
      .write
      .format("csv")
      .option("sep", "\t")
      .mode(SaveMode.Overwrite)
      .save(s"/daas/motl/ads/ads_scenic_tourist_msk_d/day_id=$day_id")

    //增加分区
    spark.sql(s"alter table  ads.ads_scenic_tourist_msk_d add if not exists partition(day_id='$day_id') ")

    while (true){

    }
  }
}
