# startTDS.sh:  Date: 2012.7.31
./stopTDS.sh

echo  "    +-----------------------------------------------------------------+"
echo  "    +-                                                               -+"
echo  "    +-    Company:  Shanghai Tangdi Information Technology Co.,Ltd   -+"
echo  "    +-    DepartMent: The third party payment business department    -+"
echo  "    +-                                                               -+"
echo  "    +-    Function: Startup TDS Service                              -+"
echo  "    +-                                                               -+"
echo  "    +-----------------------------------------------------------------+"
echo  "    "
echo  "    "
echo  "     Function: Startup TDS Service"
echo  '    '
echo  "     begin startup TDS Service ..."

# Set Lang
export LANG=en_US.UTF-8
echo "     Set System LANG=$LANG is success"

#nohup /usr/java/jdk1.6.0_43/bin/java -jar -Xms256m -Xmx1024m -Dcom.sun.management.jmxremote.port=8187 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false  start.jar & 
nohup /usr/java/jdk1.6.0_43/bin/java -jar -Xms256m -Xmx1024m ../tdshome/start.jar >> nohup.out &
pid=`ps -fe | grep java | grep "tdshome/start.jar" | grep -v oracle |grep -v grep|grep -v resin |grep -v hudson|grep -v tomcat|awk '{print $2}'`
if [ "$pid" != "" ]; then
  echo "     Startup TDS Service succss, process ID=[$pid]."
  echo "     TDS Log is appending to ~/tdshome/nohup.out."
else
  echo "     Startup TDS service Failure."
  echo "     TDS Service is not running. "
fi

echo '    '
