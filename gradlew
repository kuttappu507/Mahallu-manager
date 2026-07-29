#!/usr/bin/env sh
# Gradle wrapper for Mahallu Manager
# Uses JAVA_HOME if set, otherwise falls back to common JDK paths
set -e
APP_HOME=`pwd -P`
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

# Pick a Java binary
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA="$JAVA_HOME/bin/java"
elif [ -x "/usr/lib/jvm/java-17-openjdk-amd64/bin/java" ]; then
    JAVA="/usr/lib/jvm/java-17-openjdk-amd64/bin/java"
elif [ -x "/opt/hostedtoolcache/Java_Temurin-Hotspot_jdk/17.0.19-10/x64/bin/java" ]; then
    JAVA="/opt/hostedtoolcache/Java_Temurin-Hotspot_jdk/17.0.19-10/x64/bin/java"
elif [ -x "/opt/java/openjdk/bin/java" ]; then
    JAVA="/opt/java/openjdk/bin/java"
elif command -v java >/dev/null 2>&1; then
    JAVA=`command -v java`
else
    echo "ERROR: Java not found. Install JDK 17 or set JAVA_HOME." >&2
    exit 1
fi

exec "$JAVA" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
