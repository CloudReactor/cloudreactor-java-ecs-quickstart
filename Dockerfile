FROM openjdk:17.0.2-slim-bullseye AS base

LABEL maintainer="jtsay@cloudreactor.io"

RUN apt-get update \
  && apt-get install -y --no-install-recommends python3=3.9.2-3 python3-pip=20.3.4-4 \
  && apt-get clean && rm -rf /var/lib/apt/lists/*

# Run as non-root user for better security
RUN groupadd appuser && useradd -g appuser --create-home appuser
USER appuser
RUN mkdir /home/appuser/app
WORKDIR /home/appuser/app

# Output directly to the terminal to prevent logs from being lost
# https://stackoverflow.com/questions/59812009/what-is-the-use-of-pythonunbuffered-in-docker-file
ENV PYTHONUNBUFFERED 1

# Don't write *.pyc files
ENV PYTHONDONTWRITEBYTECODE 1
#
# Enable the fault handler for segfaults
# https://docs.python.org/3/library/faulthandler.html
ENV PYTHONFAULTHANDLER 1

ENV PYTHONPATH /home/appuser/app

COPY deploy_config/files/proc_wrapper-requirements.txt .
RUN pip3 install --no-input --no-cache-dir -r proc_wrapper-requirements.txt
RUN pip3 install --no-input --no-cache-dir cloudreactor-procwrapper==3.1.2

FROM base AS builder

COPY gradlew .
COPY gradle ./gradle

# Cache the download of the wrapper
RUN ./gradlew --version

#COPY gradle.properties .
COPY build.gradle.kts .

# Only download dependencies
# Eat the expected build failure since no source code has been copied yet
RUN ./gradlew clean build --no-daemon || true

COPY src ./src

RUN ./gradlew shadowJar --no-daemon

FROM base AS release

COPY --from=builder /home/appuser/app/build/libs/app.jar .

CMD python3 -m proc_wrapper java -jar app.jar $TASK_COMMAND

FROM builder AS development
ENTRYPOINT ["bash"]

FROM release
