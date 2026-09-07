from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status, permissions
from django.contrib.auth import get_user_model
from django.db.models import Count, Sum, Q
from django.utils import timezone
from users.models import Notification
from users.serializers import UserSerializer
from jobs.models import Job, Review
from jobs.serializers import JobSerializer, ReviewSerializer
from profiles.models import WorkerDocument, TradeProfile, ServiceRequest
from profiles.serializers import WorkerDocumentSerializer, TradeProfileSerializer, ServiceRequestSerializer

User = get_user_model()

class IsPlatformAdmin(permissions.BasePermission):
    """
    Allows access only to authenticated platform admins / staff / superusers.
    """
    def has_permission(self, request, view):
        return bool(
            request.user and
            request.user.is_authenticated and
            (request.user.is_staff or request.user.is_superuser or getattr(request.user, 'is_admin', False))
        )


class AdminDashboardStatsView(APIView):
    permission_classes = [IsPlatformAdmin]

    def get(self, request):
        total_users = User.objects.count()
        total_clients = User.objects.filter(is_client=True).count()
        total_workers = User.objects.filter(is_worker=True).count()
        total_staff = User.objects.filter(Q(is_staff=True) | Q(is_superuser=True) | Q(is_admin=True)).count()

        # Verification stats
        kyc_pending = User.objects.filter(verification_status='pending').count()
        kyc_verified = User.objects.filter(verification_status='verified').count()
        kyc_rejected = User.objects.filter(verification_status='rejected').count()
        kyc_unsubmitted = User.objects.filter(verification_status='unsubmitted').count()

        # Worker documents stats
        docs_pending = WorkerDocument.objects.filter(status='pending').count()
        docs_verified = WorkerDocument.objects.filter(status='verified').count()

        # Job stats
        total_jobs = Job.objects.count()
        jobs_pending = Job.objects.filter(status='pending').count()
        jobs_active = Job.objects.filter(status__in=['accepted', 'scheduled']).count()
        jobs_worker_completed = Job.objects.filter(status='worker_completed').count()
        jobs_disputed = Job.objects.filter(status='disputed').count()
        jobs_completed = Job.objects.filter(status='completed').count()
        jobs_declined = Job.objects.filter(status='declined').count()

        # Direct Service Requests
        total_service_requests = ServiceRequest.objects.count()
        service_requests_pending = ServiceRequest.objects.filter(status='pending').count()
        service_requests_disputed = ServiceRequest.objects.filter(status='disputed').count()

        # Financial / Escrow stats
        escrow_held = Job.objects.filter(escrow_status='held').aggregate(total=Sum('budget'))['total'] or 0.00
        escrow_released = Job.objects.filter(escrow_status='released').aggregate(total=Sum('budget'))['total'] or 0.00
        escrow_refunded = Job.objects.filter(escrow_status='refunded').aggregate(total=Sum('budget'))['total'] or 0.00

        trade_profiles_count = TradeProfile.objects.count()
        total_reviews = Review.objects.count()

        return Response({
            "users": {
                "total": total_users,
                "clients": total_clients,
                "workers": total_workers,
                "staff": total_staff,
                "kyc_pending": kyc_pending,
                "kyc_verified": kyc_verified,
                "kyc_rejected": kyc_rejected,
                "kyc_unsubmitted": kyc_unsubmitted,
                "docs_pending": docs_pending,
                "docs_verified": docs_verified,
            },
            "jobs": {
                "total": total_jobs,
                "pending": jobs_pending,
                "active": jobs_active,
                "worker_completed": jobs_worker_completed,
                "disputed": jobs_disputed,
                "completed": jobs_completed,
                "declined": jobs_declined,
            },
            "service_requests": {
                "total": total_service_requests,
                "pending": service_requests_pending,
                "disputed": service_requests_disputed,
            },
            "escrow": {
                "held": float(escrow_held),
                "released": float(escrow_released),
                "refunded": float(escrow_refunded),
            },
            "trade_profiles": trade_profiles_count,
            "reviews": total_reviews,
            "server_time": timezone.now().isoformat(),
        }, status=status.HTTP_200_OK)


