package ads

import com.shujia.common.SparkTool
import org.apache.spark.sql._

object ADSCityWideMskDay extends SparkTool {
  override def run(spark: SparkSession): Unit = {
    import spark.implicits._
    import org.apache.spark.sql.functions._

    /**
     * 1.读取数据
     *
     */

    // 市游客表
    val cityTourist: Dataset[Row] = spark
      .table("ads.ads_city_tourist_msk_d")
      .where($"day_id" === day_id)

    // 用户画像表
    val userTag: Dataset[Row] = spark
      .table("dim.dim_usertag_msk_m")
      .where($"month_id" === month_id)

    // 行政区配置表
    val adminCode: DataFrame = spark
      .table("dim.dim_admincode")

    // 在行政区配置表中每一个城市有多条数据，需要去重
    val cityIdAndName: Dataset[Row] = adminCode
      .select($"city_id" as "d_city_id", $"city_name" as "d_city_name")
      .distinct()


    //停留时间分段规则
    val d_stay_time_sections: Column =
      when($"d_stay_time" >= 3 and $"d_stay_time" < 6, "[3-6)")
        .when($"d_stay_time" >= 6 and $"d_stay_time" < 9, "[6-9)")
        .when($"d_stay_time" >= 9 and $"d_stay_time" < 12, "[9-12)")
        .when($"d_stay_time" >= 12 and $"d_stay_time" < 15, "[12-15)")
        .when($"d_stay_time" >= 15 and $"d_stay_time" < 18, "[15-18)")
        .otherwise("[18-)")

    //出游距离分段规则
    val d_distance_sections: Column =
      when($"d_max_distance" >= 10 and $"d_max_distance" < 50, "[10-50)")
        .when($"d_max_distance" >= 50 and $"d_max_distance" < 80, "[50-80)")
        .when($"d_max_distance" >= 80 and $"d_max_distance" < 120, "[80-120)")
        .when($"d_max_distance" >= 120 and $"d_max_distance" < 200, "[120-200)")
        .when($"d_max_distance" >= 200 and $"d_max_distance" < 400, "[200-400)")
        .when($"d_max_distance" >= 400 and $"d_max_distance" < 800, "[400-800)")
        .otherwise("[800-)")

    //年龄分段规则
    val age_sections: Column =
      when($"age" >= 0 and $"age" < 20, "[0-20)")
      .when($"age" >= 20 and $"age" < 25, "[20-25)")
      .when($"age" >= 25 and $"age" < 30, "[25-30)")
      .when($"age" >= 30 and $"age" < 35, "[30-35)")
      .when($"age" >= 35 and $"age" < 40, "[35-40)")
      .when($"age" >= 40 and $"age" < 45, "[40-45)")
      .when($"age" >= 45 and $"age" < 50, "[45-50)")
      .when($"age" >= 50 and $"age" < 55, "[50-55)")
      .when($"age" >= 55 and $"age" < 60, "[55-60)")
      .when($"age" >= 60 and $"age" < 65, "[60-65)")
      .otherwise("[65-)")


    /**
     * 2.关联表
     */
    val cituWide: DataFrame = cityTourist
      //关联行政区配置表获取游客目的地市名
      .join(cityIdAndName.hint("broadcast"), "d_city_id")
      //关联行政区配置表获取游客来源地城市名和省名
      .join(adminCode.hint("broadcast"), $"source_county_id" === $"county_id")
      //整理数据
      .select($"mdn", $"d_city_name", $"city_name" as "o_city_name", $"prov_name" as "o_province_name", $"d_stay_time", $"d_max_distance")
      //停留时间分段
      .withColumn("d_stay_time_section", d_stay_time_sections)
      //出游距离分段
      .withColumn("d_distance_section", d_distance_sections)
      //关联用户画像表
      .join(userTag.hint("broadcast"), "mdn")
      //年龄分段
      .withColumn("age_section", age_sections)
      .select(
        $"mdn",
        $"d_city_name",
        $"o_city_name",
        $"o_province_name",
        $"d_stay_time",
        $"d_max_distance",
        $"d_stay_time_section",
        $"d_distance_section",
        $"age",
        $"age_section",
        $"gender",
        $"number_attr",
        $"trmnl_brand",
        $"packg",
        $"conpot"
      )


    //保存数据
    cituWide.write
      .format("csv")
      .option("sep", "\t")
      .mode(SaveMode.Overwrite)
      .save(s"/daas/motl/ads/ads_city_wide_msk_d/day_id=$day_id")


    spark.sql(s"alter table ads.ads_city_wide_msk_d add if not exists partition(day_id='$day_id') ")

  }
}

