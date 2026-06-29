const API = '/api/v1';

function getToken() {
    return localStorage.getItem('token');
}

function isLoggedIn() {
    return !!getToken();
}

function getUser() {
    const u = localStorage.getItem('user');
    return u ? JSON.parse(u) : null;
}

function hasRole(role) {
    const user = getUser();
    return user && user.role && user.role === role;
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    window.location.href = '/index.html';
}

function authHeaders() {
    const t = getToken();
    const h = {};
    if (t) h['Authorization'] = 'Bearer ' + t;
    return h;
}

async function apiFetch(url, opts = {}) {
    opts.headers = { 'Content-Type': 'application/json', ...authHeaders(), ...opts.headers };
    const res = await fetch(API + url, opts);
    if (!res.ok) {
        let msg;
        try { const e = await res.json(); msg = e.message || e.error || 'Request failed'; }
        catch { msg = res.statusText; }
        throw new Error(msg);
    }
    const text = await res.text();
    if (!text) return null;
    const json = JSON.parse(text);
    return json.data !== undefined ? json.data : json;
}

async function apiUpload(url, formData) {
    const h = authHeaders();
    const res = await fetch(API + url, { method: 'POST', headers: h, body: formData });
    if (!res.ok) {
        let msg;
        try { const e = await res.json(); msg = e.message || e.error || 'Upload failed'; }
        catch { msg = res.statusText; }
        throw new Error(msg);
    }
    const text = await res.text();
    if (!text) return null;
    const json = JSON.parse(text);
    return json.data !== undefined ? json.data : json;
}

async function apiPost(url, body) {
    return apiFetch(url, { method: 'POST', body: JSON.stringify(body) });
}

async function apiPut(url, body) {
    return apiFetch(url, { method: 'PUT', body: JSON.stringify(body) });
}

async function apiPatch(url, body) {
    return apiFetch(url, { method: 'PATCH', body: JSON.stringify(body) });
}

async function apiDelete(url) {
    return apiFetch(url, { method: 'DELETE' });
}

/* ── Toast notifications ── */
function showToast(msg, type = 'error') {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        container.className = 'toast-container';
        document.body.appendChild(container);
    }
    const el = document.createElement('div');
    el.className = 'toast toast-' + type;
    el.textContent = msg;
    container.appendChild(el);
    setTimeout(() => {
        el.style.animation = 'toastOut 0.3s ease forwards';
        setTimeout(() => el.remove(), 300);
    }, 4000);
}

/* ── Alert (backward compat) ── */
function showAlert(msg, type = 'error') {
    showToast(msg, type);
    const el = document.getElementById('alert');
    if (el) {
        el.innerHTML = '<div class="alert alert-' + type + '">' + msg + '</div>';
    }
}

/* ── Skeleton loading ── */
function showSkeleton(containerId, count = 3) {
    const el = document.getElementById(containerId);
    if (!el) return;
    el.innerHTML = '';
    for (let i = 0; i < count; i++) {
        el.innerHTML += '<div class="skeleton skeleton-card"><div class="skeleton-line"></div><div class="skeleton-line"></div><div class="skeleton-line"></div></div>';
    }
}

/* ── Navbar ── */
function showNavbar() {
    const nav = document.getElementById('navbar');
    if (!nav) return;
    const user = getUser();
    if (user && isLoggedIn()) {
        const isRecruiter = hasRole('RECRUITER');
        const isApplicant = hasRole('APPLICANT');
        nav.innerHTML = `
            <a href="/index.html" class="logo">JobPortal</a>
            <button class="nav-toggle" onclick="toggleNav()">☰</button>
            <div class="nav-links" id="nav-links">
                <a href="/messages.html" onclick="closeNav()">Messages</a>
                ${isRecruiter ? '<a href="/post-job.html" onclick="closeNav()">Post Job</a><a href="/my-company.html" onclick="closeNav()">Company</a>' : '<a href="/index.html" onclick="closeNav()">Jobs</a>'}
                ${isApplicant ? '<a href="/my-cvs.html" onclick="closeNav()">My CVs</a><a href="/my-applications.html" onclick="closeNav()">Applications</a>' : ''}
                <span class="nav-user">${user.fullName || user.fullname || user.email}</span>
                <a class="nav-logout" onclick="logout()">Logout</a>
            </div>`;
    } else {
        nav.innerHTML = `
            <a href="/index.html" class="logo">JobPortal</a>
            <button class="nav-toggle" onclick="toggleNav()">☰</button>
            <div class="nav-links" id="nav-links">
                <a href="/index.html" onclick="closeNav()">Jobs</a>
                <a href="/login.html" onclick="closeNav()">Login</a>
                <a href="/register.html" onclick="closeNav()">Register</a>
            </div>`;
    }
}

function toggleNav() {
    document.getElementById('nav-links')?.classList.toggle('open');
}

function closeNav() {
    document.getElementById('nav-links')?.classList.remove('open');
}

/* ── Format salary ── */
function formatSalary(min, max, currency) {
    const fmt = (v) => {
        if (v >= 1000000) return (v / 1000000).toFixed(0) + 'M';
        if (v >= 1000) return (v / 1000).toFixed(0) + 'K';
        return v;
    };
    const sym = currency === 'USD' ? '$' : (currency === 'VND' ? '₫' : '');
    if (max && max !== min) return sym + fmt(min) + ' - ' + sym + fmt(max);
    return sym + fmt(min);
}

/* ── Format date ── */
function formatDate(dateStr) {
    if (!dateStr) return '';
    const d = new Date(dateStr);
    return d.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
}

async function updateUnreadBadge() {
    const badge = document.getElementById('unread-badge');
    if (!badge) return;
    if (!isLoggedIn()) { badge.textContent = ''; return; }
    try {
        const count = await apiFetch('/messages/unread-count');
        badge.textContent = count > 0 ? count : '';
    } catch { badge.textContent = ''; }
}

/* ── STOMP / WebSocket ── */
let stompClient = null;
let stompConnected = false;

function stompConnect() {
    if (stompConnected || !isLoggedIn() || typeof SockJS === 'undefined') return;
    const token = getToken();
    if (!token) return;
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({ 'Authorization': 'Bearer ' + token }, () => {
        stompConnected = true;
        stompClient.subscribe('/user/queue/messages', (msg) => {
            try {
                const body = JSON.parse(msg.body);
                if (body === 'mark-read') { updateUnreadBadge(); return; }
                if (body && body.senderId && window.onNewMessage) {
                    window.onNewMessage(body);
                }
                updateUnreadBadge();
            } catch {}
        });
    }, () => { stompConnected = false; });
}

function stompDisconnect() {
    if (stompClient && stompConnected) {
        stompClient.disconnect(() => { stompConnected = false; });
    }
}

document.addEventListener('DOMContentLoaded', () => {
    showNavbar();
    if (isLoggedIn()) {
        updateUnreadBadge();
        if (typeof SockJS !== 'undefined') {
            setTimeout(stompConnect, 1000);
        }
    }
});
