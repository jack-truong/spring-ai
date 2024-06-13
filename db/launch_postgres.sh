#!/bin/bash

USER=$(whoami)
DB_IMAGE="chinook-postgres"
CONTAINER_NAME="postgres_ai_demo"

docker build . -t "${USER}"/"${DB_IMAGE}"

EXISTING_RUNNING_CONTAINER=$(docker ps -a --filter "name=${CONTAINER_NAME}" --format '{{.Names}}' --filter status=running)
EXISTING_EXITED_CONTAINER=$(docker ps -a --filter "name=${CONTAINER_NAME}" --format '{{.Names}}' --filter status=exited)

if [ -n "${EXISTING_RUNNING_CONTAINER}" ]
then
  echo "Container with name ${CONTAINER_NAME} is already running, skipping..."
else
  if [ -n "${EXISTING_EXITED_CONTAINER}" ]
  then
    docker rm "$(docker ps -a --filter "name=${CONTAINER_NAME}" --format '{{.ID}}')"
  fi
  echo "Launching container with name ${CONTAINER_NAME}"
  docker run -d \
    --name "${CONTAINER_NAME}" \
    -e POSTGRES_PASSWORD=machineai \
    -v ${CONTAINER_NAME}_volume:/var/lib/postgresql/data \
    -p 5432:5432 \
    "${USER}"/"${DB_IMAGE}":latest
fi