class AdminUserListView(APIView):
    permission_classes = [IsPlatformAdmin]

    def get(self, request):
        queryset = User.objects.all().order_by('-date_joined')

        role = request.query_params.get('role')
        if role == 'client':
            queryset = queryset.filter(is_client=True)
        elif role == 'worker':
            queryset = queryset.filter(is_worker=True)
        elif role == 'admin':
            queryset = queryset.filter(Q(is_staff=True) | Q(is_superuser=True) | Q(is_admin=True))

        verification = request.query_params.get('verification')
        if verification:
            queryset = queryset.filter(verification_status=verification)

        active_status = request.query_params.get('active')
        if active_status == 'true':
            queryset = queryset.filter(is_active=True)
        elif active_status == 'false':
            queryset = queryset.filter(is_active=False)

        search = request.query_params.get('search')
        if search:
            queryset = queryset.filter(
                Q(username__icontains=search) |
                Q(email__icontains=search) |
                Q(first_name__icontains=search) |
                Q(phone_number__icontains=search)
            )

        serializer = UserSerializer(queryset, many=True)
        return Response(serializer.data, status=status.HTTP_200_OK)


class AdminUserDetailView(APIView):
    permission_classes = [IsPlatformAdmin]

    def get(self, request, pk):
        try:
            user = User.objects.get(pk=pk)
        except User.DoesNotExist:
            return Response({"error": "User not found."}, status=status.HTTP_404_NOT_FOUND)

        user_data = UserSerializer(user).data

        # Fetch associated activity
        posted_jobs = Job.objects.filter(client=user).order_by('-created_at')[:10]
        taken_jobs = Job.objects.filter(worker=user).order_by('-created_at')[:10]
        worker_docs = WorkerDocument.objects.filter(worker=user)
        trade_profiles = TradeProfile.objects.filter(worker=user)
        reviews_received = Review.objects.filter(target=user).order_by('-created_at')[:10]

        return Response({
            "user": user_data,
            "posted_jobs": JobSerializer(posted_jobs, many=True).data,
            "taken_jobs": JobSerializer(taken_jobs, many=True).data,
            "worker_docs": WorkerDocumentSerializer(worker_docs, many=True).data,
            "trade_profiles": TradeProfileSerializer(trade_profiles, many=True).data,
            "reviews_received": ReviewSerializer(reviews_received, many=True).data,
        }, status=status.HTTP_200_OK)

    def patch(self, request, pk):
        try:
            user = User.objects.get(pk=pk)
        except User.DoesNotExist:
            return Response({"error": "User not found."}, status=status.HTTP_404_NOT_FOUND)

        # Allow admin to update is_active, is_staff, is_admin, wallet_balance, verification_status
        allowed_fields = ['is_active', 'is_staff', 'is_admin', 'wallet_balance', 'verification_status', 'rejection_reason']
        for field in allowed_fields:
            if field in request.data:
                setattr(user, field, request.data[field])

        user.save()
        return Response({
            "message": f"User {user.username} updated successfully.",
            "user": UserSerializer(user).data
        }, status=status.HTTP_200_OK)


