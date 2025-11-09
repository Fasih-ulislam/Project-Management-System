document.addEventListener('DOMContentLoaded', () => {
  const token = localStorage.getItem("token");
if(!token){
    window.location.href = "login.html";
}

    const buttons = document.querySelectorAll('.nav-button');
    const forms = document.querySelectorAll('.form-section');
    const infoBox = document.getElementById('member-info-box');
  
    function showForm(formId) {
      forms.forEach(form => {
        form.style.display = (form.id === formId) ? 'block' : 'none';
      });
  
      buttons.forEach(btn => {
        btn.classList.toggle('active', btn.dataset.target === formId);
      });
  
      if (infoBox) {
        infoBox.style.display = 'none';
        infoBox.innerHTML = '';
      }
    }
  
    showForm('info-form');
  
    const urlParams = new URLSearchParams(window.location.search);
    const projectId = urlParams.get("id");
  
    if (!projectId) {
      alert("No project selected. Please go back and select a project.");
    } else {
      fetchTasks(projectId);
    }
  
    function fetchTasks(projectId) {
      fetch(`http://localhost:8080/projects/${projectId}/tasks`, {
        headers: {
          "Authorization": "Bearer " + localStorage.getItem("token"),
        },
      })
      .then(res => res.json())
      .then(tasks => {
        const listContainer = document.getElementById("task-list");
        listContainer.innerHTML = ""; // Clear existing tasks
        if (tasks.length === 0) {
          listContainer.innerHTML = "<p>No tasks found for this project.</p>";
          return;
        }
  
        tasks.forEach(task => {
          const card = document.createElement("div");
          card.className = "project-card";
          card.innerHTML = `
            <div class="project-info">
              <h3>${task.title}</h3>
              <p>Start Date: ${task.startDate || 'N/A'}</p>
              <p>Deadline: ${task.endDate || 'N/A'}</p>
              <p>Status: ${task.completed ? "Completed" : "In Progress"}</p>
              <p class="description">${task.description}</p>
              <a href="task-details.html?taskId=${task.id}" class="view-details-btn">View Details</a>
              <a href="expenses.html?projectId=${projectId}&taskId=${task.id}" class="view-details-btn">Expense Details</a>
              <a href="teams.html?taskId=${task.id}" class="view-details-btn">Assign Team</a>
            </div>
          `;
          card.addEventListener("click", () => {
            document.getElementById("update-task-id").value = task.id;
            document.getElementById("delete-task-id").value = task.id;
          });
          listContainer.appendChild(card);
        });
      })
      .catch(err => {
        console.error(err);
        alert("Failed to fetch tasks.");
      });
    }
  
    // Create Task
    document.getElementById("task-register-form").addEventListener("submit", async (e) => {
      e.preventDefault();
  
      const form = e.target;
      const taskData = {
        title: form.title.value,
        description: form.description.value,
        startDate: form.startDate.value,
        endDate: form.endDate.value,
      };
  
      try {
        const response = await fetch(`http://localhost:8080/projects/${projectId}/tasks`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
          body: JSON.stringify(taskData),
        });
  
        if (!response.ok) throw new Error("Failed to create task");
  
        alert("Task created successfully!");
        form.reset();
        showForm("info-form");
        fetchTasks(projectId);
      } catch (err) {
        console.error(err);
        alert("Error creating task.");
      }
    });
  
    // Delete Task
    document.getElementById("task-delete-form").addEventListener("submit", async (e) => {
      e.preventDefault();
      const taskId = document.getElementById("delete-task-id").value;
  
      if (!taskId) {
        alert("Please provide a task ID.");
        return;
      }
  
      try {
        const res = await fetch(`http://localhost:8080/projects/${projectId}/tasks/${taskId}`, {
          method: "DELETE",
          headers: {
            Authorization: "Bearer " + localStorage.getItem("token"),
          },
        });
  
        if (!res.ok) throw new Error("Failed to delete task");
        alert("Task deleted successfully!");
        showForm("info-form");
        fetchTasks(projectId);
      } catch (err) {
        console.error(err);
        alert("Error deleting task.");
      }
    });
  
    // Nav button click handler
    buttons.forEach(btn => {
      btn.addEventListener("click", () => showForm(btn.dataset.target));
    });
  });
  