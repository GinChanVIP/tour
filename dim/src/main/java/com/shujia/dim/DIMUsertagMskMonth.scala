package com.shujia.dim

import com.shujia.common.SparkTool
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

/**
 * @author chengjin
 * @version 2022/1/13 15:05
 */

object DIMUsertagMskMonth extends SparkTool {
  override def run(spark: SparkSession): Unit = {
    /**
     * 用户画像表
     *
     */
    import spark.implicits._
    import org.apache.spark.sql.functions._
    /**
     * 读取ods层的用户画像表
     *
     */

    val usertag: DataFrame = spark
      .table("ods.ods_usertag_m")
      .where($"month_id" === month_id)
      .select(
        upper(md5(concat($"mdn", expr("'shujia'")))) as "mdn",
        upper(md5(concat($"name", expr("'shujia'")))) as "name",
        $"gender",
        $"age",
        upper(md5(concat($"id_number", expr("'shujia'")))) as "id_number",
        $"number_attr",
        $"trmnl_brand",
        $"trmnl_price",
        $"packg",
        $"conpot",
        $"resi_grid_id",
        $"resi_county_id"
      )


    //保存数据
    usertag.write
      .format("csv")
      .option("sep", "\t")
      .mode(SaveMode.Overwrite)
      .save(s"/daas/motl/dim/dim_usertag_msk_m/month_id=$month_id")


    //增加分区
    spark.sql(s"alter table dim.dim_usertag_msk_m add if not exists partition(month_id='$month_id')")
  }
}

