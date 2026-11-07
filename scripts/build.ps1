param([switch]$RequireImplementation)
$ErrorActionPreference = 'Stop'
Set-Location (Split-Path $PSScriptRoot -Parent)
$repositoryPath = Join-Path (Get-Location).Path '.m2'
$options = @('-Pbenchmark', "-Dmaven.repo.local=$repositoryPath")
if ($RequireImplementation) { $options += '-DrequireImplementation=true' }
& mvn @options clean package
if ($LASTEXITCODE -ne 0) { throw 'Build/test failed' }
$javaForBuild = if ($env:JAVA_HOME -and (Test-Path (Join-Path $env:JAVA_HOME 'bin\java.exe'))) {
    Join-Path $env:JAVA_HOME 'bin\java.exe'
} else {
    (Get-Command java -CommandType Application -ErrorAction Stop | Select-Object -First 1).Source
}
$benchmarkList = & $javaForBuild -jar target/benchmarks.jar -l
if ($LASTEXITCODE -ne 0 -or -not ($benchmarkList -match 'study.SortBenchmark.sort')) {
    throw 'Packaged JAR is missing the generated JMH benchmark. Check annotation processing.'
}
$benchmarkList | Write-Output
