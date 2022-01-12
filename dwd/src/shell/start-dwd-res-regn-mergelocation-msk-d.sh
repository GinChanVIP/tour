#!/usr/bin/env bash
#***********************************************************************************
# **  文件名称:start-dwd-res-regn-mergelocation-msk-d.sh
# **  创建日期: 2021年7月29日
# **  编写人员: qinxiao
# **  输入信息: ddr，oidd,wcdr dpi
# **  输出信息: 位置融合表
# **
# **  功能描述: 计算位置融合表
# **  处理过程:
# **  Copyright(c) 2016 TianYi Cloud Technologies (China), Inc.
# **  All Rights Reserved.
#***********************************************************************************

#***********************************************************************************
#==修改日期==|===修改人=====|======================================================|
#
#***********************************************************************************

# 时间不能写死,请问shell里面怎么传参数
day_id=$1

spark-submit \
--master yarn-client \
--class com.shujia.dwd.DWDResReignMergeLocationMskDay \
--num-executors 1 \
--executor-cores 2 \
--executor-memory 4G \
dwd-1.0-SNAPSHOT.jar $day_id