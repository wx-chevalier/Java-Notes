#!/bin/bash
#
# 构建项目 helm chart
#
# gitlab CI 变量
#   https://docs.gitlab.com/ee/ci/variables/predefined_variables.html
#
# Globals:
#   CI_COMMIT_TAG: 当前提交的 tag
#   CI_COMMIT_REF_NAME: 当前提交的 ref name (branch/tag)
#   CHARTMUSEUM_URL: chartmuseum 地址

# 切入项目根目录
cd `dirname $0`/..

CHART=`pwd`/scripts/chart
CHART_VERSION=`cat ${CHART}/Chart.yaml | grep '^version: ' | cut -d' ' -f2`

err() {
  echo "[$(date +'%Y-%m-%dT%H:%M:%S%z')]: $@" >&2
}

#######################################
# 尝试安装 curl，安装失败退出脚本
# Returns:
#   0 安装成功，非 0 安装失败
#######################################
install_curl_or_exit() {
  if which curl; then return 0; fi

  if which apk; then apk add curl; fi
  if which curl; then return 0; fi

  if which apt; then apt install --yes curl; fi
  if which curl; then return 0; fi

  err "curl not found"
  return 1
}

#######################################
# 构建并推送 helm chart 到 chartmuseum
# 如果存在 push 插件，使用 push 插件推送
#   https://github.com/chartmuseum/helm-push
# 使用 curl 进行推送（这里假定了是在 alpine 中执行，使用 apk 安装 curl）
#   https://chartmuseum.com/docs/
#
# Globals:
#   CHART
#   CHARTMUSEUM_URL
# Returns:
#   0 推送成功，非 0 推送失败
#######################################
build_chart_and_push() {
  if helm plugin ls | grep push;
  then
    helm repo add ufc $CHARTMUSEUM_URL
    helm push $CHART ufc --force
    return $?
  fi

  if ! install_curl_or_exit; then return $?; fi


  echo "[1/2] helm package $CHART"

  if ! (rm -rf out/chart \
          && mkdir -p out/chart \
          && helm package $CHART -d out/chart)
  then
    res=$?
    err "Error packaging $CHART"
    echo $res
  fi


  local chart_package="out/chart/$(ls out/chart)"
  echo "[2/2] push package ${chart_package}"

  local res=$(curl --data-binary "@${chart_package}" \
                   "${CHARTMUSEUM_URL}/api/charts?force=true")
  if echo $res | grep '"saved":true';
  then
    return $?
  else
    res=$?
    err "failed pushing package"
    return $res
  fi
}

#######################################
# 使用 build_chart_and_push 构建并推送 tag 了的 commit 的 helm chart
#   - 保证 commit tag 和 chart 版本一致
# Globals:
#   CI_COMMIT_TAG
#   CHART_VERSION
# Returns:
#   0 成功，非 0 失败
#######################################
build_tag() {
  if [[ ${CI_COMMIT_TAG} != ${CHART_VERSION} ]];
  then
    err "chart version ${CHART_VERSION} != commit tag ${CI_COMMIT_TAG}"
    return 1
  fi

  build_chart_and_push
  return $?
}

#######################################
# 在 dev 分支使用 build_chart_and_push 构建推送 helm chart
#   - 确保 chart 版本中包含 "dev"
# Globals:
#   CHART
#   CHART_VERSION
# Returns:
#   0 成功，非 0 失败
#######################################
build_dev() {
  if echo ${CHART_VERSION} | grep dev;
  then
    build_chart_and_push
    return $?
  else
    err "Not pushing(dev): chart version ${CHART_VERSION} does not contain dev"
    return 1
  fi
}


case ${CI_COMMIT_REF_NAME} in
  dev)
    echo "Build chart on dev branch"
    build_dev
    ;;
  master)
    echo "Skip chart building on master branch"
    ;;
  *)
    if [[ ! -z "${CI_COMMIT_TAG}" ]];
    then
      build_tag
    else
      echo "Skip building on ref ${CI_COMMIT_REF_NAME}"
    fi
    ;;
esac
