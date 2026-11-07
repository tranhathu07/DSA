param([int]$Warmup = 20, [int]$Measurements = 10, [int]$Forks = 3)
$ErrorActionPreference = 'Stop'
Set-Location (Split-Path $PSScriptRoot -Parent)
& "$PSScriptRoot/build.ps1" -RequireImplementation
if (!(Test-Path data/manifest.csv)) { throw 'Generate datasets first; see README.' }
$run = 'results/pilot-' + (Get-Date -Format 'yyyyMMdd-HHmmss')
New-Item -ItemType Directory -Path $run | Out-Null
# Windows PowerShell 5.1 treats redirected native stderr as ErrorRecords.
# Java writes its version to stderr even when it exits successfully.
$java = $null
if ($env:JAVA_HOME) {
    $javaHomeExecutable = Join-Path $env:JAVA_HOME 'bin\java.exe'
    if (Test-Path -LiteralPath $javaHomeExecutable -PathType Leaf) {
        $java = $javaHomeExecutable
    }
}
if (-not $java) {
    $java = (Get-Command java -CommandType Application -ErrorAction Stop | Select-Object -First 1).Source
}
$versionProcess = Start-Process -FilePath $java -ArgumentList '-version' -Wait -PassThru -WindowStyle Hidden -RedirectStandardError "$run/environment.txt" -RedirectStandardOutput "$run/version-stdout.txt"
if ($versionProcess.ExitCode -ne 0) { throw "Java version check failed: $($versionProcess.ExitCode)" }
$previousErrorAction = $ErrorActionPreference
try {
    # Preserve native diagnostics in the log; exit code determines success.
    $ErrorActionPreference = 'Continue'
    & $java -jar target/benchmarks.jar study.SortBenchmark -p 'dataset=random_10000_1,duplicates_10000_1,random_1000000_1,duplicates_1000000_1' -p 'cutoff=0,7,10,32,64' -wi $Warmup -i $Measurements -f $Forks -foe true -rf json -rff "$run/jmh.json" 2>&1 | Tee-Object "$run/console.log"
    $benchmarkExitCode = $LASTEXITCODE
} finally {
    $ErrorActionPreference = $previousErrorAction
}
if ($benchmarkExitCode -ne 0) { throw "Pilot failed (exit code $benchmarkExitCode); see $run/console.log" }
