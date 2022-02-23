package io.cloudreactor.javaquickstart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cloudreactor.tasksymphony.wrapperio.TaskStatusUpdater;

public class Main {
  public static void main(final String[] args) {
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

     var result = add(5, 10);

      statusUpdater.sendUpdateAndIgnoreError(
          null, // successCount
          null, // errorCount
          null, // skippedCount
          null, // expectedCount
          "Finished Java QuickStart app, result = " + result + "!", // lastStatusMessage
          null  // extraProps
      );
    }
  }

  static int add(int a, int b) {
    return a + b;
  }

  private static final Logger logger = LoggerFactory.getLogger(Main.class);
}
