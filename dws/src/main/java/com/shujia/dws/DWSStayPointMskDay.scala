package com.shujia.dws

import com.shujia.common.SparkTool
import com.shujia.common.grid.Grid
import org.apache.spark.sql.expressions.{UserDefinedFunction, Window}
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

import java.awt.geom.Point2D

/**
 * @author chengjin
 * @version 2022/1/12 15:16
 */
object DWSStayPointMskDay extends SparkTool {
  override def run(spark: SparkSession): Unit = {

    import spark.implicits._
    import org.apache.spark.sql.functions._

    /**
     * 2.自定义函数获取网格中心点的经度
     *
     */

    val getLongi: UserDefinedFunction = udf((grid: String) => {
      val point: Point2D.Double = Grid.getCenter(grid.toLong)
      point.getX
    })

    //获取维度
    val getLati: UserDefinedFunction = udf((grid: String) => {
      val point: Point2D.Double = Grid.getCenter(grid.toLong)
      point.getY
    })

    /**
     * 1.读取位置融合表
     */
    val stayPointDF: DataFrame = spark
      .table("dwd.dwd_res_regn_mergelocation_msk_d")
      .where($"day_id" === day_id)
      .withColumn("start_date", split($"start_time", ",")(1))
      .withColumn("end_date", split($"start_time", ",")(0))
      .withColumn("beGrid", lag("grid_id", 1, "") over Window.partitionBy("mdn").orderBy("start_date"))
      .withColumn("flag", when($"grid_id" === $"beGrid", 0).otherwise(1))
      .withColumn("category", sum($"flag") over Window.partitionBy("mdn").orderBy("start_date"))
      .groupBy($"mdn", $"grid_id", $"category", $"county_id")
      .agg(min("start_date") as "grid_first_time", max("end_date") as "grid_last_time")
      //计算用户在网格中的停留时间
      .withColumn("duration", (unix_timestamp($"grid_last_time", "yyyyMMddHHmmss") - unix_timestamp($"grid_first_time", "yyyyMMddHHmmss")) / 60)
      //获取网格中心点的经纬度
      .withColumn("longi", getLongi($"grid_id"))
      .withColumn("lati", getLati($"grid_id"))

      .select($"mdn", round($"longi", 4), round($"lati", 4), $"grid_id", $"county_id", round($"duration", 2), $"grid_first_time", $"grid_last_time")

    /**
     * 3.保存数据
     */
    stayPointDF
      .write
      .format("csv")
      .option("sep","\t")
      .mode(SaveMode.Overwrite)
      .save("/daas/motl/dws/dws_staypoint_msk_d/day_id=" + day_id)

    /**
     * 4.增加分区
     */
    spark.sql(s"alter table dws.dws_staypoint_msk_d add if not exists partition(day_id='$day_id')")
  }
}
