package com.shujia.dwd

import org.apache.spark.sql.{DataFrame, Dataset, Row, SaveMode, SparkSession}
/**
 * @author chengjin
 * @version 2022/1/11 20:43
 */
object DWDResReignMergeLocationMskDay extends SparkTool {
  override def run(spark: SparkSession): Unit = {

    import org.apache.spark.sql.functions._
    import spark.implicits._

    /**
     * 1.从hive中读取表
     */
    val oidd: Dataset[Row] = spark
      .table("ods.ods_oidd")
      .where($"day_id" === day_id)

    val ddr: Dataset[Row] = spark
      .table("ods.ods_ddr")
      .where($"day_id" === day_id)

    val dpi = spark
      .table("ods.ods_dpi")
      .where($"day_id" === day_id)

    val wcdr: Dataset[Row] = spark
      .table("ods.ods_wcdr")
      .where($"day_id" === day_id)

    /**
     * 2.融合表
     */
    val merge: Dataset[Row] = oidd
      .union(ddr)
      .union(dpi)
      .union(wcdr)

    /**
     * 3.筛选字段与hive中创建的表一一对应
     */
    val mergeLocationDF: DataFrame = merge.select(
      // 加盐
      upper(md5(concat($"mdn", expr("'shujia'")))) as "mdn",
      $"start_time",
      $"county_id",
      $"longi",
      $"lati",
      $"bsid",
      $"grid_id",
      $"biz_type",
      $"event_type",
      $"data_source"
    )

    /**
     * 4.保存数据
     */
    mergeLocationDF
      .write
      .format("csv")
      .option("sep","\t")
      .mode(SaveMode.Overwrite)
      // 保存到/daas/motl/dwd/dwd_res_regn_mergelocation_msk_d/day_id=20180503
      .save("/daas/motl/dwd/dwd_res_regn_mergelocation_msk_d/day_id=" + day_id)

    /**
     * 5.增加分区
     */
    spark.sql(
      """
        |alter table dwd.dwd_res_regn_mergelocation_msk_d  add if not exists partition(day_id='$day_id')
        |""".stripMargin)
  }
}
