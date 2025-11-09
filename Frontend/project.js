document.addEventListener("DOMContentLoaded", () => {
    const forms = document.querySelectorAll(".form-section");
    const buttons = document.querySelectorAll(".nav-button");
    const token = localStorage.getItem("token");
if(!token){
    window.location.href = "login.html";
}

  
    function showForm(id) {
      forms.forEach(form => form.style.display = (form.id === id) ? "block" : "none");
      buttons.forEach(btn => btn.classList.toggle("active", btn.dataset.target === id));
    }
  
    showForm("project-list-section");
    fetchProjects();
  
    buttons.forEach(btn => {
      btn.addEventListener("click", () => showForm(btn.dataset.target));
    });
  
    async function fetchProjects() {
      try {
        const response = await fetch("http://localhost:8080/projects", {
          headers: {
            Authorization: "Bearer " + localStorage.getItem("token"),
          },
        });
        const projects = await response.json();
        const container = document.getElementById("project-list");
        container.innerHTML = "";
  
        if (projects.length === 0) {
          container.innerHTML = "<p>No projects found.</p>";
          return;
        }
  
        projects.forEach(p => {
          const card = document.createElement("div");
          card.className = "project-card";
          card.innerHTML = `
            <h3>${p.name}</h3>
            <p>ID: ${p.id}</p>
            <p>Manager ID: ${p.managerId}</p>
            <p>Start: ${p.startDate}</p>
            <p>End: ${p.endDate}</p>
            <p>Budget: $${p.budget}</p>
            <p>Status: ${p.status}</p>
            <p>Description: ${p.description || 'N/A'}</p>
          `;
          card.addEventListener("click", () => {
            document.getElementById("update-project-id").value = p.id;
            document.getElementById("delete-project-id").value = p.id;
          });
          container.appendChild(card);
        });
      } catch (err) {
        console.error(err);
        alert("Failed to fetch projects.");
      }
    }
  
    document.getElementById("create-project-form").addEventListener("submit", async (e) => {
      e.preventDefault();
      const form = e.target;
  
      const data = {
        managerId: form.managerId.value,
        name: form.name.value,
        description: form.description.value,
        startDate: form.startDate.value,
        endDate: form.endDate.value,
        budget: parseFloat(form.budget.value),
      };
  
      try {
        const response = await fetch("http://localhost:8080/projects", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: "Bearer " + localStorage.getItem("token"),
          },
          body: JSON.stringify(data),
        });
  
        if (!response.ok) throw new Error("Failed to create project");
  
        alert("Project created!");
        form.reset();
        showForm("project-list-section");
        fetchProjects();
      } catch (err) {
        console.error(err);
        alert("Error creating project.");
      }
    });
  
    document.getElementById("update-project-form").addEventListener("submit", async (e) => {
      e.preventDefault();
      const form = e.target;
      const id = form.id.value;
  
      const data = {
        managerId: form.managerId.value,
        name: form.name.value,
        description: form.description.value,
        startDate: form.startDate.value,
        endDate: form.endDate.value,
        budget: parseFloat(form.budget.value),
      };
  
      try {
        const response = await fetch(`http://localhost:8080/projects/${id}`, {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: "Bearer " + localStorage.getItem("token"),
          },
          body: JSON.stringify(data),
        });
  
        if (!response.ok) throw new Error("Failed to update project");
  
        alert("Project updated!");
        showForm("project-list-section");
        fetchProjects();
      } catch (err) {
        console.error(err);
        alert("Error updating project.");
      }
    });
  
    document.getElementById("delete-project-form").addEventListener("submit", async (e) => {
      e.preventDefault();
      const id = document.getElementById("delete-project-id").value;
  
      try {
        const response = await fetch(`http://localhost:8080/projects/${id}`, {
          method: "DELETE",
          headers: {
            Authorization: "Bearer " + localStorage.getItem("token"),
          },
        });
  
        if (!response.ok) throw new Error("Failed to delete project");
  
        alert("Project deleted!");
        showForm("project-list-section");
        fetchProjects();
      } catch (err) {
        console.error(err);
        alert("Error deleting project.");
      }
    });
  });
  