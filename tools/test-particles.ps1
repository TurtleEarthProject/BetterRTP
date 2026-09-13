param(
    [Parameter(Mandatory = $true)][string]$ApiJar,
    [Parameter(Mandatory = $true)][string]$GuavaJar,
    [string]$JavaBin = '',
    [switch]$Legacy
)

$ErrorActionPreference = 'Stop'
$taskRoot = Split-Path -Parent $PSScriptRoot
$apiPath = (Resolve-Path -LiteralPath $ApiJar).Path
$guavaPath = (Resolve-Path -LiteralPath $GuavaJar).Path
$outputPath = Join-Path $taskRoot 'target/compatibility-test'
$javacCommand = if ($JavaBin) { Join-Path $JavaBin 'javac.exe' } else { 'javac' }
$javaCommand = if ($JavaBin) { Join-Path $JavaBin 'java.exe' } else { 'java' }
$sources = @(
    (Join-Path $taskRoot 'src/main/java/me/SuperRonanCraft/BetterRTP/versions/BukkitParticles.java'),
    (Join-Path $taskRoot 'src/test/java/me/SuperRonanCraft/BetterRTP/versions/BukkitParticlesCompatibilityTest.java')
)
& $javacCommand --release 8 -cp $apiPath -d $outputPath @sources
if ($LASTEXITCODE -ne 0) { throw 'Compatibility test compilation failed' }
$testArgs = if ($Legacy) { @('legacy') } else { @() }
& $javaCommand -cp "$outputPath;$apiPath;$guavaPath" me.SuperRonanCraft.BetterRTP.versions.BukkitParticlesCompatibilityTest @testArgs
if ($LASTEXITCODE -ne 0) { throw 'Compatibility test failed' }
