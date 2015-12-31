#!/bin/sh
# stopTDS.sh:  Date: 2012.7.31
echo  "    +-----------------------------------------------------------------+"
echo  "    +-                                                               -+"
echo  "    +-    Company:  Shanghai Tangdi Information Technology Co.,Ltd   -+"
echo  "    +-    DepartMent: The third party payment business department    -+"
echo  "    +-                                                               -+"
echo  "    +-    Function: Stop TDS Service                                 -+"
echo  "    +-                                                               -+"
echo  "    +-----------------------------------------------------------------+"
echo  "    "
echo  "    "
echo  "     begin stop TDS Service ..."

pid=`ps -fe | grep java | grep "tdshome/start.jar" | grep -v oracle |grep -v grep|grep -v resin |grep -v hudson|grep -v tomcat|awk '{print $2}'`
if [ "$pid" != "" ]; then
  echo "     shutdown TDS Service, process ID=[$pid]."
  kill -9 $pid
  if [ $? -eq 1 ]; then
    echo "     shutdown TDS service Failure."
  else
    echo "     stop TDS Service succss!"
    
  fi
else
  echo "     TDS Service is not running. "
fi

echo '    '
