#!/usr/bin/env bash
#
# 本地构建并推送项目镜像
#
# Globals:
#  DISABLE_SPOTBUGS
#  DISABLE_TEST
#  DOCKER_REGISTRY_SERVER
#  TAG

set -e

# 切入项目根目录
cd "$(dirname "$0")/../.."

DOCKERFILE=scripts/docker/Dockerfile-local

DOCKER_REGISTRY_SERVER=${DOCKER_REGISTRY_SERVER:=registry.biz.com}
IMAGE=${DOCKER_REGISTRY_SERVER}/ufc/ufc-api

TAG=${TAG:=latest}


echo "[1/3] Build project locally ${IMAGE}:${TAG}"

[[ ${DISABLE_SPOTBUGS} != 'true' ]] \
  && echo "- spotbugs" && ./gradlew spotbugsMain spotbugsTest
[[ ${DISABLE_TEST} != 'true' ]] \
  && echo "- testing" && ./gradlew test

echo "- building"
./gradlew :ufc:ufc-api:build -x test


echo "[2/3] Finished building ${TAG}"
docker build --tag $IMAGE:$TAG -f $DOCKERFILE .


echo "[3/3] Pushing $IMAGE:$TAG"
docker push $IMAGE:$TAG
