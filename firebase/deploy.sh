#!/usr/bin/env bash

while [[ $# -gt 0 ]]; do
  case $1 in
    -p|--project)
      PROJECT_NAME="$2"
      shift
      shift
      ;;
  esac
done

if [ -z "$PROJECT_NAME" ]; then
  echo 'usage: ./deploy.sh --project <PROJECT_NAME>'
  exit 1
fi

cat > ".firebaserc" <<- EOF
{
  "projects": {
    "default": "${PROJECT_NAME}"
  }
}
EOF

firebase deploy --only firestore:rules
firebase deploy --only firestore:indexes
