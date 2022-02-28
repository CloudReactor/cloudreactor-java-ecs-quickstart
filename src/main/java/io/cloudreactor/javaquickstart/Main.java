package io.cloudreactor.javaquickstart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cloudreactor.tasksymphony.wrapperio.TaskStatusUpdater;

/** The main class of this application, which demonstrates the use of the
 *  task status updater.
 */
public final class Main {
  private Main() { }

  /** Entrypoint.
   *
   * @param args An array of command-line arguments.
   */
  public static void main(final String[] args) {
    var taskName = "main";

    if (args.length > 0) {
      taskName = args[0];
    }

    switch (taskName) {
      case "main" -> runMainTask();
      case "add" -> runAdderTask();
      case "readsecret" -> readSecret();
      default -> throw new RuntimeException("Unknown task name: " + taskName);
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

      LOGGER.info("Hello!");

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

  static int add(final int a, final int b) {
    return a + b;
  }

  static void readSecret() {
    try (TaskStatusUpdater statusUpdater = new TaskStatusUpdater()) {
      var secret = System.getenv("SECRET_VALUE");
      var message = "Secret is " + secret;

      System.out.println(message);
      statusUpdater.sendUpdateAndIgnoreError(
        null, // successCount
        null, // errorCount
        null, // skippedCount
        null, // expectedCount
        message, // lastStatusMessage
        null  // extraProps
      );
    }
  }

  /** Logger for this class. */
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
}
