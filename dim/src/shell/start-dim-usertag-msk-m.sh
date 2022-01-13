#!/usr/bin/env bash
#***********************************************************************************
# **  文件名称:start-dim-usertag-msk-m.sh
# **  创建日期: 2021年7月29日
# **  编写人员: qinxiao
# **  输入信息: 用户画像表
# **  输出信息: 用户画像表
# **
# **  功能描述: 用户画像表
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
--class com.shujia.dim.DIMUsertagMskMonth \
--num-executors 1 \
--executor-cores 2 \
--executor-memory 4G \
--jars common-1.0-SNAPSHOT.jar \
dim-1.0-SNAPSHOT.jar $day_id




