#!/usr/bin/env bash
# exit on error
set -o errexit

pip install -r requirements.txt

# collectstatic only — no DB access needed during build.
# IMPORTANT: migrate runs in the Start Command, not here.
# Render's internal PostgreSQL hostname is only reachable at runtime, not during builds.
#
# Render Start Command must be:
#   python manage.py migrate && gunicorn service_connect.wsgi:application --bind 0.0.0.0:$PORT --workers 2 --timeout 120
python manage.py collectstatic --no-input
