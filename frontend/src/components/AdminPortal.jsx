import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';

const BASE_URL = process.env.REACT_APP_API_URL || "http://127.0.0.1:8000";
const ADMIN_API = `${BASE_URL}/api/users/admin/`;
const USER_API = `${BASE_URL}/api/users/`;

export default function AdminPortal() {
  const [adminToken, setAdminToken] = useState(localStorage.getItem('admin_access_token') || localStorage.getItem('access_token') || '');
  const [adminUser, setAdminUser] = useState(null);
  const [authChecking, setAuthChecking] = useState(true);

  // Login Form State (Dedicated Govt-Style Admin Login)
  const [loginForm, setLoginForm] = useState({ username: '', password: '' });
  const [loginError, setLoginError] = useState('');
  const [loginLoading, setLoginLoading] = useState(false);

  // Active Tab
  const [activeTab, setActiveTab] = useState('dashboard'); // 'dashboard', 'kyc', 'users', 'jobs', 'trade_profiles', 'reviews'

  // Data states
  const [stats, setStats] = useState(null);
  const [loadingStats, setLoadingStats] = useState(false);

  // KYC Verification Tab State
  const [kycData, setKycData] = useState({ user_kyc: [], worker_docs: [], pending_count: 0 });
  const [kycFilter, setKycFilter] = useState('all'); // 'all', 'pending', 'verified', 'rejected'
  const [selectedDoc, setSelectedDoc] = useState(null); // Document detail / review modal
  const [reviewAction, setReviewAction] = useState({ action: '', reason: '' });
  const [processingKyc, setProcessingKyc] = useState(false);

  // User Management Tab State
  const [usersList, setUsersList] = useState([]);
  const [userFilterRole, setUserFilterRole] = useState('all');
  const [userFilterStatus, setUserFilterStatus] = useState('all');
  const [userFilterVerification, setUserFilterVerification] = useState('all');
  const [userSearch, setUserSearch] = useState('');
  const [selectedUserDetail, setSelectedUserDetail] = useState(null);

  // Jobs Tab State
  const [jobsData, setJobsData] = useState({ jobs: [], service_requests: [] });
  const [jobStatusFilter, setJobStatusFilter] = useState('all');
  const [jobSearch, setJobSearch] = useState('');
  const [selectedDisputeJob, setSelectedDisputeJob] = useState(null);
  const [disputeActionData, setDisputeActionData] = useState({ action: 'release_to_worker', note: '' });
  const [processingDispute, setProcessingDispute] = useState(false);

  // Trade Profiles Tab State
  const [tradeProfilesList, setTradeProfilesList] = useState([]);
  const [tradeCategoryFilter, setTradeCategoryFilter] = useState('all');

  // Reviews Tab State
  const [reviewsList, setReviewsList] = useState([]);

  // Toast / System message
  const [toast, setToast] = useState(null);

  const showToast = (message, type = 'info') => {
    setToast({ message, type });
    setTimeout(() => setToast(null), 4000);
  };

  const getAuthHeaders = useCallback(() => {
    const token = localStorage.getItem('admin_access_token') || localStorage.getItem('access_token');
    return { headers: { Authorization: `Bearer ${token}` } };
  }, []);

  // 1. Verify Admin Session on Mount
  const verifyAdminAuth = useCallback(async () => {
    setAuthChecking(true);
    const token = localStorage.getItem('admin_access_token') || localStorage.getItem('access_token');
    if (!token) {
      setAuthChecking(false);
      setAdminUser(null);
      return;
    }

    try {
      const res = await axios.get(`${USER_API}me/`, {
        headers: { Authorization: `Bearer ${token}` }
      });

      if (res.data.is_staff || res.data.is_superuser || res.data.is_admin) {
        setAdminUser(res.data);
        setAdminToken(token);
      } else {
        // Non-admin user attempted to access
        setAdminUser(null);
        setAdminToken('');
        setLoginError('Access Denied: Administrative and staff authorization required.');
      }
    } catch (err) {
      console.error("Admin auth verification failed", err);
      setAdminUser(null);
      setAdminToken('');
    } finally {
      setAuthChecking(false);
    }
  }, []);

  useEffect(() => {
    verifyAdminAuth();
  }, [verifyAdminAuth]);

  // Dedicated Admin Login Handler
  const handleAdminLogin = async (e) => {
    e.preventDefault();
    setLoginLoading(true);
    setLoginError('');

    try {
      const res = await axios.post(`${USER_API}login/`, {
        username: loginForm.username,
        password: loginForm.password
      });

      const user = res.data.user;
      if (user.is_staff || user.is_superuser || user.is_admin) {
        localStorage.setItem('admin_access_token', res.data.access);
        setAdminToken(res.data.access);
        setAdminUser(user);
        showToast(`Welcome, Superintendent ${user.username}. Authorized session started.`, 'success');
      } else {
        setLoginError('Authorization Failed: This account does not possess administrative staff credentials.');
      }
    } catch (err) {
      setLoginError(err.response?.data?.detail || 'Invalid administrative credentials or account inactive.');
    } finally {
      setLoginLoading(false);
    }
  };

  const handleAdminLogout = () => {
    localStorage.removeItem('admin_access_token');
    setAdminToken('');
    setAdminUser(null);
    setLoginForm({ username: '', password: '' });
  };

  // --- TAB DATA LOADERS ---
  const fetchDashboardStats = useCallback(async () => {
    if (!adminToken) return;
    setLoadingStats(true);
    try {
      const res = await axios.get(`${ADMIN_API}stats/`, getAuthHeaders());
      setStats(res.data);
    } catch (err) {
      console.error("Failed to load stats", err);
      if (err.response?.status === 403) handleAdminLogout();
    } finally {
      setLoadingStats(false);
    }
  }, [adminToken, getAuthHeaders]);

  const fetchKycList = useCallback(async () => {
    if (!adminToken) return;
    try {
      const res = await axios.get(`${ADMIN_API}kyc/?status=${kycFilter}`, getAuthHeaders());
      setKycData(res.data);
    } catch (err) {
      console.error("Failed to load KYC list", err);
    }
  }, [adminToken, kycFilter, getAuthHeaders]);

  const fetchUsersList = useCallback(async () => {
    if (!adminToken) return;
    try {
      const params = new URLSearchParams();
      if (userFilterRole !== 'all') params.append('role', userFilterRole);
      if (userFilterStatus !== 'all') params.append('active', userFilterStatus === 'active' ? 'true' : 'false');
      if (userFilterVerification !== 'all') params.append('verification', userFilterVerification);
      if (userSearch.trim()) params.append('search', userSearch.trim());

      const res = await axios.get(`${ADMIN_API}users/?${params.toString()}`, getAuthHeaders());
      setUsersList(res.data);
    } catch (err) {
      console.error("Failed to load users", err);
    }
  }, [adminToken, userFilterRole, userFilterStatus, userFilterVerification, userSearch, getAuthHeaders]);

  const fetchJobsData = useCallback(async () => {
    if (!adminToken) return;
    try {
      const params = new URLSearchParams();
      if (jobStatusFilter !== 'all') params.append('status', jobStatusFilter);
      if (jobSearch.trim()) params.append('search', jobSearch.trim());

      const res = await axios.get(`${ADMIN_API}jobs/?${params.toString()}`, getAuthHeaders());
      setJobsData(res.data);
    } catch (err) {
      console.error("Failed to load jobs", err);
    }
  }, [adminToken, jobStatusFilter, jobSearch, getAuthHeaders]);

  const fetchTradeProfiles = useCallback(async () => {
    if (!adminToken) return;
    try {
      const params = new URLSearchParams();
      if (tradeCategoryFilter !== 'all') params.append('category', tradeCategoryFilter);
      const res = await axios.get(`${ADMIN_API}trade-profiles/?${params.toString()}`, getAuthHeaders());
      setTradeProfilesList(res.data);
    } catch (err) {
      console.error("Failed to load trade profiles", err);
    }
  }, [adminToken, tradeCategoryFilter, getAuthHeaders]);

  const fetchReviewsList = useCallback(async () => {
    if (!adminToken) return;
    try {
      const res = await axios.get(`${ADMIN_API}reviews/`, getAuthHeaders());
      setReviewsList(res.data);
    } catch (err) {
      console.error("Failed to load reviews", err);
    }
  }, [adminToken, getAuthHeaders]);

  // Switch tab data loading
  useEffect(() => {
    if (!adminUser) return;
    if (activeTab === 'dashboard') fetchDashboardStats();
    if (activeTab === 'kyc') fetchKycList();
    if (activeTab === 'users') fetchUsersList();
    if (activeTab === 'jobs') fetchJobsData();
    if (activeTab === 'trade_profiles') fetchTradeProfiles();
    if (activeTab === 'reviews') fetchReviewsList();
  }, [activeTab, adminUser, fetchDashboardStats, fetchKycList, fetchUsersList, fetchJobsData, fetchTradeProfiles, fetchReviewsList]);

  // KYC Review Action
  const handleKycAction = async (item, action, reason = '') => {
    setProcessingKyc(true);
    try {
      await axios.post(`${ADMIN_API}kyc/review/`, {
        type: item.type,
        id: item.id,
        action: action,
        reason: reason
      }, getAuthHeaders());

      showToast(`Document #${item.id} successfully ${action === 'approve' ? 'VERIFIED' : 'REJECTED'}.`, 'success');
      setSelectedDoc(null);
      setReviewAction({ action: '', reason: '' });
      fetchKycList();
      fetchDashboardStats();
    } catch (err) {
      showToast(err.response?.data?.error || 'Action failed.', 'error');
    } finally {
      setProcessingKyc(false);
    }
  };

  // Inspect User Detail
  const handleInspectUser = async (userId) => {
    try {
      const res = await axios.get(`${ADMIN_API}users/${userId}/`, getAuthHeaders());
      setSelectedUserDetail(res.data);
    } catch (err) {
      showToast('Could not fetch user dossier.', 'error');
    }
  };

  // Toggle User Active / Staff Status
  const handleUpdateUserStatus = async (userId, updatePayload) => {
    try {
      const res = await axios.patch(`${ADMIN_API}users/${userId}/`, updatePayload, getAuthHeaders());
      showToast(res.data.message, 'success');
      fetchUsersList();
      if (selectedUserDetail && selectedUserDetail.user.id === userId) {
        handleInspectUser(userId);
      }
    } catch (err) {
      showToast(err.response?.data?.error || 'Failed to update user.', 'error');
    }
  };

  // Resolve Job Dispute
  const handleResolveDispute = async () => {
    if (!selectedDisputeJob) return;
    setProcessingDispute(true);
    try {
      const res = await axios.post(`${ADMIN_API}jobs/${selectedDisputeJob.id}/dispute/`, {
        action: disputeActionData.action,
        note: disputeActionData.note
      }, getAuthHeaders());

      showToast(res.data.message, 'success');
      setSelectedDisputeJob(null);
      setDisputeActionData({ action: 'release_to_worker', note: '' });
      fetchJobsData();
      fetchDashboardStats();
    } catch (err) {
      showToast(err.response?.data?.error || 'Failed to resolve dispute.', 'error');
    } finally {
      setProcessingDispute(false);
    }
  };

  // Toggle Trade Profile Active
  const handleToggleTradeProfile = async (profileId, currentActive) => {
    try {
      await axios.patch(`${ADMIN_API}trade-profiles/${profileId}/`, { is_active: !currentActive }, getAuthHeaders());
      showToast(`Trade profile #${profileId} active status set to ${!currentActive}.`, 'success');
      fetchTradeProfiles();
    } catch (err) {
      showToast('Failed to update trade profile.', 'error');
    }
  };

  // Delete Review
  const handleDeleteReview = async (reviewId) => {
    if (!window.confirm(`Are you sure you want to permanently delete Review #${reviewId}?`)) return;
    try {
      await axios.delete(`${ADMIN_API}reviews/${reviewId}/`, getAuthHeaders());
      showToast(`Review #${reviewId} deleted.`, 'success');
      fetchReviewsList();
    } catch (err) {
      showToast('Failed to delete review.', 'error');
    }
  };

  // Helper date formatter
  const formatDate = (dateStr) => {
    if (!dateStr) return 'N/A';
    return new Date(dateStr).toLocaleString('en-IN', {
      year: 'numeric', month: 'short', day: 'numeric',
      hour: '2-digit', minute: '2-digit'
    });
  };

  // -------------------------------------------------------------
  // RENDER: AUTH CHECKING SPINNER
  // -------------------------------------------------------------
  if (authChecking) {
    return (
      <div className="min-h-screen bg-white flex flex-col items-center justify-center font-sans text-gray-800">
        <div className="w-12 h-12 border-4 border-blue-900 border-t-transparent rounded-full animate-spin mb-4"></div>
        <p className="text-sm font-bold tracking-wider uppercase text-gray-600">Verifying Administrative Clearance...</p>
      </div>
    );
  }

  // -------------------------------------------------------------
  // RENDER: DEDICATED GOVT / ADMIN LOGIN SCREEN
  // -------------------------------------------------------------
  if (!adminUser) {
    return (
      <div className="min-h-screen bg-slate-50 text-slate-900 font-sans flex flex-col justify-between">
        {/* Official Header */}
        <header className="bg-blue-900 text-white border-b-4 border-amber-500 py-4 px-8 shadow-md">
          <div className="max-w-6xl mx-auto flex items-center justify-between">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 bg-white text-blue-900 rounded-sm flex items-center justify-center font-serif text-2xl font-black border border-slate-300">
                🏛️
              </div>
              <div>
                <h1 className="text-lg font-black tracking-wide uppercase">SAHAYA PLATFORM ADMINISTRATION</h1>
                <p className="text-xs text-blue-200 tracking-wider">CENTRAL ADMINISTRATIVE & VERIFICATION DESK</p>
              </div>
            </div>
            <div className="text-right text-xs text-blue-200">
              <p className="font-mono">PORTAL: ADMIN-V1</p>
              <p className="font-semibold text-amber-300">AUTHORIZED ACCESS ONLY</p>
            </div>
          </div>
        </header>

        {/* Login Box */}
        <main className="flex-1 flex items-center justify-center p-6">
          <div className="w-full max-w-md bg-white border-2 border-slate-300 shadow-xl rounded-none p-8">
            <div className="border-b-2 border-slate-200 pb-4 mb-6 text-center">
              <div className="inline-block bg-blue-50 text-blue-900 text-xs font-bold px-3 py-1 border border-blue-200 mb-2 uppercase tracking-widest">
                Official Staff Sign-In
              </div>
              <h2 className="text-xl font-bold text-slate-800 uppercase tracking-tight">Superintendent Login</h2>
              <p className="text-xs text-slate-500 mt-1">Please enter verified platform administrator credentials.</p>
            </div>

            {loginError && (
              <div className="mb-6 p-3 bg-red-50 border-l-4 border-red-600 text-red-700 text-xs font-medium">
                <div className="font-bold flex items-center gap-1 mb-1">
                  <span>⚠️</span> SECURITY ALERT
                </div>
                <p>{loginError}</p>
              </div>
            )}

            <form onSubmit={handleAdminLogin} className="space-y-4">
              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Admin Username / ID <span className="text-red-600">*</span>
                </label>
                <input
                  type="text"
                  required
                  value={loginForm.username}
                  onChange={(e) => setLoginForm({ ...loginForm, username: e.target.value })}
                  placeholder="Enter Username"
                  className="w-full px-3 py-2.5 text-sm bg-white border-2 border-slate-300 rounded-none focus:border-blue-900 focus:outline-none font-mono"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Master Password <span className="text-red-600">*</span>
                </label>
                <input
                  type="password"
                  required
                  value={loginForm.password}
                  onChange={(e) => setLoginForm({ ...loginForm, password: e.target.value })}
                  placeholder="••••••••••••"
                  className="w-full px-3 py-2.5 text-sm bg-white border-2 border-slate-300 rounded-none focus:border-blue-900 focus:outline-none"
                />
              </div>

              <div className="pt-2">
                <button
                  type="submit"
                  disabled={loginLoading}
                  className="w-full py-3 bg-blue-900 hover:bg-blue-800 text-white font-bold text-sm tracking-wider uppercase border border-blue-950 transition shadow-sm disabled:opacity-50"
                >
                  {loginLoading ? "AUTHENTICATING..." : "VERIFY & ENTER CONSOLE →"}
                </button>
              </div>
            </form>

            <div className="mt-8 pt-4 border-t border-slate-200 text-center">
              <p className="text-[11px] text-slate-500 font-sans leading-relaxed">
                <strong>Notice:</strong> This administrative portal is restricted to authorized operations personnel. All authentication attempts and subsequent data queries are logged for audit compliance.
              </p>
            </div>
          </div>
        </main>

        {/* Official Footer */}
        <footer className="bg-slate-800 text-slate-400 text-xs py-3 px-8 text-center border-t border-slate-700">
          <p>© 2026 SAHAYA Community Services – Administrative Services Division. All Rights Reserved.</p>
        </footer>
      </div>
    );
  }

  // -------------------------------------------------------------
  // RENDER: MAIN ADMIN CONSOLE (AUTHENTICATED)
  // -------------------------------------------------------------
  return (
    <div className="min-h-screen bg-slate-100 text-slate-900 font-sans flex flex-col">
      {/* Toast Notification */}
      {toast && (
        <div className={`fixed top-4 right-4 z-50 px-5 py-3 border shadow-lg text-xs font-bold tracking-wide flex items-center gap-2 ${toast.type === 'success' ? 'bg-green-50 border-green-600 text-green-800' :
            toast.type === 'error' ? 'bg-red-50 border-red-600 text-red-800' :
              'bg-blue-50 border-blue-600 text-blue-800'
          }`}>
          <span>{toast.type === 'success' ? '✓' : toast.type === 'error' ? '✕' : 'ℹ'}</span>
          <span>{toast.message}</span>
        </div>
      )}

      {/* 1. TOP OFFICIAL HEADER */}
      <header className="bg-blue-900 text-white border-b-4 border-amber-500 shadow">
        <div className="max-w-7xl mx-auto px-6 py-3.5 flex flex-wrap items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-white text-blue-900 flex items-center justify-center font-bold text-xl border border-blue-950">
              🏛️
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h1 className="text-base font-black tracking-wide uppercase">SAHAYA ADMINISTRATIVE DESK</h1>
                <span className="bg-amber-400 text-blue-950 text-[10px] font-black px-2 py-0.5 uppercase tracking-wider">
                  SUPERINTENDENT
                </span>
              </div>
              <p className="text-xs text-blue-200">Central Portal for Document Verification, Dispute Settlement & Platform Governance</p>
            </div>
          </div>

          <div className="flex items-center gap-4 text-xs">
            <div className="bg-blue-950 px-3 py-1.5 border border-blue-800 text-right">
              <span className="text-blue-300 block text-[10px] uppercase">Officer In-Charge</span>
              <span className="font-bold text-white font-mono">{adminUser.username} ({adminUser.first_name || 'Staff'})</span>
            </div>
            <button
              onClick={handleAdminLogout}
              className="bg-red-700 hover:bg-red-800 text-white px-3 py-2 font-bold uppercase tracking-wider border border-red-900 transition"
              title="Securely exit administrative portal"
            >
              Log Out ⏻
            </button>
          </div>
        </div>
      </header>

      {/* 2. SUB-NAVIGATION TABS */}
      <nav className="bg-white border-b border-slate-300 shadow-sm sticky top-0 z-30">
        <div className="max-w-7xl mx-auto px-6 flex flex-wrap items-center justify-between">
          <div className="flex flex-wrap gap-1">
            {[
              { id: 'dashboard', label: '📊 Dashboard & Overview' },
              { id: 'kyc', label: `🪪 KYC Verifications ${stats?.users?.kyc_pending ? `(${stats.users.kyc_pending})` : ''}` },
              { id: 'users', label: '👥 User Directory' },
              { id: 'jobs', label: `🛠️ Jobs & Disputes ${stats?.jobs?.disputed ? `(${stats.jobs.disputed} Disputed)` : ''}` },
              { id: 'trade_profiles', label: '💼 Trade Profiles' },
              { id: 'reviews', label: '⭐ Reviews & Ratings' },
            ].map(tab => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`px-4 py-3 text-xs font-bold tracking-wide uppercase border-b-2 transition ${activeTab === tab.id
                    ? 'border-blue-900 text-blue-900 bg-blue-50/50'
                    : 'border-transparent text-slate-600 hover:text-slate-900 hover:bg-slate-50'
                  }`}
              >
                {tab.label}
              </button>
            ))}
          </div>

          <div className="py-2 text-[11px] text-slate-500 font-mono flex items-center gap-3">
            <span>● System Live</span>
            <button
              onClick={() => {
                if (activeTab === 'dashboard') fetchDashboardStats();
                if (activeTab === 'kyc') fetchKycList();
                if (activeTab === 'users') fetchUsersList();
                if (activeTab === 'jobs') fetchJobsData();
                if (activeTab === 'trade_profiles') fetchTradeProfiles();
                if (activeTab === 'reviews') fetchReviewsList();
                showToast('Console data refreshed', 'info');
              }}
              className="px-2 py-1 bg-slate-100 hover:bg-slate-200 border border-slate-300 font-sans font-bold text-slate-700"
            >
              ↻ Refresh
            </button>
          </div>
        </div>
      </nav>

      {/* 3. MAIN WORKSPACE CONTAINER */}
      <main className="flex-1 max-w-7xl w-full mx-auto p-6">

        {/* ======================================================== */}
        {/* TAB 1: DASHBOARD & OVERVIEW */}
        {/* ======================================================== */}
        {activeTab === 'dashboard' && (
          <div className="space-y-6">
            <div className="bg-white p-4 border border-slate-300 flex items-center justify-between">
              <div>
                <h2 className="text-sm font-bold uppercase tracking-wider text-slate-800">Operational Summary</h2>
                <p className="text-xs text-slate-500">Live platform metrics across users, verification queues, orders, and financial escrow.</p>
              </div>
              <span className="text-xs font-mono bg-slate-100 px-3 py-1 border border-slate-200">
                Server Time: {stats?.server_time ? new Date(stats.server_time).toLocaleString() : 'Loading...'}
              </span>
            </div>

            {loadingStats && !stats ? (
              <div className="p-12 text-center text-sm text-slate-500">Loading platform statistics...</div>
            ) : (
              <>
                {/* Metric Cards Grid */}
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                  {/* Total Users */}
                  <div className="bg-white border-2 border-slate-300 p-4 shadow-sm">
                    <div className="flex justify-between items-start">
                      <div>
                        <p className="text-[11px] font-bold uppercase tracking-wider text-slate-500">Total Registered Users</p>
                        <p className="text-2xl font-black text-slate-900 mt-1">{stats?.users?.total || 0}</p>
                      </div>
                      <span className="text-2xl">👥</span>
                    </div>
                    <div className="mt-3 pt-2 border-t border-slate-100 flex justify-between text-[11px] text-slate-600">
                      <span>Clients: <strong>{stats?.users?.clients || 0}</strong></span>
                      <span>Workers: <strong>{stats?.users?.workers || 0}</strong></span>
                      <span>Staff: <strong>{stats?.users?.staff || 0}</strong></span>
                    </div>
                  </div>

                  {/* KYC Pending */}
                  <div className={`bg-white border-2 ${stats?.users?.kyc_pending > 0 ? 'border-amber-400 bg-amber-50/20' : 'border-slate-300'} p-4 shadow-sm`}>
                    <div className="flex justify-between items-start">
                      <div>
                        <p className="text-[11px] font-bold uppercase tracking-wider text-amber-700">KYC Verification Queue</p>
                        <p className="text-2xl font-black text-amber-800 mt-1">{stats?.users?.kyc_pending || 0}</p>
                      </div>
                      <span className="text-2xl">🪪</span>
                    </div>
                    <div className="mt-3 pt-2 border-t border-slate-100 flex justify-between text-[11px] text-slate-600">
                      <span>Verified: <strong className="text-green-700">{stats?.users?.kyc_verified || 0}</strong></span>
                      <span>Rejected: <strong className="text-red-700">{stats?.users?.kyc_rejected || 0}</strong></span>
                      <span>Unsubmitted: <strong>{stats?.users?.kyc_unsubmitted || 0}</strong></span>
                    </div>
                  </div>

                  {/* Job Orders */}
                  <div className="bg-white border-2 border-slate-300 p-4 shadow-sm">
                    <div className="flex justify-between items-start">
                      <div>
                        <p className="text-[11px] font-bold uppercase tracking-wider text-slate-500">Total Marketplace Jobs</p>
                        <p className="text-2xl font-black text-slate-900 mt-1">{stats?.jobs?.total || 0}</p>
                      </div>
                      <span className="text-2xl">🛠️</span>
                    </div>
                    <div className="mt-3 pt-2 border-t border-slate-100 flex justify-between text-[11px] text-slate-600">
                      <span>Active: <strong className="text-blue-700">{stats?.jobs?.active || 0}</strong></span>
                      <span>Disputed: <strong className="text-red-700">{stats?.jobs?.disputed || 0}</strong></span>
                      <span>Completed: <strong className="text-green-700">{stats?.jobs?.completed || 0}</strong></span>
                    </div>
                  </div>

                  {/* Escrow Funds */}
                  <div className="bg-white border-2 border-slate-300 p-4 shadow-sm">
                    <div className="flex justify-between items-start">
                      <div>
                        <p className="text-[11px] font-bold uppercase tracking-wider text-slate-500">Escrow Balance Held</p>
                        <p className="text-2xl font-black text-blue-900 mt-1">₹{Number(stats?.escrow?.held || 0).toLocaleString('en-IN')}</p>
                      </div>
                      <span className="text-2xl">💰</span>
                    </div>
                    <div className="mt-3 pt-2 border-t border-slate-100 flex justify-between text-[11px] text-slate-600">
                      <span>Released: <strong>₹{Number(stats?.escrow?.released || 0).toLocaleString('en-IN')}</strong></span>
                      <span>Refunded: <strong>₹{Number(stats?.escrow?.refunded || 0).toLocaleString('en-IN')}</strong></span>
                    </div>
                  </div>
                </div>

                {/* Quick Action Shortcuts */}
                <div className="bg-white border-2 border-slate-300 p-6 shadow-sm">
                  <h3 className="text-xs font-bold uppercase tracking-wider text-slate-700 mb-4 border-b pb-2">Administrative Priority Actions</h3>
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <button
                      onClick={() => { setKycFilter('pending'); setActiveTab('kyc'); }}
                      className="p-4 bg-amber-50 border border-amber-300 hover:bg-amber-100 text-left transition"
                    >
                      <div className="flex items-center justify-between font-bold text-amber-900 text-sm">
                        <span>Review Pending KYC Submissions</span>
                        <span className="bg-amber-600 text-white text-xs px-2 py-0.5">{stats?.users?.kyc_pending || 0}</span>
                      </div>
                      <p className="text-xs text-amber-700 mt-1">Inspect identity cards (Aadhaar, Voter ID) and approve worker credentials.</p>
                    </button>

                    <button
                      onClick={() => { setJobStatusFilter('disputed'); setActiveTab('jobs'); }}
                      className="p-4 bg-red-50 border border-red-300 hover:bg-red-100 text-left transition"
                    >
                      <div className="flex items-center justify-between font-bold text-red-900 text-sm">
                        <span>Handle Contested / Disputed Jobs</span>
                        <span className="bg-red-600 text-white text-xs px-2 py-0.5">{stats?.jobs?.disputed || 0}</span>
                      </div>
                      <p className="text-xs text-red-700 mt-1">Settle escrow disputes between clients and workers with refund or payout.</p>
                    </button>

                    <button
                      onClick={() => setActiveTab('users')}
                      className="p-4 bg-blue-50 border border-blue-300 hover:bg-blue-100 text-left transition"
                    >
                      <div className="flex items-center justify-between font-bold text-blue-900 text-sm">
                        <span>User Dossier & Account Status</span>
                        <span>👥</span>
                      </div>
                      <p className="text-xs text-blue-700 mt-1">Search user directory, toggle active status, and inspect banking details.</p>
                    </button>
                  </div>
                </div>
              </>
            )}
          </div>
        )}

        {/* ======================================================== */}
        {/* TAB 2: KYC & DOCUMENT VERIFICATION */}
        {/* ======================================================== */}
        {activeTab === 'kyc' && (
          <div className="space-y-4">
            {/* Filter Bar */}
            <div className="bg-white p-4 border border-slate-300 flex flex-wrap items-center justify-between gap-4">
              <div>
                <h2 className="text-sm font-bold uppercase tracking-wider text-slate-800">Identity & Document Verification Desk</h2>
                <p className="text-xs text-slate-500">Review official identity proofs uploaded by clients and workers.</p>
              </div>

              <div className="flex items-center gap-2">
                <span className="text-xs font-bold text-slate-600 uppercase">Status Filter:</span>
                {['all', 'pending', 'verified', 'rejected'].map(st => (
                  <button
                    key={st}
                    onClick={() => setKycFilter(st)}
                    className={`px-3 py-1 text-xs font-bold uppercase border transition ${kycFilter === st
                        ? 'bg-blue-900 text-white border-blue-950'
                        : 'bg-white text-slate-700 border-slate-300 hover:bg-slate-100'
                      }`}
                  >
                    {st}
                  </button>
                ))}
              </div>
            </div>

            {/* Document Table */}
            <div className="bg-white border-2 border-slate-300 shadow-sm overflow-x-auto">
              <table className="w-full text-left border-collapse text-xs">
                <thead>
                  <tr className="bg-slate-100 text-slate-700 border-b border-slate-300 font-bold uppercase tracking-wider">
                    <th className="p-3 border-r border-slate-300">Doc ID</th>
                    <th className="p-3 border-r border-slate-300">User / Applicant</th>
                    <th className="p-3 border-r border-slate-300">Role</th>
                    <th className="p-3 border-r border-slate-300">Doc Type</th>
                    <th className="p-3 border-r border-slate-300">Status</th>
                    <th className="p-3 border-r border-slate-300">Submitted At</th>
                    <th className="p-3 text-center">Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-200">
                  {kycData.user_kyc.length === 0 && kycData.worker_docs.length === 0 ? (
                    <tr>
                      <td colSpan="7" className="p-8 text-center text-slate-500">No document submissions found matching current filter.</td>
                    </tr>
                  ) : (
                    <>
                      {kycData.user_kyc.map(item => (
                        <tr key={`user_${item.id}`} className="hover:bg-slate-50 transition">
                          <td className="p-3 font-mono font-bold text-slate-700 border-r border-slate-200">KYC-U{item.id}</td>
                          <td className="p-3 border-r border-slate-200">
                            <div className="font-bold text-slate-900">{item.name || item.username}</div>
                            <div className="text-[11px] text-slate-500 font-mono">{item.email} | {item.phone_number || 'No phone'}</div>
                          </td>
                          <td className="p-3 border-r border-slate-200 font-semibold">{item.role}</td>
                          <td className="p-3 border-r border-slate-200 font-medium text-slate-800">{item.id_type || 'Identity Card'}</td>
                          <td className="p-3 border-r border-slate-200">
                            <span className={`px-2 py-0.5 text-[10px] font-black uppercase tracking-wider border ${item.verification_status === 'verified' ? 'bg-green-100 text-green-800 border-green-300' :
                                item.verification_status === 'pending' ? 'bg-amber-100 text-amber-800 border-amber-300' :
                                  'bg-red-100 text-red-800 border-red-300'
                              }`}>
                              {item.verification_status}
                            </span>
                          </td>
                          <td className="p-3 border-r border-slate-200 text-slate-600 font-mono">{formatDate(item.submitted_at)}</td>
                          <td className="p-3 text-center">
                            <button
                              onClick={() => setSelectedDoc(item)}
                              className="px-3 py-1 bg-blue-900 hover:bg-blue-800 text-white font-bold text-xs uppercase border border-blue-950 transition"
                            >
                              Inspect & Verify 🔍
                            </button>
                          </td>
                        </tr>
                      ))}

                      {kycData.worker_docs.map(item => (
                        <tr key={`worker_doc_${item.id}`} className="hover:bg-slate-50 transition">
                          <td className="p-3 font-mono font-bold text-slate-700 border-r border-slate-200">DOC-W{item.id}</td>
                          <td className="p-3 border-r border-slate-200">
                            <div className="font-bold text-slate-900">{item.name || item.username}</div>
                            <div className="text-[11px] text-slate-500 font-mono">{item.email}</div>
                          </td>
                          <td className="p-3 border-r border-slate-200 font-semibold text-indigo-700">Worker Document</td>
                          <td className="p-3 border-r border-slate-200 font-medium text-slate-800">{item.id_type}</td>
                          <td className="p-3 border-r border-slate-200">
                            <span className={`px-2 py-0.5 text-[10px] font-black uppercase tracking-wider border ${item.verification_status === 'verified' ? 'bg-green-100 text-green-800 border-green-300' :
                                item.verification_status === 'pending' ? 'bg-amber-100 text-amber-800 border-amber-300' :
                                  'bg-red-100 text-red-800 border-red-300'
                              }`}>
                              {item.verification_status}
                            </span>
                          </td>
                          <td className="p-3 border-r border-slate-200 text-slate-600 font-mono">{formatDate(item.uploaded_at)}</td>
                          <td className="p-3 text-center">
                            <button
                              onClick={() => setSelectedDoc(item)}
                              className="px-3 py-1 bg-blue-900 hover:bg-blue-800 text-white font-bold text-xs uppercase border border-blue-950 transition"
                            >
                              Inspect & Verify 🔍
                            </button>
                          </td>
                        </tr>
                      ))}
                    </>
                  )}
                </tbody>
              </table>
            </div>

            {/* DOCUMENT INSPECTION & APPROVAL MODAL */}
            {selectedDoc && (
              <div className="fixed inset-0 bg-slate-900/60 z-50 flex items-center justify-center p-4">
                <div className="bg-white border-4 border-blue-900 max-w-4xl w-full max-h-[90vh] overflow-y-auto shadow-2xl p-6">
                  <div className="flex justify-between items-start border-b-2 border-slate-200 pb-3 mb-4">
                    <div>
                      <span className="text-[10px] font-black bg-blue-100 text-blue-900 px-2 py-0.5 uppercase tracking-wider border border-blue-200">
                        OFFICIAL DOSSIER INSPECTION
                      </span>
                      <h3 className="text-lg font-black text-slate-900 uppercase mt-1">
                        Verification Review: {selectedDoc.name || selectedDoc.username}
                      </h3>
                      <p className="text-xs text-slate-500 font-mono">
                        Username: {selectedDoc.username} | Email: {selectedDoc.email} | Phone: {selectedDoc.phone_number || 'N/A'}
                      </p>
                    </div>
                    <button
                      onClick={() => { setSelectedDoc(null); setReviewAction({ action: '', reason: '' }); }}
                      className="text-slate-400 hover:text-slate-800 font-bold text-lg px-2"
                    >
                      ✕
                    </button>
                  </div>

                  {/* Document Images Viewer */}
                  <div className="mb-6">
                    <h4 className="text-xs font-bold uppercase tracking-wider text-slate-700 mb-2">Uploaded Document Media:</h4>

                    {selectedDoc.type === 'user_kyc' ? (
                      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 bg-slate-50 p-4 border border-slate-300">
                        {/* ID Front */}
                        <div className="bg-white p-3 border border-slate-200 text-center">
                          <p className="text-[11px] font-bold text-slate-700 uppercase mb-2">Front Side Proof</p>
                          {selectedDoc.id_front_image ? (
                            <a href={selectedDoc.id_front_image.startsWith('http') ? selectedDoc.id_front_image : `${BASE_URL}${selectedDoc.id_front_image}`} target="_blank" rel="noopener noreferrer">
                              <img
                                src={selectedDoc.id_front_image.startsWith('http') ? selectedDoc.id_front_image : `${BASE_URL}${selectedDoc.id_front_image}`}
                                alt="ID Front"
                                className="w-full h-48 object-contain border bg-slate-100 hover:opacity-90"
                              />
                            </a>
                          ) : (
                            <div className="h-48 flex items-center justify-center text-xs text-slate-400 border border-dashed">No Front Image</div>
                          )}
                        </div>

                        {/* ID Back */}
                        <div className="bg-white p-3 border border-slate-200 text-center">
                          <p className="text-[11px] font-bold text-slate-700 uppercase mb-2">Back Side Proof</p>
                          {selectedDoc.id_back_image ? (
                            <a href={selectedDoc.id_back_image.startsWith('http') ? selectedDoc.id_back_image : `${BASE_URL}${selectedDoc.id_back_image}`} target="_blank" rel="noopener noreferrer">
                              <img
                                src={selectedDoc.id_back_image.startsWith('http') ? selectedDoc.id_back_image : `${BASE_URL}${selectedDoc.id_back_image}`}
                                alt="ID Back"
                                className="w-full h-48 object-contain border bg-slate-100 hover:opacity-90"
                              />
                            </a>
                          ) : (
                            <div className="h-48 flex items-center justify-center text-xs text-slate-400 border border-dashed">No Back Image</div>
                          )}
                        </div>

                        {/* ID Selfie */}
                        <div className="bg-white p-3 border border-slate-200 text-center">
                          <p className="text-[11px] font-bold text-slate-700 uppercase mb-2">Live Selfie / Face Proof</p>
                          {selectedDoc.id_selfie_image ? (
                            <a href={selectedDoc.id_selfie_image.startsWith('http') ? selectedDoc.id_selfie_image : `${BASE_URL}${selectedDoc.id_selfie_image}`} target="_blank" rel="noopener noreferrer">
                              <img
                                src={selectedDoc.id_selfie_image.startsWith('http') ? selectedDoc.id_selfie_image : `${BASE_URL}${selectedDoc.id_selfie_image}`}
                                alt="Selfie"
                                className="w-full h-48 object-contain border bg-slate-100 hover:opacity-90"
                              />
                            </a>
                          ) : (
                            <div className="h-48 flex items-center justify-center text-xs text-slate-400 border border-dashed">No Selfie Provided</div>
                          )}
                        </div>
                      </div>
                    ) : (
                      /* Worker Doc File */
                      <div className="bg-slate-50 p-6 border border-slate-300 text-center">
                        <p className="text-xs font-bold text-slate-700 mb-2">Document Type: {selectedDoc.id_type}</p>
                        {selectedDoc.file_url ? (
                          <div className="space-y-3">
                            <img
                              src={selectedDoc.file_url.startsWith('http') ? selectedDoc.file_url : `${BASE_URL}${selectedDoc.file_url}`}
                              alt="Worker Doc"
                              className="max-h-72 mx-auto object-contain border bg-white"
                              onError={(e) => { e.target.style.display = 'none'; }}
                            />
                            <a
                              href={selectedDoc.file_url.startsWith('http') ? selectedDoc.file_url : `${BASE_URL}${selectedDoc.file_url}`}
                              target="_blank"
                              rel="noopener noreferrer"
                              className="inline-block px-4 py-2 bg-blue-900 text-white font-bold text-xs uppercase"
                            >
                              Download / Open Original File ↗
                            </a>
                          </div>
                        ) : (
                          <p className="text-xs text-slate-400">No attached file found.</p>
                        )}
                      </div>
                    )}
                  </div>

                  {/* Rejection Note input if rejecting */}
                  {reviewAction.action === 'reject' && (
                    <div className="mb-4 p-4 bg-red-50 border-2 border-red-300">
                      <label className="block text-xs font-bold text-red-900 uppercase tracking-wider mb-1">
                        State Reason for Rejection <span className="text-red-600">*</span>
                      </label>
                      <textarea
                        rows="2"
                        value={reviewAction.reason}
                        onChange={(e) => setReviewAction({ ...reviewAction, reason: e.target.value })}
                        placeholder="e.g. Document image is blurred; Name on Aadhaar does not match registered user profile."
                        className="w-full p-2 text-xs border border-red-300 focus:outline-none focus:border-red-600 bg-white"
                        required
                      ></textarea>
                    </div>
                  )}

                  {/* Action Buttons */}
                  <div className="flex items-center justify-end gap-3 pt-3 border-t border-slate-200">
                    <button
                      onClick={() => { setSelectedDoc(null); setReviewAction({ action: '', reason: '' }); }}
                      className="px-4 py-2 bg-slate-200 hover:bg-slate-300 text-slate-700 text-xs font-bold uppercase"
                    >
                      Close
                    </button>

                    {reviewAction.action === 'reject' ? (
                      <button
                        onClick={() => handleKycAction(selectedDoc, 'reject', reviewAction.reason)}
                        disabled={processingKyc || !reviewAction.reason.trim()}
                        className="px-5 py-2 bg-red-700 hover:bg-red-800 text-white text-xs font-bold uppercase disabled:opacity-50"
                      >
                        {processingKyc ? "Processing..." : "Confirm & Dispatch Rejection ✕"}
                      </button>
                    ) : (
                      <button
                        onClick={() => setReviewAction({ action: 'reject', reason: '' })}
                        className="px-4 py-2 bg-red-100 hover:bg-red-200 text-red-800 text-xs font-bold uppercase border border-red-300"
                      >
                        Reject Document
                      </button>
                    )}

                    {reviewAction.action !== 'reject' && (
                      <button
                        onClick={() => handleKycAction(selectedDoc, 'approve')}
                        disabled={processingKyc}
                        className="px-6 py-2 bg-green-700 hover:bg-green-800 text-white text-xs font-bold uppercase border border-green-900 shadow-sm"
                      >
                        {processingKyc ? "Processing..." : "Approve & Mark Verified ✓"}
                      </button>
                    )}
                  </div>
                </div>
              </div>
            )}
          </div>
        )}

        {/* ======================================================== */}
        {/* TAB 3: USER MANAGEMENT */}
        {/* ======================================================== */}
        {activeTab === 'users' && (
          <div className="space-y-4">
            {/* Search & Filter Header */}
            <div className="bg-white p-4 border border-slate-300 flex flex-wrap items-center justify-between gap-4">
              <div>
                <h2 className="text-sm font-bold uppercase tracking-wider text-slate-800">User Registry & Account Directory</h2>
                <p className="text-xs text-slate-500">Manage clients, workers, staff accounts, verification status and access controls.</p>
              </div>

              {/* Filters */}
              <div className="flex flex-wrap items-center gap-3">
                <input
                  type="text"
                  placeholder="Search name, phone, email, username..."
                  value={userSearch}
                  onChange={(e) => setUserSearch(e.target.value)}
                  className="px-3 py-1.5 text-xs border border-slate-300 focus:outline-none focus:border-blue-900 w-64 bg-slate-50"
                />

                <select
                  value={userFilterRole}
                  onChange={(e) => setUserFilterRole(e.target.value)}
                  className="px-3 py-1.5 text-xs border border-slate-300 bg-white font-bold"
                >
                  <option value="all">All Roles</option>
                  <option value="client">Clients Only</option>
                  <option value="worker">Workers Only</option>
                  <option value="admin">Staff / Admins Only</option>
                </select>

                <select
                  value={userFilterVerification}
                  onChange={(e) => setUserFilterVerification(e.target.value)}
                  className="px-3 py-1.5 text-xs border border-slate-300 bg-white font-bold"
                >
                  <option value="all">All KYC Status</option>
                  <option value="verified">Verified</option>
                  <option value="pending">Pending</option>
                  <option value="rejected">Rejected</option>
                  <option value="unsubmitted">Unsubmitted</option>
                </select>

                <select
                  value={userFilterStatus}
                  onChange={(e) => setUserFilterStatus(e.target.value)}
                  className="px-3 py-1.5 text-xs border border-slate-300 bg-white font-bold"
                >
                  <option value="all">All Account Status</option>
                  <option value="active">Active Only</option>
                  <option value="inactive">Deactivated Only</option>
                </select>
              </div>
            </div>

            {/* Users Table */}
            <div className="bg-white border-2 border-slate-300 shadow-sm overflow-x-auto">
              <table className="w-full text-left border-collapse text-xs">
                <thead>
                  <tr className="bg-slate-100 text-slate-700 border-b border-slate-300 font-bold uppercase tracking-wider">
                    <th className="p-3 border-r border-slate-300">UID</th>
                    <th className="p-3 border-r border-slate-300">User Details</th>
                    <th className="p-3 border-r border-slate-300">Contact</th>
                    <th className="p-3 border-r border-slate-300">Role</th>
                    <th className="p-3 border-r border-slate-300">KYC Status</th>
                    <th className="p-3 border-r border-slate-300">Status</th>
                    <th className="p-3 border-r border-slate-300">Wallet</th>
                    <th className="p-3 text-center">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-200">
                  {usersList.length === 0 ? (
                    <tr>
                      <td colSpan="8" className="p-8 text-center text-slate-500">No users found matching current filters.</td>
                    </tr>
                  ) : (
                    usersList.map(u => (
                      <tr key={u.id} className="hover:bg-slate-50 transition">
                        <td className="p-3 font-mono font-bold text-slate-700 border-r border-slate-200">#{u.id}</td>
                        <td className="p-3 border-r border-slate-200">
                          <div className="font-bold text-slate-900">{u.first_name || u.username}</div>
                          <div className="text-[11px] text-slate-500 font-mono">@{u.username}</div>
                        </td>
                        <td className="p-3 border-r border-slate-200 font-mono text-[11px]">
                          <div>{u.email}</div>
                          <div className="text-slate-500">{u.phone_number || '—'}</div>
                        </td>
                        <td className="p-3 border-r border-slate-200">
                          {u.is_staff || u.is_superuser || u.is_admin ? (
                            <span className="bg-purple-100 text-purple-900 font-bold px-2 py-0.5 border border-purple-200">STAFF</span>
                          ) : u.is_worker ? (
                            <span className="bg-indigo-100 text-indigo-900 font-bold px-2 py-0.5 border border-indigo-200">WORKER</span>
                          ) : (
                            <span className="bg-slate-100 text-slate-800 font-bold px-2 py-0.5 border border-slate-200">CLIENT</span>
                          )}
                        </td>
                        <td className="p-3 border-r border-slate-200">
                          <span className={`px-2 py-0.5 text-[10px] font-black uppercase tracking-wider border ${u.verification_status === 'verified' ? 'bg-green-100 text-green-800 border-green-300' :
                              u.verification_status === 'pending' ? 'bg-amber-100 text-amber-800 border-amber-300' :
                                u.verification_status === 'rejected' ? 'bg-red-100 text-red-800 border-red-300' :
                                  'bg-slate-100 text-slate-600 border-slate-200'
                            }`}>
                            {u.verification_status}
                          </span>
                        </td>
                        <td className="p-3 border-r border-slate-200">
                          <span className={`px-2 py-0.5 text-[10px] font-black uppercase ${u.is_active ? 'text-green-700 bg-green-50' : 'text-red-700 bg-red-50'}`}>
                            {u.is_active ? 'ACTIVE' : 'DEACTIVATED'}
                          </span>
                        </td>
                        <td className="p-3 border-r border-slate-200 font-mono font-bold text-slate-800">
                          ₹{Number(u.wallet_balance || 0).toLocaleString('en-IN')}
                        </td>
                        <td className="p-3 text-center">
                          <button
                            onClick={() => handleInspectUser(u.id)}
                            className="px-3 py-1 bg-slate-800 hover:bg-slate-900 text-white font-bold text-xs uppercase"
                          >
                            Inspect Dossier
                          </button>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>

            {/* USER DOSSIER MODAL */}
            {selectedUserDetail && (
              <div className="fixed inset-0 bg-slate-900/60 z-50 flex items-center justify-center p-4">
                <div className="bg-white border-4 border-blue-900 max-w-3xl w-full max-h-[90vh] overflow-y-auto shadow-2xl p-6">
                  <div className="flex justify-between items-start border-b-2 border-slate-200 pb-3 mb-4">
                    <div>
                      <span className="text-[10px] font-black bg-blue-100 text-blue-900 px-2 py-0.5 uppercase tracking-wider border border-blue-200">
                        OFFICIAL USER DOSSIER #{selectedUserDetail.user.id}
                      </span>
                      <h3 className="text-xl font-black text-slate-900 mt-1">
                        {selectedUserDetail.user.first_name || selectedUserDetail.user.username}
                      </h3>
                      <p className="text-xs text-slate-500 font-mono">Registered on: {formatDate(selectedUserDetail.user.date_joined)}</p>
                    </div>
                    <button
                      onClick={() => setSelectedUserDetail(null)}
                      className="text-slate-400 hover:text-slate-800 font-bold text-lg px-2"
                    >
                      ✕
                    </button>
                  </div>

                  {/* Profile Summary Grid */}
                  <div className="grid grid-cols-2 md:grid-cols-4 gap-3 bg-slate-50 p-4 border border-slate-300 mb-4 text-xs">
                    <div>
                      <span className="text-slate-500 block uppercase text-[10px]">Username</span>
                      <strong className="font-mono text-slate-900">{selectedUserDetail.user.username}</strong>
                    </div>
                    <div>
                      <span className="text-slate-500 block uppercase text-[10px]">Email Address</span>
                      <strong className="font-mono text-slate-900">{selectedUserDetail.user.email}</strong>
                    </div>
                    <div>
                      <span className="text-slate-500 block uppercase text-[10px]">Mobile Phone</span>
                      <strong className="font-mono text-slate-900">{selectedUserDetail.user.phone_number || 'N/A'}</strong>
                    </div>
                    <div>
                      <span className="text-slate-500 block uppercase text-[10px]">Wallet Balance</span>
                      <strong className="font-mono text-blue-900">₹{selectedUserDetail.user.wallet_balance}</strong>
                    </div>
                  </div>

                  {/* Banking Info */}
                  <div className="bg-slate-50 p-4 border border-slate-300 mb-4 text-xs">
                    <h4 className="font-bold uppercase tracking-wider text-slate-700 mb-2">Linked Bank Account Details</h4>
                    <div className="grid grid-cols-3 gap-3">
                      <div><span className="text-slate-500 block uppercase text-[10px]">Bank Name</span><strong>{selectedUserDetail.user.bank_name || 'Not Linked'}</strong></div>
                      <div><span className="text-slate-500 block uppercase text-[10px]">Account Number</span><strong className="font-mono">{selectedUserDetail.user.bank_account_number || 'N/A'}</strong></div>
                      <div><span className="text-slate-500 block uppercase text-[10px]">IFSC Code</span><strong className="font-mono">{selectedUserDetail.user.bank_ifsc || 'N/A'}</strong></div>
                    </div>
                  </div>

                  {/* Administrative Controls */}
                  <div className="bg-amber-50/40 p-4 border border-amber-200 mb-4">
                    <h4 className="text-xs font-bold uppercase tracking-wider text-slate-800 mb-3">Administrative Controls</h4>
                    <div className="flex flex-wrap items-center gap-3">
                      <button
                        onClick={() => handleUpdateUserStatus(selectedUserDetail.user.id, { is_active: !selectedUserDetail.user.is_active })}
                        className={`px-4 py-2 text-xs font-bold uppercase border ${selectedUserDetail.user.is_active
                            ? 'bg-red-700 hover:bg-red-800 text-white border-red-900'
                            : 'bg-green-700 hover:bg-green-800 text-white border-green-900'
                          }`}
                      >
                        {selectedUserDetail.user.is_active ? 'Suspend / Deactivate Account' : 'Reactivate Account'}
                      </button>

                      <button
                        onClick={() => handleUpdateUserStatus(selectedUserDetail.user.id, { is_staff: !selectedUserDetail.user.is_staff })}
                        className="px-4 py-2 bg-slate-800 hover:bg-slate-900 text-white text-xs font-bold uppercase border border-slate-950"
                      >
                        {selectedUserDetail.user.is_staff ? 'Revoke Staff Access' : 'Grant Staff Authorization'}
                      </button>
                    </div>
                  </div>

                  {/* Associated Jobs */}
                  <div className="space-y-3">
                    <h4 className="text-xs font-bold uppercase tracking-wider text-slate-700">Recent Associated Activity</h4>
                    <div className="text-xs text-slate-600">
                      <p>• Posted Jobs: <strong>{selectedUserDetail.posted_jobs.length}</strong></p>
                      <p>• Accepted Work Assignments: <strong>{selectedUserDetail.taken_jobs.length}</strong></p>
                      <p>• Active Trade Profiles: <strong>{selectedUserDetail.trade_profiles.length}</strong></p>
                      <p>• Reviews Received: <strong>{selectedUserDetail.reviews_received.length}</strong></p>
                    </div>
                  </div>

                  <div className="mt-6 pt-3 border-t border-slate-200 text-right">
                    <button
                      onClick={() => setSelectedUserDetail(null)}
                      className="px-4 py-2 bg-slate-200 hover:bg-slate-300 text-slate-700 text-xs font-bold uppercase"
                    >
                      Close Dossier
                    </button>
                  </div>
                </div>
              </div>
            )}
          </div>
        )}

        {/* ======================================================== */}
        {/* TAB 4: JOBS & DISPUTES */}
        {/* ======================================================== */}
        {activeTab === 'jobs' && (
          <div className="space-y-4">
            {/* Filter Bar */}
            <div className="bg-white p-4 border border-slate-300 flex flex-wrap items-center justify-between gap-4">
              <div>
                <h2 className="text-sm font-bold uppercase tracking-wider text-slate-800">Job Marketplace & Dispute Settlement Desk</h2>
                <p className="text-xs text-slate-500">Monitor all platform service jobs, escrow states, and resolve contested orders.</p>
              </div>

              <div className="flex flex-wrap items-center gap-3">
                <input
                  type="text"
                  placeholder="Search job title, client, worker, location..."
                  value={jobSearch}
                  onChange={(e) => setJobSearch(e.target.value)}
                  className="px-3 py-1.5 text-xs border border-slate-300 focus:outline-none focus:border-blue-900 w-64 bg-slate-50"
                />

                <select
                  value={jobStatusFilter}
                  onChange={(e) => setJobStatusFilter(e.target.value)}
                  className="px-3 py-1.5 text-xs border border-slate-300 bg-white font-bold"
                >
                  <option value="all">All Statuses</option>
                  <option value="disputed">⚠️ Disputed Only</option>
                  <option value="pending">Pending</option>
                  <option value="accepted">Accepted / In Progress</option>
                  <option value="worker_completed">Worker Completed</option>
                  <option value="completed">Completed</option>
                  <option value="declined">Declined / Cancelled</option>
                </select>
              </div>
            </div>

            {/* Jobs Table */}
            <div className="bg-white border-2 border-slate-300 shadow-sm overflow-x-auto">
              <table className="w-full text-left border-collapse text-xs">
                <thead>
                  <tr className="bg-slate-100 text-slate-700 border-b border-slate-300 font-bold uppercase tracking-wider">
                    <th className="p-3 border-r border-slate-300">Job ID</th>
                    <th className="p-3 border-r border-slate-300">Title & Service</th>
                    <th className="p-3 border-r border-slate-300">Client</th>
                    <th className="p-3 border-r border-slate-300">Assigned Worker</th>
                    <th className="p-3 border-r border-slate-300">Budget</th>
                    <th className="p-3 border-r border-slate-300">Status</th>
                    <th className="p-3 border-r border-slate-300">Escrow State</th>
                    <th className="p-3 text-center">Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-200">
                  {jobsData.jobs.length === 0 ? (
                    <tr>
                      <td colSpan="8" className="p-8 text-center text-slate-500">No jobs found matching current filters.</td>
                    </tr>
                  ) : (
                    jobsData.jobs.map(job => (
                      <tr key={job.id} className={`hover:bg-slate-50 transition ${job.status === 'disputed' ? 'bg-red-50/40' : ''}`}>
                        <td className="p-3 font-mono font-bold text-slate-700 border-r border-slate-200">#JOB-{job.id}</td>
                        <td className="p-3 border-r border-slate-200">
                          <div className="font-bold text-slate-900">{job.title}</div>
                          <div className="text-[11px] text-slate-500">{job.service_type} | {job.address}</div>
                        </td>
                        <td className="p-3 border-r border-slate-200 font-mono text-[11px]">
                          <strong>{job.client.name || job.client.username}</strong>
                          <div className="text-slate-500">{job.client.phone}</div>
                        </td>
                        <td className="p-3 border-r border-slate-200 font-mono text-[11px]">
                          {job.worker ? (
                            <>
                              <strong>{job.worker.name || job.worker.username}</strong>
                              <div className="text-slate-500">{job.worker.phone}</div>
                            </>
                          ) : (
                            <span className="text-slate-400 italic">Unassigned</span>
                          )}
                        </td>
                        <td className="p-3 border-r border-slate-200 font-mono font-bold text-slate-900">
                          ₹{job.budget}
                        </td>
                        <td className="p-3 border-r border-slate-200">
                          <span className={`px-2 py-0.5 text-[10px] font-black uppercase tracking-wider border ${job.status === 'completed' ? 'bg-green-100 text-green-800 border-green-300' :
                              job.status === 'disputed' ? 'bg-red-100 text-red-800 border-red-300' :
                                job.status === 'worker_completed' ? 'bg-indigo-100 text-indigo-800 border-indigo-300' :
                                  job.status === 'accepted' ? 'bg-blue-100 text-blue-800 border-blue-300' :
                                    'bg-slate-100 text-slate-700 border-slate-300'
                            }`}>
                            {job.status}
                          </span>
                        </td>
                        <td className="p-3 border-r border-slate-200">
                          <span className={`px-2 py-0.5 text-[10px] font-bold uppercase ${job.escrow_status === 'held' ? 'text-amber-800 bg-amber-50 border border-amber-200' :
                              job.escrow_status === 'released' ? 'text-green-800 bg-green-50 border border-green-200' :
                                job.escrow_status === 'refunded' ? 'text-blue-800 bg-blue-50 border border-blue-200' :
                                  'text-slate-500'
                            }`}>
                            {job.escrow_status}
                          </span>
                        </td>
                        <td className="p-3 text-center">
                          {job.status === 'disputed' ? (
                            <button
                              onClick={() => { setSelectedDisputeJob(job); setDisputeActionData({ action: 'release_to_worker', note: '' }); }}
                              className="px-3 py-1 bg-red-700 hover:bg-red-800 text-white font-bold text-xs uppercase border border-red-900 animate-pulse"
                            >
                              Resolve Dispute ⚖️
                            </button>
                          ) : (
                            <button
                              onClick={() => { setSelectedDisputeJob(job); setDisputeActionData({ action: 'force_complete', note: '' }); }}
                              className="px-3 py-1 bg-slate-800 hover:bg-slate-900 text-white font-bold text-xs uppercase"
                            >
                              Manage Job
                            </button>
                          )}
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>

            {/* DISPUTE & JOB RESOLUTION MODAL */}
            {selectedDisputeJob && (
              <div className="fixed inset-0 bg-slate-900/60 z-50 flex items-center justify-center p-4">
                <div className="bg-white border-4 border-blue-900 max-w-2xl w-full shadow-2xl p-6">
                  <div className="flex justify-between items-start border-b-2 border-slate-200 pb-3 mb-4">
                    <div>
                      <span className="text-[10px] font-black bg-red-100 text-red-900 px-2 py-0.5 uppercase tracking-wider border border-red-200">
                        ADMINISTRATIVE DISPUTE RESOLUTION
                      </span>
                      <h3 className="text-lg font-black text-slate-900 uppercase mt-1">
                        Job #{selectedDisputeJob.id}: {selectedDisputeJob.title}
                      </h3>
                      <p className="text-xs text-slate-500 font-mono">Escrow Amount: ₹{selectedDisputeJob.budget} | Escrow Status: {selectedDisputeJob.escrow_status}</p>
                    </div>
                    <button
                      onClick={() => setSelectedDisputeJob(null)}
                      className="text-slate-400 hover:text-slate-800 font-bold text-lg px-2"
                    >
                      ✕
                    </button>
                  </div>

                  {/* Dispute Details */}
                  <div className="bg-slate-50 p-4 border border-slate-300 mb-4 text-xs space-y-2">
                    <p><strong>Job Description:</strong> {selectedDisputeJob.description}</p>
                    <p><strong>Location:</strong> {selectedDisputeJob.address}</p>
                    <div className="grid grid-cols-2 gap-4 pt-2 border-t border-slate-200">
                      <div>
                        <span className="text-slate-500 block uppercase text-[10px]">Client</span>
                        <strong>{selectedDisputeJob.client.name || selectedDisputeJob.client.username}</strong> ({selectedDisputeJob.client.phone})
                      </div>
                      <div>
                        <span className="text-slate-500 block uppercase text-[10px]">Worker</span>
                        <strong>{selectedDisputeJob.worker ? (selectedDisputeJob.worker.name || selectedDisputeJob.worker.username) : 'None'}</strong>
                      </div>
                    </div>
                  </div>

                  {/* Resolution Form */}
                  <div className="space-y-4">
                    <div>
                      <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                        Select Administrative Ruling <span className="text-red-600">*</span>
                      </label>
                      <select
                        value={disputeActionData.action}
                        onChange={(e) => setDisputeActionData({ ...disputeActionData, action: e.target.value })}
                        className="w-full p-2 text-xs border border-slate-300 font-bold bg-white"
                      >
                        <option value="release_to_worker">Release Escrow (₹{selectedDisputeJob.budget}) to Worker & Mark Completed</option>
                        <option value="refund_to_client">Refund Full Escrow (₹{selectedDisputeJob.budget}) to Client Wallet</option>
                        <option value="force_complete">Force Mark Job Completed</option>
                        <option value="cancel">Cancel Order</option>
                      </select>
                    </div>

                    <div>
                      <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                        Official Ruling Remark / Notes
                      </label>
                      <textarea
                        rows="2"
                        value={disputeActionData.note}
                        onChange={(e) => setDisputeActionData({ ...disputeActionData, note: e.target.value })}
                        placeholder="e.g. Verified client photos showing incomplete work; full refund authorized per platform policy."
                        className="w-full p-2 text-xs border border-slate-300 bg-white"
                      ></textarea>
                    </div>
                  </div>

                  {/* Modal Footer */}
                  <div className="flex items-center justify-end gap-3 pt-4 mt-4 border-t border-slate-200">
                    <button
                      onClick={() => setSelectedDisputeJob(null)}
                      className="px-4 py-2 bg-slate-200 hover:bg-slate-300 text-slate-700 text-xs font-bold uppercase"
                    >
                      Cancel
                    </button>
                    <button
                      onClick={handleResolveDispute}
                      disabled={processingDispute}
                      className="px-6 py-2 bg-blue-900 hover:bg-blue-800 text-white text-xs font-bold uppercase border border-blue-950 shadow-sm"
                    >
                      {processingDispute ? "Executing Ruling..." : "Execute Official Ruling ⚖️"}
                    </button>
                  </div>
                </div>
              </div>
            )}
          </div>
        )}

        {/* ======================================================== */}
        {/* TAB 5: TRADE PROFILES */}
        {/* ======================================================== */}
        {activeTab === 'trade_profiles' && (
          <div className="space-y-4">
            <div className="bg-white p-4 border border-slate-300 flex flex-wrap items-center justify-between gap-4">
              <div>
                <h2 className="text-sm font-bold uppercase tracking-wider text-slate-800">Worker Trade Profiles Directory</h2>
                <p className="text-xs text-slate-500">Review worker public trade listings, skills, and activation status.</p>
              </div>

              <div>
                <select
                  value={tradeCategoryFilter}
                  onChange={(e) => setTradeCategoryFilter(e.target.value)}
                  className="px-3 py-1.5 text-xs border border-slate-300 bg-white font-bold"
                >
                  <option value="all">All Trade Categories</option>
                  <option value="Plumbing">Plumbing</option>
                  <option value="Carpentry">Carpentry</option>
                  <option value="Electrical Work">Electrical Work</option>
                  <option value="Painting">Painting</option>
                  <option value="Cleaning / Deep Clean">Cleaning / Deep Clean</option>
                  <option value="Appliance Repair">Appliance Repair</option>
                </select>
              </div>
            </div>

            <div className="bg-white border-2 border-slate-300 shadow-sm overflow-x-auto">
              <table className="w-full text-left border-collapse text-xs">
                <thead>
                  <tr className="bg-slate-100 text-slate-700 border-b border-slate-300 font-bold uppercase tracking-wider">
                    <th className="p-3 border-r border-slate-300">Profile ID</th>
                    <th className="p-3 border-r border-slate-300">Display Name</th>
                    <th className="p-3 border-r border-slate-300">Worker User</th>
                    <th className="p-3 border-r border-slate-300">Category</th>
                    <th className="p-3 border-r border-slate-300">Experience</th>
                    <th className="p-3 border-r border-slate-300">Skills</th>
                    <th className="p-3 border-r border-slate-300">Active State</th>
                    <th className="p-3 text-center">Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-200">
                  {tradeProfilesList.length === 0 ? (
                    <tr>
                      <td colSpan="8" className="p-8 text-center text-slate-500">No trade profiles found.</td>
                    </tr>
                  ) : (
                    tradeProfilesList.map(p => (
                      <tr key={p.id} className="hover:bg-slate-50 transition">
                        <td className="p-3 font-mono font-bold text-slate-700 border-r border-slate-200">#TP-{p.id}</td>
                        <td className="p-3 border-r border-slate-200 font-bold text-slate-900">{p.display_name}</td>
                        <td className="p-3 border-r border-slate-200 font-mono text-slate-600">ID: {p.worker}</td>
                        <td className="p-3 border-r border-slate-200 font-medium text-indigo-800">{p.trade_category}</td>
                        <td className="p-3 border-r border-slate-200 font-mono">{p.years_of_experience} Years</td>
                        <td className="p-3 border-r border-slate-200 max-w-xs truncate text-slate-600">{p.skills}</td>
                        <td className="p-3 border-r border-slate-200">
                          <span className={`px-2 py-0.5 text-[10px] font-black uppercase ${p.is_active ? 'text-green-700 bg-green-50 border border-green-200' : 'text-slate-500 bg-slate-100'}`}>
                            {p.is_active ? 'ACTIVE' : 'INACTIVE'}
                          </span>
                        </td>
                        <td className="p-3 text-center">
                          <button
                            onClick={() => handleToggleTradeProfile(p.id, p.is_active)}
                            className={`px-3 py-1 text-xs font-bold uppercase border ${p.is_active
                                ? 'bg-red-50 text-red-700 border-red-300 hover:bg-red-100'
                                : 'bg-green-50 text-green-700 border-green-300 hover:bg-green-100'
                              }`}
                          >
                            {p.is_active ? 'Deactivate' : 'Activate'}
                          </button>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* ======================================================== */}
        {/* TAB 6: REVIEWS & MODERATION */}
        {/* ======================================================== */}
        {activeTab === 'reviews' && (
          <div className="space-y-4">
            <div className="bg-white p-4 border border-slate-300 flex items-center justify-between">
              <div>
                <h2 className="text-sm font-bold uppercase tracking-wider text-slate-800">Platform Reviews & Moderation</h2>
                <p className="text-xs text-slate-500">Inspect client and worker feedback, ratings, and remove abusive or false reviews.</p>
              </div>
            </div>

            <div className="bg-white border-2 border-slate-300 shadow-sm overflow-x-auto">
              <table className="w-full text-left border-collapse text-xs">
                <thead>
                  <tr className="bg-slate-100 text-slate-700 border-b border-slate-300 font-bold uppercase tracking-wider">
                    <th className="p-3 border-r border-slate-300">Review ID</th>
                    <th className="p-3 border-r border-slate-300">Job Reference</th>
                    <th className="p-3 border-r border-slate-300">Reviewer</th>
                    <th className="p-3 border-r border-slate-300">Target User</th>
                    <th className="p-3 border-r border-slate-300">Rating</th>
                    <th className="p-3 border-r border-slate-300">Comment</th>
                    <th className="p-3 border-r border-slate-300">Date</th>
                    <th className="p-3 text-center">Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-200">
                  {reviewsList.length === 0 ? (
                    <tr>
                      <td colSpan="8" className="p-8 text-center text-slate-500">No customer reviews submitted yet.</td>
                    </tr>
                  ) : (
                    reviewsList.map(r => (
                      <tr key={r.id} className="hover:bg-slate-50 transition">
                        <td className="p-3 font-mono font-bold text-slate-700 border-r border-slate-200">#REV-{r.id}</td>
                        <td className="p-3 border-r border-slate-200 font-mono text-slate-600">Job #{r.job_id} ({r.job_title})</td>
                        <td className="p-3 border-r border-slate-200 font-bold text-slate-900">{r.reviewer_name || r.reviewer_username}</td>
                        <td className="p-3 border-r border-slate-200 text-slate-800">{r.target_name || r.target_username}</td>
                        <td className="p-3 border-r border-slate-200 font-bold text-amber-600">
                          {'★'.repeat(r.rating)}{'☆'.repeat(5 - r.rating)} ({r.rating}/5)
                        </td>
                        <td className="p-3 border-r border-slate-200 max-w-sm text-slate-700">{r.comment || '<No comment>'}</td>
                        <td className="p-3 border-r border-slate-200 font-mono text-slate-500">{formatDate(r.created_at)}</td>
                        <td className="p-3 text-center">
                          <button
                            onClick={() => handleDeleteReview(r.id)}
                            className="px-3 py-1 bg-red-100 hover:bg-red-200 text-red-800 font-bold text-xs uppercase border border-red-300 transition"
                          >
                            Delete 🗑️
                          </button>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        )}

      </main>

      {/* 4. OFFICIAL FOOTER */}
      <footer className="bg-slate-800 text-slate-400 text-xs py-3 px-8 text-center border-t border-slate-700 mt-auto">
        <p>© 2026 SAHAYA Community Services – Administrative Services Division. All actions logged.</p>
      </footer>
    </div>
  );
}
