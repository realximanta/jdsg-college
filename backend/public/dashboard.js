const API_BASE = "https://api.jdsgcollege.dpdns.org";
AOS.init();

// 1. Auth Guard & Load User Data
const token = localStorage.getItem("jdsg_token");
const userStr = localStorage.getItem("jdsg_user");

if (!token || !userStr) {
    window.location.href = "index.html";
} else {
    const user = JSON.parse(userStr);
    
    // Populate Profile
    document.getElementById("profile-name").innerText = user.full_name || "User";
    document.getElementById("profile-role").innerText = user.role || "Student";
    document.getElementById("profile-phone").innerText = user.phone || "-";
    
    // Update Avatar with fallback
    const avatarUrl = user.profile_pic_url 
        ? user.profile_pic_url 
        : `https://ui-avatars.com/api/?name=${encodeURIComponent(user.full_name || "User")}&background=1E3A8A&color=fff`;
    document.getElementById("profile-img").src = avatarUrl;

    // Role-Specific Details
    if (user.role === "student") {
        document.getElementById("detail-label-1").innerText = "Class";
        document.getElementById("detail-val-1").innerText = user.class_name || "N/A";
        document.getElementById("detail-label-2").innerText = "Semester";
        document.getElementById("detail-val-2").innerText = user.semester || "N/A";
    } else {
        document.getElementById("detail-label-1").innerText = "Department";
        document.getElementById("detail-val-1").innerText = user.department || "N/A";
        document.getElementById("detail-label-2").innerText = "Designation";
        document.getElementById("detail-val-2").innerText = user.designation || "N/A";
    }
}

// 2. Fetch Notifications
async function fetchNotifications() {
    const loading = document.getElementById("notif-loading");
    const error = document.getElementById("notif-error");
    const list = document.getElementById("notif-list");

    loading.classList.remove("hidden");
    error.classList.add("hidden");
    list.classList.add("hidden");

    try {
        const res = await fetch(`${API_BASE}/api/notifications`);
        if (!res.ok) throw new Error("Server error");
        
        const notifications = await res.json();
        
        loading.classList.add("hidden");
        
        if (notifications.length === 0) {
            list.innerHTML = `<p class="text-slate-500 text-center py-8">No new notifications.</p>`;
            list.classList.remove("hidden");
            return;
        }

        // Render Cards
        list.innerHTML = notifications.map((n, index) => `
            <div class="bg-white p-6 rounded-xl shadow-sm border border-slate-100 hover:shadow-md transition duration-300" data-aos="fade-up" data-aos-delay="${index * 100}">
                <div class="flex justify-between items-start mb-2">
                    <span class="text-xs font-bold text-tealaccent uppercase tracking-wider bg-tealaccent/10 px-2 py-1 rounded">Notice</span>
                    <span class="text-xs text-slate-400 font-medium">${n.date}</span>
                </div>
                <h3 class="text-lg font-bold text-slate-800 mb-4 leading-tight">${n.title}</h3>
                <a href="${n.url}" target="_blank" class="inline-flex items-center text-institutional font-semibold hover:text-blue-900 text-sm">
                    Read Full Notice 
                    <svg class="w-4 h-4 ml-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 5l7 7m0 0l-7 7m7-7H3"></path></svg>
                </a>
            </div>
        `).join('');

        list.classList.remove("hidden");

    } catch (err) {
        console.error(err);
        loading.classList.add("hidden");
        error.classList.remove("hidden");
    }
}

// 3. Logout
function logout() {
    localStorage.removeItem("jdsg_token");
    localStorage.removeItem("jdsg_user");
    window.location.href = "index.html";
}

// Initialize
fetchNotifications();
