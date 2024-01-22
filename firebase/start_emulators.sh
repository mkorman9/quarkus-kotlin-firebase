#!/usr/bin/env bash

PROJECT_NAME="emulator-project"

cat > ".firebaserc" <<- EOF
{
  "projects": {
    "default": "${PROJECT_NAME}"
  }
}
EOF

firebase emulators:start
