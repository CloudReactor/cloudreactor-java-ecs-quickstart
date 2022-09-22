FROM debian:buster-slim as base

# The lines below are copied from the Dockerfile in the official
# Amazon Corretto Dockerfile repository:
# https://github.com/corretto/corretto-docker/blob/main/17/slim/debian/Dockerfile
ARG version=17.0.4.9-1

# In addition to installing the Amazon corretto, we also install
# fontconfig. The folks who manage the docker hub's
# official image library have found that font management
# is a common usecase, and painpoint, and have
# recommended that Java images include font support.
#
# See:
#  https://github.com/docker-library/official-images/blob/master/test/tests/java-uimanager-font/container.java
#
# Slim:
#   JLink is used (retaining all modules) to create a slimmer version of the JDK excluding man-pages, header files and debugging symbols - saving ~113MB.
RUN set -ux \
    && apt-get update \
    && apt-get install -y --no-install-recommends \
        curl ca-certificates gnupg software-properties-common fontconfig \
    && curl -fL https://apt.corretto.aws/corretto.key | apt-key add - \
    && add-apt-repository 'deb https://apt.corretto.aws stable main' \
    && mkdir -p /usr/share/man/man1 \
    && apt-get update \
    && apt-get install -y java-17-amazon-corretto-jdk=1:$version binutils \
    && jlink --add-modules "$(java --list-modules | sed -e 's/@[0-9].*$/,/' | tr -d \\n)" --no-man-pages --no-header-files --strip-debug --output /opt/corretto-slim \
    && apt-get purge -y --auto-remove -o APT::AutoRemove::RecommendsImportant=false \
            curl gnupg software-properties-common binutils java-17-amazon-corretto-jdk=1:$version \
    && mkdir -p /usr/lib/jvm \
    && mv /opt/corretto-slim /usr/lib/jvm/java-17-amazon-corretto \
    && jdk_tools="java keytool rmid rmiregistry javac jaotc jlink jmod jhsdb jar jarsigner javadoc javap jcmd jconsole jdb jdeps jdeprscan jimage jinfo jmap jps jrunscript jshell jstack jstat jstatd serialver" \
    && priority=$(echo "1${version}" | sed "s/\(\.\|-\)//g") \
    && for i in ${jdk_tools}; do \
          update-alternatives --install /usr/bin/$i $i /usr/lib/jvm/java-17-amazon-corretto/bin/$i ${priority}; \
       done

ENV LANG C.UTF-8
ENV JAVA_HOME=/usr/lib/jvm/java-17-amazon-corretto
# End lines copies from https://github.com/corretto/corretto-docker/blob/main/17/slim/debian/Dockerfile

LABEL maintainer="jtsay@cloudreactor.io"

RUN set -ux \
  && apt-get update \
  && apt-get upgrade -y \
  && apt-get install -y --no-install-recommends \
  openssl \
  libexpat1 \
  ca-certificates \
  wget \
  && apt-get clean && rm -rf /var/lib/apt/lists/*

# Run as non-root user for better security
RUN groupadd appuser && useradd -g appuser --create-home appuser
USER appuser
RUN mkdir /home/appuser/app
WORKDIR /home/appuser/app

# Use the standalone executable for x64/AMD64 Linux.
RUN wget -nv https://github.com/CloudReactor/cloudreactor-procwrapper/raw/5.0.1/bin/nuitka/linux-amd64/5.0.1/proc_wrapper.bin
RUN chmod +x proc_wrapper.bin

FROM base AS builder

COPY gradlew .
COPY gradle ./gradle

# Cache the download of the wrapper
RUN ./gradlew --version

COPY build.gradle.kts .

# Only download dependencies
# Eat the expected build failure since no source code has been copied yet
RUN ./gradlew clean build --no-daemon || true

COPY src ./src

RUN ./gradlew shadowJar --no-daemon

FROM base AS release

COPY --from=builder /home/appuser/app/build/libs/app.jar .

ENTRYPOINT ["./proc_wrapper.bin"]

FROM builder AS development
ENTRYPOINT ["bash"]

FROM release
