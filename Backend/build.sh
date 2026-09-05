#!/usr/bin/env bash
# exit on error
set -o errexit

pip install -r requirements.txt

# NOTE: collectstatic runs here (no DB needed).
# NOTE: migrate is intentionally NOT run here.
#       Render's internal PostgreSQL hostname is only reachable at runtime,
#       not during the build phase. Run migrations via the Start Command instead.
#       Start Command: python manage.py migrate && gunicorn service_connect.wsgi:application
python manage.py collectstatic --no-input
