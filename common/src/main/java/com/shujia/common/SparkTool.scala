package com.shujia.common

import org.apache.spark.internal.Logging
import org.apache.spark.sql.SparkSession

/**
 * 封装的spark 工具将通用的代码放到这个工具中
 *
 * @author chengjin
 * @version 2022/1/12 11:15
 */

abstract class SparkTool extends Logging {

  var day_id: String = _
  var month_id: String = _

  def main(args: Array[String]): Unit = {
    if (args.length == 0) {
      log.error("请传入时间参数")
    }

    day_id = args(0)

    //截取月
    month_id = day_id.substring(0, 6)

    log.info(s"时间参数为: $day_id")

    log.info("创建spark环境")
    val spark: SparkSession = SparkSession
      .builder()
      .appName(this.getClass.getSimpleName.replace("$", ""))
      .enableHiveSupport()
      .getOrCreate()

    log.info("调用run方法")
    this.run(spark)
  }

  def run(spark: SparkSession)
}