class AdminKYCListView(APIView):
    permission_classes = [IsPlatformAdmin]

    def get(self, request):
        status_filter = request.query_params.get('status', 'all')

        # 1. User Identity KYC Submissions
        user_kyc_qs = User.objects.exclude(verification_status='unsubmitted')
        if status_filter != 'all':
            user_kyc_qs = user_kyc_qs.filter(verification_status=status_filter)
        user_kyc_qs = user_kyc_qs.order_by('-submitted_at', '-date_joined')

        user_kyc_list = []
        for u in user_kyc_qs:
            user_kyc_list.append({
                "type": "user_kyc",
                "id": u.id,
                "user_id": u.id,
                "username": u.username,
                "name": u.first_name,
                "email": u.email,
                "phone_number": u.phone_number,
                "role": "Worker" if u.is_worker else ("Client" if u.is_client else "User"),
                "verification_status": u.verification_status,
                "id_type": u.id_type or "Identity Document",
                "id_front_image": u.id_front_image.url if u.id_front_image else None,
                "id_back_image": u.id_back_image.url if u.id_back_image else None,
                "id_selfie_image": u.id_selfie_image.url if u.id_selfie_image else None,
                "rejection_reason": u.rejection_reason,
                "submitted_at": u.submitted_at,
                "reviewed_at": u.reviewed_at,
            })

        # 2. Worker Document Submissions
        worker_docs_qs = WorkerDocument.objects.all().select_related('worker')
        if status_filter != 'all':
            worker_docs_qs = worker_docs_qs.filter(status=status_filter)
        worker_docs_qs = worker_docs_qs.order_by('-uploaded_at')

        worker_doc_list = []
        for doc in worker_docs_qs:
            worker_doc_list.append({
                "type": "worker_doc",
                "id": doc.id,
                "user_id": doc.worker.id,
                "username": doc.worker.username,
                "name": doc.worker.first_name,
                "email": doc.worker.email,
                "phone_number": doc.worker.phone_number,
                "role": "Worker",
                "verification_status": doc.status,
                "id_type": doc.document_type,
                "file_url": doc.file.url if doc.file else None,
                "uploaded_at": doc.uploaded_at,
            })

        return Response({
            "user_kyc": user_kyc_list,
            "worker_docs": worker_doc_list,
            "pending_count": User.objects.filter(verification_status='pending').count() + WorkerDocument.objects.filter(status='pending').count()
        }, status=status.HTTP_200_OK)


class AdminKYCReviewActionView(APIView):
    permission_classes = [IsPlatformAdmin]

    def post(self, request):
        doc_type = request.data.get('type') # 'user_kyc' or 'worker_doc'
        target_id = request.data.get('id')
        action = request.data.get('action') # 'approve' or 'reject'
        reason = request.data.get('reason', '')

        if not target_id or not action:
            return Response({"error": "Target ID and action ('approve' or 'reject') are required."}, status=status.HTTP_400_BAD_REQUEST)

        if doc_type == 'user_kyc':
            try:
                user = User.objects.get(pk=target_id)
            except User.DoesNotExist:
                return Response({"error": "User not found."}, status=status.HTTP_404_NOT_FOUND)

            if action == 'approve':
                user.verification_status = 'verified'
                user.rejection_reason = None
                user.reviewed_at = timezone.now()
                user.save()

                Notification.objects.create(
                    user=user,
                    title="🎉 Identity Verification Approved!",
                    message="Your identity verification documents have been reviewed and approved by the platform administrator. You now have full verified access!"
                )
                return Response({"message": f"User '{user.username}' KYC verified successfully."}, status=status.HTTP_200_OK)

            elif action == 'reject':
                if not reason.strip():
                    return Response({"error": "A rejection reason is required."}, status=status.HTTP_400_BAD_REQUEST)

                user.verification_status = 'rejected'
                user.rejection_reason = reason
                user.reviewed_at = timezone.now()
                user.save()

                Notification.objects.create(
                    user=user,
                    title="⚠️ Identity Verification Rejected",
                    message=f"Your submitted verification document could not be approved. Reason: {reason}. Please re-upload clear copies."
                )
                return Response({"message": f"User '{user.username}' KYC rejected."}, status=status.HTTP_200_OK)

        elif doc_type == 'worker_doc':
            try:
                doc = WorkerDocument.objects.get(pk=target_id)
            except WorkerDocument.DoesNotExist:
                return Response({"error": "Document not found."}, status=status.HTTP_404_NOT_FOUND)

            if action == 'approve':
                doc.status = 'verified'
                doc.save()

                Notification.objects.create(
                    user=doc.worker,
                    title=f"✅ {doc.document_type} Verified",
                    message=f"Your {doc.document_type} has been reviewed and verified by administration."
                )
                return Response({"message": f"Worker document #{doc.id} approved."}, status=status.HTTP_200_OK)

            elif action == 'reject':
                doc.status = 'rejected'
                doc.save()

                Notification.objects.create(
                    user=doc.worker,
                    title=f"❌ {doc.document_type} Rejected",
                    message=f"Your {doc.document_type} was rejected by administration. Note: {reason or 'Invalid or unclear document.'}"
                )
                return Response({"message": f"Worker document #{doc.id} rejected."}, status=status.HTTP_200_OK)

        return Response({"error": "Invalid doc_type. Must be 'user_kyc' or 'worker_doc'."}, status=status.HTTP_400_BAD_REQUEST)


