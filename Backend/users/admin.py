from django.contrib import admin
from django.contrib.auth.admin import UserAdmin
from django.utils.html import format_html
from .models import User, Notification

# Register the custom User model
@admin.register(User)
class CustomUserAdmin(UserAdmin):
    fieldsets = UserAdmin.fieldsets + (
        ('Roles & Address', {'fields': ('is_client', 'is_worker', 'is_admin', 'phone_number', 'address_lat', 'address_lng')}),
        ('Verification & KYC Documents', {
            'fields': (
                'verification_status',
                'id_type',
                'id_front_image',
                'id_front_preview',
                'id_back_image',
                'id_back_preview',
                'id_selfie_image',
                'id_selfie_preview',
                'rejection_reason',
                'submitted_at',
                'reviewed_at'
            )
        }),
        ('Wallet & Bank Details', {'fields': ('wallet_balance', 'bank_name', 'bank_account_number', 'bank_ifsc')}),
    )
    add_fieldsets = UserAdmin.add_fieldsets + (
        ('Roles', {'fields': ('is_client', 'is_worker', 'is_admin', 'phone_number')}),
    )
    readonly_fields = ['id_front_preview', 'id_back_preview', 'id_selfie_preview']
    list_display = ['username', 'email', 'is_client', 'is_worker', 'verification_status', 'id_type', 'submitted_at', 'is_staff']
    list_filter = UserAdmin.list_filter + ('verification_status', 'is_client', 'is_worker', 'id_type')
    search_fields = UserAdmin.search_fields + ('phone_number', 'username', 'email')

    def id_front_preview(self, obj):
        if obj.id_front_image:
            return format_html(
                '<a href="{}" target="_blank"><img src="{}" style="max-height: 160px; max-width: 280px; border-radius: 8px; border: 1px solid #ddd; object-fit: contain;" /></a>',
                obj.id_front_image.url, obj.id_front_image.url
            )
        return "No Front ID Document Uploaded"
    id_front_preview.short_description = "Front ID Preview"

    def id_back_preview(self, obj):
        if obj.id_back_image:
            return format_html(
                '<a href="{}" target="_blank"><img src="{}" style="max-height: 160px; max-width: 280px; border-radius: 8px; border: 1px solid #ddd; object-fit: contain;" /></a>',
                obj.id_back_image.url, obj.id_back_image.url
            )
        return "No Back ID Document Uploaded"
    id_back_preview.short_description = "Back ID Preview"

    def id_selfie_preview(self, obj):
        if obj.id_selfie_image:
            return format_html(
                '<a href="{}" target="_blank"><img src="{}" style="max-height: 160px; max-width: 280px; border-radius: 8px; border: 1px solid #ddd; object-fit: contain;" /></a>',
                obj.id_selfie_image.url, obj.id_selfie_image.url
            )
        return "No Selfie Image Uploaded"
    id_selfie_preview.short_description = "Selfie Photo Preview"

@admin.register(Notification)
class NotificationAdmin(admin.ModelAdmin):
    list_display = ['user', 'title', 'is_read', 'created_at']
    list_filter = ['is_read', 'created_at']
    search_fields = ['user__username', 'title', 'message']