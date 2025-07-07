#!/usr/bin/env bash
BUILD_VERSION=0.5.8
if [ "$GITHUB_REF" = "refs/heads/master" ]
  then
    LAST_VERSION=$(git tag --sort=-v:refname --merged | grep -E '[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+' | head -n1)
    echo "$LAST_VERSION" | grep -Ev '^$' > /dev/null || exit 1
    if [[ "$LAST_VERSION" == ${BUILD_VERSION}.* ]]
      then BUILD_NUM=$(( 1 + $( cut -d. -f4 <<< "${LAST_VERSION}") ))
      else BUILD_NUM=0
    fi
    echo "${BUILD_VERSION}.${BUILD_NUM}"
  else
    GIT_COMMIT=$( git rev-parse --short HEAD )
    echo "$GITHUB_HEAD_REF"-"$GIT_COMMIT"
fi