class AdminJobListView(APIView):
    permission_classes = [IsPlatformAdmin]

    def get(self, request):
        queryset = Job.objects.all().select_related('client', 'worker').order_by('-created_at')

        status_param = request.query_params.get('status')
        if status_param and status_param != 'all':
            queryset = queryset.filter(status=status_param)

        escrow_param = request.query_params.get('escrow_status')
        if escrow_param and escrow_param != 'all':
            queryset = queryset.filter(escrow_status=escrow_param)

        search = request.query_params.get('search')
        if search:
            queryset = queryset.filter(
                Q(title__icontains=search) |
                Q(description__icontains=search) |
                Q(client__username__icontains=search) |
                Q(worker__username__icontains=search) |
                Q(service_type__icontains=search) |
                Q(address__icontains=search)
            )

        job_list = []
        for job in queryset:
            job_list.append({
                "id": job.id,
                "title": job.title,
                "description": job.description,
                "service_type": job.service_type,
                "budget": float(job.budget),
                "is_negotiable": job.is_negotiable,
                "urgency_level": job.urgency_level,
                "status": job.status,
                "address": job.address,
                "latitude": job.latitude,
                "longitude": job.longitude,
                "client": {
                    "id": job.client.id,
                    "username": job.client.username,
                    "name": job.client.first_name,
                    "phone": job.client.phone_number,
                    "email": job.client.email,
                },
                "worker": {
                    "id": job.worker.id,
                    "username": job.worker.username,
                    "name": job.worker.first_name,
                    "phone": job.worker.phone_number,
                    "email": job.worker.email,
                } if job.worker else None,
                "escrow_status": job.escrow_status,
                "payment_method": job.payment_method,
                "paid_at": job.paid_at,
                "released_at": job.released_at,
                "scheduled_date": job.scheduled_date,
                "scheduled_time_slot": job.scheduled_time_slot,
                "created_at": job.created_at,
            })

        # Also get direct service requests
        sr_queryset = ServiceRequest.objects.all().select_related('client', 'worker', 'trade_profile').order_by('-created_at')
        sr_list = []
        for sr in sr_queryset:
            sr_list.append({
                "id": sr.id,
                "client_username": sr.client.username,
                "client_name": sr.client.first_name,
                "worker_username": sr.worker.username,
                "worker_name": sr.worker.first_name,
                "trade_category": sr.trade_profile.trade_category,
                "description": sr.description,
                "preferred_date": sr.preferred_date,
                "preferred_time_slot": sr.preferred_time_slot,
                "status": sr.status,
                "worker_notes": sr.worker_notes,
                "created_at": sr.created_at,
            })

        return Response({
            "jobs": job_list,
            "service_requests": sr_list
        }, status=status.HTTP_200_OK)


