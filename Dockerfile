FROM amazoncorretto:17.0.4-al2 as base

LABEL maintainer="jtsay@cloudreactor.io"

RUN yum install -y shadow-utils wget

# Run as non-root user for better security
RUN groupadd appuser && useradd -g appuser --create-home appuser
USER appuser
RUN mkdir /home/appuser/app
WORKDIR /home/appuser/app

RUN wget -nv https://github.com/CloudReactor/cloudreactor-procwrapper/raw/5.0.2/bin/pyinstaller/al2/5.0.2/proc_wrapper.bin
RUN chmod +x ./proc_wrapper.bin

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
