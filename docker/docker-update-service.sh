#!/bin/bash
set -e

source compose-utils.sh

ADDITIONAL_COMPOSE_QUEUE_ARGS=$(additionalComposeQueueArgs) || exit $?

ADDITIONAL_COMPOSE_ARGS=$(additionalComposeArgs) || exit $?

ADDITIONAL_CACHE_ARGS=$(additionalComposeCacheArgs) || exit $?

docker-compose \
  -f docker-compose.yml $ADDITIONAL_CACHE_ARGS $ADDITIONAL_COMPOSE_ARGS $ADDITIONAL_COMPOSE_QUEUE_ARGS \
  pull $@
docker-compose \
  -f docker-compose.yml $ADDITIONAL_CACHE_ARGS $ADDITIONAL_COMPOSE_ARGS $ADDITIONAL_COMPOSE_QUEUE_ARGS \
  up -d --no-deps --build $@
