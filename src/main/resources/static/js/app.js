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
    return t ? { 'Authorization': 'Bearer ' + t, 'Content-Type': 'application/json' }
             : { 'Content-Type': 'application/json' };
}

async function apiFetch(url, opts = {}) {
    opts.headers = { ...authHeaders(), ...opts.headers };
    const res = await fetch(API + url, opts);
    if (!res.ok) {
        const err = await res.json().catch(() => ({ error: res.statusText }));
        throw new Error(err.message || err.error || 'Request failed');
    }
    const text = await res.text();
    if (!text) return null;
    const json = JSON.parse(text);
    return json.data !== undefined ? json.data : json;
}

async function apiUpload(url, formData) {
    const t = getToken();
    const headers = t ? { 'Authorization': 'Bearer ' + t } : {};
    const res = await fetch(API + url, { method: 'POST', headers, body: formData });
    if (!res.ok) {
        const err = await res.json().catch(() => ({ error: res.statusText }));
        throw new Error(err.message || err.error || 'Upload failed');
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

function showAlert(msg, type = 'error') {
    const el = document.getElementById('alert');
    if (!el) return;
    el.innerHTML = '<div class="alert alert-' + type + '">' + msg + '</div>';
}

function showNavbar() {
    const nav = document.getElementById('navbar');
    if (!nav) return;
    const user = getUser();
    if (user && isLoggedIn()) {
        const isRecruiter = hasRole('RECRUITER');
        const isApplicant = hasRole('APPLICANT');
        nav.innerHTML = `
            <a href="/index.html" class="logo">JobPortal</a>
            <div class="nav-links">
                <a href="/index.html">Jobs</a>
                ${isRecruiter ? '<a href="/my-jobs.html">My Jobs</a><a href="/post-job.html">Post Job</a><a href="/my-company.html">My Company</a>' : ''}
                ${isApplicant ? '<a href="/my-cvs.html">My CVs</a><a href="/my-applications.html">My Applications</a>' : ''}
                <span>Hi, ${user.fullName || user.fullname}</span>
                <a onclick="logout()">Logout</a>
            </div>`;
    } else {
        nav.innerHTML = `
            <a href="/index.html" class="logo">JobPortal</a>
            <div class="nav-links">
                <a href="/index.html">Jobs</a>
                <a href="/login.html">Login</a>
                <a href="/register.html">Register</a>
            </div>`;
    }
}

document.addEventListener('DOMContentLoaded', showNavbar);
