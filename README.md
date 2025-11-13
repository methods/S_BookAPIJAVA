# S_BookAPIJAVA

### Gradle Daemon: Stop the Daemon and Force a Clean Run

**Step 1: Forcefully stop all running Gradle daemons.** 
Forcefully stop all running Gradle daemons.
This command tells Gradle to find any background processes it has running and terminate them.
```Bash
./gradlew --stop
```

**Step 2: Run a clean build.**
The clean task deletes the entire build directory. This removes any old, compiled artifacts and cached results, ensuring nothing stale is left over. We will combine it with the checkstyleMain task.
```Bash
./gradlew clean checkstyleMain
```