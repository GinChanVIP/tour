#!/usr/bin/env bash
#***********************************************************************************
# **  文件名称: start-dws-staypoint-msk-d.sh
# **  创建日期: 2021年7月29日
# **  编写人员: qinxiao
# **  输入信息: 位置融合表
# **  输出信息: 停留表
# **
# **  功能描述: 计算停留表
# **  处理过程:
# **  Copyright(c) 2016 TianYi Cloud Technologies (China), Inc.
# **  All Rights Reserved.
#***********************************************************************************

#***********************************************************************************
#==修改日期==|===修改人=====|======================================================|
#
#***********************************************************************************


day_id=$1


spark-submit \
--master yarn-client \
--class com.shujia.dws.DWSStayPointMskDay \
--num-executors 1 \
--executor-cores 1 \
--executor-memory 2G \
--jars common-1.0-SNAPSHOT.jar \
--conf spark.sql.shuffle.partitions=20 \
dws-1.0-SNAPSHOT.jar $day_id




