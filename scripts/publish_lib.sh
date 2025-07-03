#!/usr/bin/env bash

echo "nexusUrl=https://nexus.internal.blockchain.info" >> gradle.properties
echo "nexusUsername=github-actions" >> gradle.properties
echo "nexusPassword=$NEXUS_PASSWORD" >> gradle.properties
echo "gradleCachePassword=" >> gradle.properties

chown -R blockchain:blockchain $PWD
su blockchain -c "./gradlew publish --no-daemon -Pversion=$VERSION" || true