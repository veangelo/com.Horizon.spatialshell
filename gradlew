#!/bin/sh

APP_HOME=$(cd "$(dirname "$0")" && pwd)

WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

if [ ! -f "$WRAPPER_JAR" ]; then
  echo "ERROR: Could not find $WRAPPER_JAR"
  echo "Please run: gradle wrapper"
  exit 1
fi

exec java -classpath "$WRAPPER_JAR" org.gradle.wrapper.GradleWrapperMain "$@"
