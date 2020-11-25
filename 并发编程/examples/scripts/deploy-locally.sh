#!/usr/bin/env bash
#
# 更新 helm 部署
#
# Globals:
#  NAMESPACE
#  RELEASE
#  VALUES_FILE

cd "$(dirname "$0")"

set -e

CHART=`pwd`/chart

if [ ! -f "${VALUES_FILE}" ]; then
  echo "${VALUES_FILE} does not exists"
  exit 1
fi

echo "namespace=${NAMESPACE} release=${RELEASE} chart=${CHART} values=${VALUES_FILE}"
read -p "deploy now? (y/N) " yn
case $yn in
  [Yy]* ) helm -n ${NAMESPACE} upgrade ${RELEASE} ${CHART} -f ${VALUES_FILE} ;;
  * ) exit ;;
esac
