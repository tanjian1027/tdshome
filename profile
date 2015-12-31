echo '棠棣科技'

export tdshome=~/tdshome
export TWORKDIR=~/tdshome
PATH=$PATH:$HOME/bin:$TWORKDIR/bin
export PATH
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar

alias rm='echo 禁止使用删除功能!'
alias l='ls -alrt'
alias ..='cd ..'
set -o vi
export PS1="[TD]"'$LOGNAME:''$PWD>'

echo '    +-----------------------------------------------------------+'
echo '    +-----  cdae/cdat/cdal  进入应用的etc/当日trc/log目录  -----+'
echo '    +-----                                                 -----+'
echo '    +-----  cde/cdt/cdl/cdw/cds <应用目录名> 进入对应的目录-----+'
echo '    +-----  cde    进入tds平台主目录                       -----+'
echo '    +-----  cd1    进入tds平台log主目录                    -----+'
echo '    +-----  cdt    进入tds平台trc主目录                    -----+'
echo '    +-----  cde + appName   进入tds平台appName的etc目录    -----+'
echo '    +-----  cd1 + appName   进入tds平台appName的log目录    -----+'
echo '    +-----  cdt + appName   进入tds平台appName的trc目录    -----+'
echo '    +-----  cdw + webAppName进入tomcat平台AppName的目录     -----+'
echo '    +-----------------------------------------------------------+'
echo '    '

echo -n '请输入你的应用名：'
read appName
ym=`date +%Y%m`
dt=`date +%d`
alias cdat='cd $TWORKDIR/trc/$appName/$ym/$dt'
alias cdal='cd $TWORKDIR/log/$appName/$ym/$dt'
alias cdae='cd $TWORKDIR/app/$appName/etc'
alias cdas='cd $TWORKDIR/app/$appName/sql'
alias cdaw='cd ~/apache-tomcat-7.0.64/webapps/$appName'

myprofile()
{
if [ "$2" == "" ]; then
cd "${TWORKDIR}"
fi
if [ "$2" != "" ]; then
cd "${TWORKDIR}/app/$2/$1"
fi
}

myprofile2()
{
if [ "$2" == "" ]; then
cd "${TWORKDIR}/$1"
fi 
if [ "$2" != "" ]; then
cd "${TWORKDIR}/$1/$2/$ym/$dt"
fi
}

myprofile3()
{
cd "${HOME}/apache-tomcat-7.0.64/webapps/$1"
}

alias cde='myprofile etc'
alias cds='myprofile sql'
alias cdt='myprofile2 trc'
alias cdl='myprofile2 log'
alias cdw='myprofile3'
