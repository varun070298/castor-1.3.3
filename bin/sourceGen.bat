@echo off
REM $Id: sourceGen.bat 6277 2006-10-05 19:59:05Z ekuns $
set JAVA=%JAVA_HOME%\bin\java
set cp=%CLASSPATH%
for %%i in (lib\*.jar) do call cp.bat %%i
set cp=%cp%;.

%JAVA% -classpath %CP% org.exolab.castor.builder.SourceGeneratorMain %*
