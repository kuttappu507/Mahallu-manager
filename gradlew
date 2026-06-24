#!/bin/sh

#
# Copyright 2015 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

##############################################################################
##
##  Gradle start up script for UN*X
##
##############################################################################

# Attempt to set APP_HOME
# Resolve links: $0 may be a symlink
app_path="$0"

# Need this for daisy-chained symlinks.
while
    APP_HOME="${app_path%"${app_path##*/}"}" # leaves a trailing /; empty if no leading path
    [ -h "$app_path" ]
do
    ls=$( ls -ld "$app_path" )
    link=${ls#*' -> '}
    case $link in             #(
      /*) app_path=$link ;; #(
      *) app_path=$APP_HOME$link ;;
    esac
done

APP_HOME=$( cd "${APP_HOME%.}" && pwd -P ) || exit

APP_NAME="Gradle"
APP_BASE_NAME=${0##*/}
export APP_BASE_NAME
APP_HOME_FIRST_ARG="$APP_HOME"
expressed="${1:--}"

# Add a user-defined property if it exists, similar to JAVA_OPTS
if [ -z "$GRADLE_OPTS" ] ; then
    GRADLE_OPTS=''
fi

export GRADLE_OPTS

# Use the maximum available, or set MAX_FD != maximum.
MAX_FD=maximum

warn () {
    echo "$*" >&2
}

die () {
    echo
    echo "$*"
    echo
    exit 1
}

# OS specific support (must be 'true' or 'false').
darwin=false
msys=false
cygwin=false
nongnu=false
case "$( uname )" in                #(
  Darwin* ) darwin=true ;; #(
  MINGW* ) msys=true ;; #(
  CYGWIN* ) cygwin=true ;; #(
  NONGNU* ) nongnu=true ;; #(
esac

JAVA_HOME="${JAVA_HOME:-.}"
if [ ! -x "$JAVA_HOME/bin/java" ] && [ ! -x "$JAVA_HOME/bin/java.exe" ] ; then
    warn "JAVA_HOME is not defined correctly" >&2
    warn "We cannot execute $JAVA_HOME/bin/java."
    warn ""
    die "JAVA_HOME is set to an invalid directory: $JAVA_HOME"
fi

# Increase the maximum file descriptors if we can.
if ! "$cygwin" && ! "$darwin" && ! "$msys" && ! "$nongnu" ; then
    case $( ulimit -S -n ) in   #(
      ''|unlimited) ;; #(
      *) ulimit -S -n 262144 ;;
    esac
fi

# Escape application args
save () {
    for i do echo "$i" | sed "s/'/'\\\\''/g;1s/^/'/;\$s/\$/'/ " ; done
    echo " "
}
APP_ARGS=$( save "$@" )

# Collect all arguments for the java command;
#     * $DEFAULT_JVM_OPTS, $JAVA_OPTS, and $GRADLE_OPTS can contain fragments of
#       shell commands and variable substitutions, so put them in double quotes to make
#       sure that they get re-expanded; and
#     * put everything else in single quotes, so that it's not re-expanded.

set -- \
        "-Dorg.gradle.appname=$APP_BASE_NAME" \
        -classpath "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" \
        org.gradle.wrapper.GradleWrapperMain \
        "$@"

exec "$JAVA_HOME/bin/java" "$@"
