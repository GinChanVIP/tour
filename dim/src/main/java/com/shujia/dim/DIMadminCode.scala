package com.shujia.dim
import com.shujia.common.SparkTool
import org.apache.spark.sql.SparkSession

/**
 * @author chengjin
 * @version 2022/1/13 15:08
 */

object DIMadminCode extends SparkTool{
  override def run(spark: SparkSession): Unit = {

    /**
     * 读取ods行政区配置表
     *
     */

    spark
      .sql(
        """
          |insert overwrite table dim.dim_admincode
          |select * from ods.ods_admincode
          |
        """.stripMargin)

  }
}
