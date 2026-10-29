#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd -- "$SCRIPT_DIR/.." && pwd)"
LOG_FILE="$PROJECT_DIR/logs/Alzhan_Temirlan_system_report.log"

mkdir -p "$PROJECT_DIR/logs"

{
    printf 'Student: Temirlan Alzhan\n'
    printf 'Group: IT2-2307\n'
    printf 'Student ID: 38372\n'
    printf 'Date and time: '
    date --iso-8601=seconds
    printf 'Username: '
    whoami
    printf '\nOperating system\n'
    cat /etc/os-release
    printf '\nKernel\n'
    uname -r
    printf '\nCPU\n'
    lscpu
    printf '\nMemory\n'
    free -h
    printf '\nDisk usage and available space\n'
    df -h
    printf '\nReport completed for Temirlan Alzhan\n'
} | tee "$LOG_FILE"
