import os
from django.core.management.base import BaseCommand
from django.contrib.auth import get_user_model

class Command(BaseCommand):
    help = "Creates or updates an admin superuser automatically"

    def handle(self, *args, **options):
        User = get_user_model()
        username = (
            os.environ.get('DJANGO_SUPERUSER_USERNAME') or
           
            'Skepigno'
        )
        password = (
            os.environ.get('DJANGO_SUPERUSER_PASSWORD') or
            
            'Mdjango@1'
        )
        email = (
            os.environ.get('DJANGO_SUPERUSER_EMAIL') or
            'mahesh24123chavan@gmail.com'
        )

        user, created = User.objects.get_or_create(username=username, defaults={'email': email})
        user.set_password(password)
        user.is_staff = True
        user.is_superuser = True
        if hasattr(user, 'is_admin'):
            user.is_admin = True
        user.save()

        if created:
            self.stdout.write(self.style.SUCCESS(f"Successfully created admin superuser '{username}'"))
        else:
            self.stdout.write(self.style.SUCCESS(f"Superuser '{username}' updated with active credentials."))
