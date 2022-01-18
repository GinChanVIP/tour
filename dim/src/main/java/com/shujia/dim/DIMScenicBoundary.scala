package com.shujia.dim
import com.shujia.common.SparkTool
import org.apache.spark.sql.SparkSession

/**
 * @author chengjin
 * @version 2022/1/13 15:03
 */

object DIMScenicBoundary extends SparkTool {

  override def run(spark: SparkSession): Unit = {

    /**
     * 景区配置表
     *
     */

    spark.sql(
      """
        |insert overwrite table dim.dim_scenic_boundary
        |select * from ods.ods_scenic_boundary
        |
      """.stripMargin)
  }

}

