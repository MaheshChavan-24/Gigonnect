from django.urls import path
from .views import RegisterView, GoogleLoginView, DocumentUploadView, NotificationListView, NotificationReadView, MyTokenObtainPairView, CurrentUserView
from .admin_views import (
    AdminDashboardStatsView,
    AdminUserListView,
    AdminUserDetailView,
    AdminKYCListView,
    AdminKYCReviewActionView,
    AdminJobListView,
    AdminJobDisputeActionView,
    AdminTradeProfileListView,
    AdminReviewModerationView,
)
from rest_framework_simplejwt.views import (
    TokenRefreshView,
)

urlpatterns = [
    # 1. Registration: http://127.0.0.1:8000/api/users/register/
    path('register/', RegisterView.as_view(), name='auth_register'),
    
    # 2. Login: http://127.0.0.1:8000/api/users/login/
    # This handles the password check and returns the JWT tokens
    path('login/', MyTokenObtainPairView.as_view(), name='token_obtain_pair'),
    
    # 3. Refresh Token: http://127.0.0.1:8000/api/users/token/refresh/
    path('token/refresh/', TokenRefreshView.as_view(), name='token_refresh'),

    # 4. Google Login: http://127.0.0.1:8000/api/users/google-login/
    path('google-login/', GoogleLoginView.as_view(), name='google_login'),

    # 5. Document Upload: http://127.0.0.1:8000/api/users/upload-documents/
    path('upload-documents/', DocumentUploadView.as_view(), name='document_upload'),

    # 6. Notifications
    path('notifications/', NotificationListView.as_view(), name='notifications'),
    path('notifications/<int:pk>/read/', NotificationReadView.as_view(), name='notification_read'),

    # 7. Current User Detail
    path('me/', CurrentUserView.as_view(), name='current_user'),

    # ==========================================
    # 8. ADMINISTRATIVE API ENDPOINTS
    # ==========================================
    path('admin/stats/', AdminDashboardStatsView.as_view(), name='admin_stats'),
    path('admin/users/', AdminUserListView.as_view(), name='admin_users_list'),
    path('admin/users/<int:pk>/', AdminUserDetailView.as_view(), name='admin_user_detail'),
    path('admin/kyc/', AdminKYCListView.as_view(), name='admin_kyc_list'),
    path('admin/kyc/review/', AdminKYCReviewActionView.as_view(), name='admin_kyc_review_action'),
    path('admin/jobs/', AdminJobListView.as_view(), name='admin_jobs_list'),
    path('admin/jobs/<int:pk>/dispute/', AdminJobDisputeActionView.as_view(), name='admin_job_dispute_action'),
    path('admin/trade-profiles/', AdminTradeProfileListView.as_view(), name='admin_trade_profiles_list'),
    path('admin/trade-profiles/<int:pk>/', AdminTradeProfileListView.as_view(), name='admin_trade_profile_detail'),
    path('admin/reviews/', AdminReviewModerationView.as_view(), name='admin_reviews_list'),
    path('admin/reviews/<int:pk>/', AdminReviewModerationView.as_view(), name='admin_review_delete'),
]