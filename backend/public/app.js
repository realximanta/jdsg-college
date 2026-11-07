const API_BASE = "https://api.jdsgcollege.dpdns.org";
let tempToken = "";
let tempEmail = "";
let tempName = "";

AOS.init();

// 1. Handle Google Login Response
async function handleCredentialResponse(response) {
    const idToken = response.credential;
    const btnSection = document.getElementById('google-login-section');
    
    // Show loading state
    btnSection.innerHTML = `<p class="text-institutional font-semibold animate-pulse">Verifying with server...</p>`;

    try {
        const res = await fetch(`${API_BASE}/api/auth/google`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ id_token: idToken })
        });

        if (!res.ok) throw new Error("Server rejected token");
        
        const data = await res.json();

        if (data.exists === true) {
            // Existing user, save token and redirect
            localStorage.setItem("jdsg_token", data.token);
            localStorage.setItem("jdsg_user", JSON.stringify(data.user));
            window.location.href = "dashboard.html";
        } else {
            // New user, show signup form
            tempToken = data.token;
            tempEmail = data.email;
            tempName = data.name;
            
            document.getElementById('google-login-section').classList.add('hidden');
            document.getElementById('signup-section').classList.remove('hidden');
            document.getElementById('signup-email').innerText = data.email;
            document.getElementById('full_name').value = data.name;
        }
    } catch (error) {
        console.error(error);
        btnSection.innerHTML = `<p class="text-red-500">Connection failed. Backend might be waking up. Please try again.</p>`;
    }
}

// 2. Handle Signup Form Submission
document.getElementById('signup-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const submitBtn = e.target.querySelector('button[type="submit"]');
    submitBtn.innerText = "Processing...";
    submitBtn.disabled = true;

    const fullName = document.getElementById('full_name').value;
    const role = document.getElementById('role').value;
    const phone = document.getElementById('phone').value;
    const file = document.getElementById('avatar').files[0];

    if (phone.length !== 10) {
        alert("Phone number must be 10 digits");
        submitBtn.innerText = "Complete Sign Up";
        submitBtn.disabled = false;
        return;
    }

    try {
        // Step 1: Create User Profile (JSON)
        const userPayload = {
            id: "", email: tempEmail, full_name: fullName, role: role,
            phone: phone, profile_pic_url: "", 
            class_name: role === 'student' ? "B.A." : null, // Simplified for web
            department: role === 'teacher' ? "General" : null
        };

        const profileRes = await fetch(`${API_BASE}/api/users/me`, {
            method: 'PUT',
            headers: { 
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${tempToken}` 
            },
            body: JSON.stringify(userPayload)
        });

        if (!profileRes.ok) throw new Error("Failed to create profile");

        // Step 2: Upload Avatar
        const formData = new FormData();
        formData.append("avatar", file);

        const avatarRes = await fetch(`${API_BASE}/api/users/me/avatar`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${tempToken}` },
            body: formData
        });

        if (!avatarRes.ok) throw new Error("Failed to upload image");
        
        const finalUser = await avatarRes.json();
        
        // Save and redirect
        localStorage.setItem("jdsg_token", tempToken);
        localStorage.setItem("jdsg_user", JSON.stringify(finalUser));
        window.location.href = "dashboard.html";

    } catch (error) {
        console.error(error);
        alert("Error creating profile. Please check your connection.");
        submitBtn.innerText = "Complete Sign Up";
        submitBtn.disabled = false;
    }
});
