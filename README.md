# S_BookAPIJAVA

# Code Quality & Static Analysis

This project follows HMCTS engineering standards for formatting, style, static analysis, and code coverage.
The following tools are configured and integrated with Gradle:

-   Checkstyle – HMCTS Java code conventions

-   SpotBugs – static analysis for potential defects

-   JaCoCo – code coverage reporting

-   EditorConfig – consistent whitespace and formatting rules


----------

## Checkstyle (HMCTS rules)

Checkstyle uses the HMCTS `checkstyle.xml`, enforcing naming, formatting, Javadoc, and structural rules.

### Run Checkstyle

`./gradlew checkstyleMain`
or
`./gradlew checkstyleTest`

### Run ALL Checkstyle tasks

`./gradlew checkstyle`

### Checkstyle reports

Reports are generated at:

`build/reports/checkstyle/`

----------

## SpotBugs (static analysis)

SpotBugs analyses compiled bytecode and flags potential null pointer issues, performance problems, and common Java defects.

### List SpotBugs tasks

`./gradlew tasks --all | grep spotbugs`

### Run SpotBugs on main code

`./gradlew spotbugsMain`

### Run SpotBugs on test code

`./gradlew spotbugsTest`

### Run all SpotBugs tasks

`./gradlew spotbugs`

### SpotBugs reports

`build/reports/spotbugs/`

----------

## JaCoCo (test coverage)

JaCoCo generates unit test and integration test coverage reports in XML and HTML.

### Run tests and generate JaCoCo reports

`./gradlew test jacocoTestReport`

### Coverage report location

`build/reports/jacoco/test/html/index.html`

----------

## EditorConfig (formatting and whitespace)

The project uses HMCTS `.editorconfig` rules, enforcing:

-   2-space indentation for most files

-   4-space indentation for `.java` files

-   `LF` line endings

-   UTF-8 charset

-   No trailing whitespace

-   A newline at the end of every file


Most IDEs (including IntelliJ) apply these rules automatically.

----------

## Running all verification tasks

To verify everything before committing:

`./gradlew clean build`

This runs:

-   Checkstyle

-   SpotBugs

-   Tests

-   JaCoCo

-   Compilation

-   Packaging

----------

### Gradle Daemon: Stop the Daemon and Force a Clean Run

**Step 1: Forcefully stop all running Gradle daemons.**
This command tells Gradle to find any background processes it has running and terminate them.
```Bash
./gradlew --stop
```

**Step 2: Run a clean build.**
The clean task deletes the entire build directory. This removes any old, compiled artifacts and cached results, ensuring nothing stale is left over. We will combine it with the checkstyleMain task.
```Bash
./gradlew clean [checkstyleMain]
```

----------

## IntelliJ Setup

### Enable Checkstyle in IntelliJ

1.  Install the **Checkstyle-IDEA** plugin

2.  Open IntelliJ settings:

    `Settings → Tools → Checkstyle`

3.  Add the configuration file:

    `config/checkstyle/checkstyle.xml`

4.  Set it as the default configuration

5.  (Optional) Enable “Scan before check-in”


----------

### Enable EditorConfig support

Verify the following setting is enabled:

`Settings → Editor → Code Style → Enable EditorConfig support`

----------

### Reformat code according to project rules

Use IntelliJ’s reformat command:

`Windows/Linux: Ctrl + Alt + L  macOS:         Cmd + Option + L`

----------

## Summary

This project aligns with HMCTS engineering standards:

-   HMCTS Checkstyle enforcement

-   SpotBugs static analysis

-   JaCoCo coverage reports

-   HMCTS EditorConfig formatting

-   Spotless removed (not used by HMCTS)
