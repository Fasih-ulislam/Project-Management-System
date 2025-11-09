
document.addEventListener("DOMContentLoaded", () => {
    const logoutLink = document.getElementById("logoutLink");
  
    if (logoutLink) {
      logoutLink.addEventListener("click", (e) => {
        e.preventDefault(); // Prevent navigation
  
        const confirmLogout = confirm("Are you sure you want to log out?");
        if (confirmLogout) {
          localStorage.clear(); // Clear auth and other stored data
          window.location.href = "login.html"; // Redirect to login page
        }
      });
    }
  });
  