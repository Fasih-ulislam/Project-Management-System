document.addEventListener('DOMContentLoaded', () => {
  const token = localStorage.getItem("token");
if(!token){
    window.location.href = "login.html";
}

  const buttons = document.querySelectorAll('.nav-button');
  const forms = document.querySelectorAll('.form-section form');
  const infoBox = document.getElementById('member-info-box');

  function showForm(formId) {
    forms.forEach(form => {
      form.style.display = (form.id === formId) ? 'block' : 'none';
    });

    buttons.forEach(btn => {
      btn.classList.toggle('active', btn.dataset.target === formId);
    });

    infoBox.style.display = 'none';
    infoBox.innerHTML = '';
  }

  // Init with register form
  showForm('register-form');

  buttons.forEach(button => {
    button.addEventListener('click', () => {
      showForm(button.dataset.target);
    });
  });

  // JWT token assumed stored in localStorage
  const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  };
  let rolesArray = JSON.parse(localStorage.getItem("roles"));

  // --- Utility function to show message in info box
  function displayMessage(message, isSuccess = true) {
    infoBox.innerHTML = `<p>${message}</p>`;
    infoBox.style.backgroundColor = isSuccess ? '#e0f2f1' : '#ffe6e6'; // teal or light red
    infoBox.style.borderLeft = isSuccess ? '6px solid #009688' : '6px solid #e53935'; // teal or red
    infoBox.style.display = 'block';
  }

  // --- Register Form
  document.getElementById('register-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const name = e.target[0].value;
    const position = e.target[1].value;
    const salary = e.target[2].value;

    const res = await fetch('http://localhost:8080/members', {
      method: 'POST',
      headers,
      body: JSON.stringify({ name, position, salary })
    });

    if (res.ok) {
      displayMessage("Registered as a member!");
      if (!rolesArray.includes("MEMBER")) {
        rolesArray.push("MEMBER");
        localStorage.setItem("roles", JSON.stringify(rolesArray));
      }
    } else {
      const error = await res.text();
      displayMessage("Registration failed: " + error, false);
    }
  });

  // --- Info Form
  document.getElementById('info-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    infoBox.style.display = 'none';
    infoBox.innerHTML = '';

    if (!rolesArray.includes("MEMBER")) {
      displayMessage("You are not a member. Please register first.", false);
      return;
    }

    const res = await fetch('http://localhost:8080/members', {
      method: 'GET',
      headers
    });

    if (res.ok) {
      const data = await res.json();
      infoBox.innerHTML = `
        <h3>Your Member Information</h3>
        <p><strong>Name:</strong> ${data.name}</p>
        <p><strong>Position:</strong> ${data.position}</p>
        <p><strong>Salary:</strong> $${data.salary}</p>
      `;
    } else if (res.status === 404) {
      displayMessage("You are not a member. Please register first.", false);
    } else {
      displayMessage("Failed to fetch info. Try again later.", false);
    }

    infoBox.style.display = 'block';
  });

  // --- Update Form
  document.getElementById('update-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const name = e.target[0].value;
    const position = e.target[1].value;
    const salary = e.target[2].value;

    if (!rolesArray.includes("MEMBER")) {
      displayMessage("You are not a member. Please register first.", false);
      return;
    }

    const res = await fetch('http://localhost:8080/members', {
      method: 'PUT',
      headers,
      body: JSON.stringify({ name, position, salary })
    });

    if (res.ok) {
      displayMessage("Member info updated.");
    } else {
      displayMessage("Failed to update info.", false);
    }
  });

  // --- Delete Form
  document.getElementById('delete-form').addEventListener('submit', async (e) => {
    e.preventDefault();

    if (!rolesArray.includes("MEMBER")) {
      displayMessage("You are not a member. Please register first.", false);
      return;
    }

    if (!confirm("Are you sure you want to delete your member account?")) return;

    const res = await fetch('http://localhost:8080/members', {
      method: 'DELETE',
      headers
    });

    if (res.ok) {
      displayMessage("Your member account has been deleted.");
      rolesArray = rolesArray.filter(role => role !== 'MEMBER');
      localStorage.setItem("roles", JSON.stringify(rolesArray));
    } else {
      displayMessage("Failed to delete account.", false);
    }
  });

});
