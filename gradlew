#!/usr/bin/env sh
APP_HOME=`pwd -P`
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar
exec /usr/lib/jvm/java-17-openjdk-amd64/bin/java -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
