#!/bin/bash

set -e

# 切入项目根目录
cd "$(dirname "$0")/.."

cp -r ./build.gradle ./.boilerplate
cp -r ./gradle.properties ./.boilerplate
cp -r ./scripts ./.boilerplate