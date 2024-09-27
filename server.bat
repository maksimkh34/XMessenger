git pull
cd Server\src
setlocal enabledelayedexpansion
set "libs="
for %%f in (..\..\JavaPlugins\*) do (
    set "libs=!libs!;%%f"
)
javac -cp .!libs! -d ..\bin Main.java
cd ..\bin
set "libs="
for %%f in (..\JavaPlugins\*) do (
    set "libs=!libs!;%%f"
)
java -cp .!libs! Main
