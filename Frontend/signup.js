document.getElementById("signupForm").addEventListener("submit", function(event) {
    event.preventDefault();
  
    const username = document.getElementById("username").value.trim();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirmPassword").value;
  
    if (password !== confirmPassword) {
      alert("Passwords do not match!");
      return;
    }
  
    const payload = {
      username,
      email,
      password
    };
  
    console.log("Sending data:", payload);
  
    fetch("http://localhost:8080/users", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(payload)
    })
    .then(response => {
        if(response.status === 400){
             throw new Error("Username already exists");
        }
      if (!response.ok){
         throw new Error("Signup failed ");
      }
      return response.json();
    })
    .then(data => {
      alert("Signup successful!");
      window.location.href = "login.html";
    })
    .catch(error => {
      alert("Error: " + error.message);
    });
  });
  