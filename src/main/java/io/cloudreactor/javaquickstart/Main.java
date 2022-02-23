package io.cloudreactor.javaquickstart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cloudreactor.tasksymphony.wrapperio.TaskStatusUpdater;

public class Main {
  public static void main(final String[] args) {
    var taskName = "main";

    if (args.length > 0) {
      taskName = args[0];
    }

    switch (taskName) {
      case "main" -> runMainTask();
      case "add" -> runAdderTask();
    }
  }

  static void runMainTask() {
    try (TaskStatusUpdater statusUpdater = new TaskStatusUpdater()) {
      statusUpdater.sendUpdateAndIgnoreError(
          0L,   // successCount
          null, // errorCount
          null, // skippedCount
          10L,  // expectedCount
          "Starting Java QuickStart app ...", // lastStatusMessage
          null  // extraProps
      );

      logger.info("Hello!");

      for (long i = 0; i < 10; i++) {
        statusUpdater.sendUpdateAndIgnoreError(
            i,    // successCount
            null, // errorCount
            null, // skippedCount
            null, // expectedCount
            "Updating row " + i + " ...", // lastStatusMessage
            null  // extraProps
        );
      }

      statusUpdater.sendUpdateAndIgnoreError(
          null, // successCount
          null, // errorCount
          null, // skippedCount
          null, // expectedCount
          "Finished Java QuickStart app!", // lastStatusMessage
          null  // extraProps
      );
    }
  }

  static void runAdderTask() {
    var result = add(5, 10);

    try (TaskStatusUpdater statusUpdater = new TaskStatusUpdater()) {
      statusUpdater.sendUpdateAndIgnoreError(
          null, // successCount
          null, // errorCount
          null, // skippedCount
          null, // expectedCount
          "Result = " + result, // lastStatusMessage
          null  // extraProps
      );
    }
  }

  static int add(int a, int b) {
    return a + b;
  }

  private static final Logger logger = LoggerFactory.getLogger(Main.class);
}
