#!/usr/bin/env bash
#***********************************************************************************
# **  文件名称:start-ads-city-tourist-msk-d.sh
# **  创建日期: 2022年1月14日
# **  编写人员: GinChan
# **  输入信息: 行政区配置表、游客表、用户画像表
# **  输出信息: 宽表
# **
# **  功能描述: 宽表
# **  处理过程:
# **  Copyright(c) 2016 TianYi Cloud Technologies (China), Inc.
# **  All Rights Reserved.
#***********************************************************************************

#***********************************************************************************
#==修改日期==|===修改人=====|======================================================|
#
#***********************************************************************************

#获取脚本所在目录
shell_home="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

#进入脚本目录
cd $shell_home


day_id=$1


spark-submit \
--master yarn-client \
--class ads.ADSCityWideMskDay \
--num-executors 1 \
--executor-cores 1 \
--executor-memory 2G \
--conf spark.sql.shuffle.partitions=10 \
--jars common-1.0-SNAPSHOT.jar \
ads-1.0-SNAPSHOT.jar $day_id




