#!/bin/sh

# $Id: test.sh 7523 2008-04-11 22:05:35Z rjoachim $

if [ -z "$JAVA_HOME" ] ; then
  JAVA=`which java`
  if [ -z "$JAVA" ] ; then
    echo "Cannot find JAVA. Please set your PATH."
    exit 1
  fi
  JAVA_BIN=`dirname $JAVA`
  JAVA_HOME=$JAVA_BIN/..
fi

JAVA=$JAVA_HOME/bin/java

DIRNAME=`dirname $0`
CASTOR_HOME=`cd $DIRNAME/..; pwd`
BUILD_D=$CASTOR_HOME/build
LIB_D=$CASTOR_HOME/lib
CPACTF_BUILD_D=$CASTOR_HOME/cpactf/build

CLASSPATH=$CLASSPATH:$BUILD_D/tests:$BUILD_D/classes
CLASSPATH=$CLASSPATH:$CPACTF_BUILD_D/classes
CLASSPATH=$CLASSPATH:$CASTOR_HOME/cpa/build/classes
CLASSPATH=`echo $LIB_D/*.jar | tr ' ' ':'`:$CLASSPATH
CLASSPATH=`echo $LIB_D/tests/*.jar | tr ' ' ':'`:$CLASSPATH

# add something like the following to include
# database driver to your classpath
#
# CLASSPATH=`echo $CASTOR_HOME/../*.jar | tr ' ' ':'`:$CLASSPATH

#$JAVA -Xms256M -Xmx512M -cp $CLASSPATH MainApp -execute $1 $2 $3 $4 $5 $6
$JAVA -cp $CLASSPATH MainApp -execute $1 $2 $3 $4 $5 $6