class AdminJobDisputeActionView(APIView):
    permission_classes = [IsPlatformAdmin]

    def post(self, request, pk):
        try:
            job = Job.objects.get(pk=pk)
        except Job.DoesNotExist:
            return Response({"error": "Job not found."}, status=status.HTTP_404_NOT_FOUND)

        action = request.data.get('action') # 'release_to_worker', 'refund_to_client', 'force_complete', 'cancel'
        note = request.data.get('note', '')

        if not action:
            return Response({"error": "Action parameter is required."}, status=status.HTTP_400_BAD_REQUEST)

        if action == 'release_to_worker':
            if not job.worker:
                return Response({"error": "Cannot release escrow: No worker assigned to this job."}, status=status.HTTP_400_BAD_REQUEST)

            job.status = 'completed'
            job.escrow_status = 'released'
            job.released_at = timezone.now()
            job.save()

            # Credit worker wallet
            job.worker.wallet_balance += job.budget
            job.worker.save()

            Notification.objects.create(
                user=job.worker,
                title="💰 Admin Dispute Resolved: Funds Released",
                message=f"Administrator has resolved the dispute on Job #{job.id} ('{job.title}'). Funds of ₹{job.budget} have been credited to your wallet. Note: {note}"
            )
            Notification.objects.create(
                user=job.client,
                title="ℹ️ Admin Dispute Decision",
                message=f"Administrator has reviewed Job #{job.id} ('{job.title}') and released payment to the worker. Note: {note}"
            )

            return Response({"message": f"Job #{job.id} dispute resolved. Funds released to worker."}, status=status.HTTP_200_OK)

        elif action == 'refund_to_client':
            job.status = 'declined'
            job.escrow_status = 'refunded'
            job.save()

            # Credit client wallet
            job.client.wallet_balance += job.budget
            job.client.save()

            Notification.objects.create(
                user=job.client,
                title="💰 Admin Dispute Resolved: Refund Issued",
                message=f"Administrator has resolved the dispute on Job #{job.id} ('{job.title}'). A full refund of ₹{job.budget} has been returned to your wallet. Note: {note}"
            )
            if job.worker:
                Notification.objects.create(
                    user=job.worker,
                    title="ℹ️ Admin Dispute Decision",
                    message=f"Administrator has reviewed Job #{job.id} ('{job.title}') and refunded the client. Note: {note}"
                )

            return Response({"message": f"Job #{job.id} dispute resolved. Full refund issued to client."}, status=status.HTTP_200_OK)

        elif action == 'force_complete':
            job.status = 'completed'
            if job.escrow_status == 'held' and job.worker:
                job.escrow_status = 'released'
                job.released_at = timezone.now()
                job.worker.wallet_balance += job.budget
                job.worker.save()
            job.save()

            return Response({"message": f"Job #{job.id} marked completed by administration."}, status=status.HTTP_200_OK)

        elif action == 'cancel':
            job.status = 'declined'
            job.save()
            return Response({"message": f"Job #{job.id} marked declined/cancelled."}, status=status.HTTP_200_OK)

        return Response({"error": "Invalid action."}, status=status.HTTP_400_BAD_REQUEST)


class AdminTradeProfileListView(APIView):
    permission_classes = [IsPlatformAdmin]

    def get(self, request):
        queryset = TradeProfile.objects.all().select_related('worker').order_by('-updated_at')
        category = request.query_params.get('category')
        if category:
            queryset = queryset.filter(trade_category=category)

        active = request.query_params.get('active')
        if active == 'true':
            queryset = queryset.filter(is_active=True)
        elif active == 'false':
            queryset = queryset.filter(is_active=False)

        serializer = TradeProfileSerializer(queryset, many=True)
        return Response(serializer.data, status=status.HTTP_200_OK)

    def patch(self, request, pk):
        try:
            profile = TradeProfile.objects.get(pk=pk)
        except TradeProfile.DoesNotExist:
            return Response({"error": "Trade profile not found."}, status=status.HTTP_404_NOT_FOUND)

        if 'is_active' in request.data:
            profile.is_active = request.data['is_active']
            profile.save()

        return Response({
            "message": f"Trade profile #{profile.id} updated.",
            "profile": TradeProfileSerializer(profile).data
        }, status=status.HTTP_200_OK)


class AdminReviewModerationView(APIView):
    permission_classes = [IsPlatformAdmin]

    def get(self, request):
        reviews = Review.objects.all().select_related('reviewer', 'target', 'job').order_by('-created_at')
        review_list = []
        for r in reviews:
            review_list.append({
                "id": r.id,
                "job_id": r.job.id,
                "job_title": r.job.title,
                "reviewer_username": r.reviewer.username,
                "reviewer_name": r.reviewer.first_name,
                "target_username": r.target.username,
                "target_name": r.target.first_name,
                "review_type": r.review_type,
                "rating": r.rating,
                "comment": r.comment,
                "created_at": r.created_at,
            })
        return Response(review_list, status=status.HTTP_200_OK)

    def delete(self, request, pk):
        try:
            review = Review.objects.get(pk=pk)
            review.delete()
            return Response({"message": f"Review #{pk} deleted by administration."}, status=status.HTTP_200_OK)
        except Review.DoesNotExist:
            return Response({"error": "Review not found."}, status=status.HTTP_404_NOT_FOUND)
