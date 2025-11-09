document.getElementById("loginForm").addEventListener("submit", async function (e) {
    e.preventDefault();
  
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
  
    const data = { username, password };
  
    try {
      const response = await fetch("http://localhost:8080/users/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
      });
  
      if (response.ok) {
        const result = await response.json();
  
        localStorage.setItem("token", result.token);
        localStorage.setItem("username", result.username);
  
        // Store roles array
        const rolesArray = result.roles.split(","); // Convert CSV to array
        localStorage.setItem("roles", JSON.stringify(rolesArray)); // Save as JSON array
  
        alert("Login successful!");

        console.log("Roles stored:", rolesArray);
  
        // Optional: Redirect
         window.location.href = "/members.html";
  
      } if (response.status === 401) {
        const errorMessage = await response.json(); // Read the error message
        alert("Login failed: " + errorMessage.message);
        return;
      }    
    } catch (err) {
      console.error("Network error:", err);
      alert("Could not connect to the server.");
    }
  });
  
