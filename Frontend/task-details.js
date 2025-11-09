document.addEventListener('DOMContentLoaded', () => {const token = localStorage.getItem("token");
if(!token){
    window.location.href = "login.html";
}

    const buttons = document.querySelectorAll('.nav-button');
    const forms = document.querySelectorAll('.form-section');
  
    const urlParams = new URLSearchParams(window.location.search);
    const taskId = urlParams.get("taskId");
  
    if (!taskId) {
      alert("Missing task ID in URL.");
      return;
    }
  
    function showForm(formId) {
      forms.forEach(form => {
        form.style.display = (form.id === formId) ? 'block' : 'none';
      });
      buttons.forEach(btn => {
        btn.classList.toggle('active', btn.dataset.target === formId);
      });
    }
  
    showForm('info-form');
    fetchTaskDetails();
  
    function fetchTaskDetails() {
      fetch(`http://localhost:8080/tasks/${taskId}/taskDetails`, {
        headers: {
          Authorization: "Bearer " + localStorage.getItem("token"),
        },
      })
        .then(res => res.json())
        .then(details => {
          const container = document.getElementById("taskdetails-list");
          container.innerHTML = "";
          if (details.length === 0) {
            container.innerHTML = "<p>No task details found.</p>";
            return;
          }
  
          details.forEach(detail => {
            let date = new Date(detail.timestamp);
            const card = document.createElement("div");
            card.className = "taskdetails-card";
            card.innerHTML = `
            <p><strong>ID:</strong> ${detail.id}</p>
            <p><strong>Status:</strong> ${detail.status}</p>
            <p><strong>Completion %:</strong> ${detail.percentageCompleted}</p>
            <p><strong>Hours Worked:</strong> ${detail.hoursWorked}</p>
            <p><strong>Comment:</strong> ${detail.comment || 'N/A'}</p>
            <p><strong>Log Time:</strong> ${date.toLocaleTimeString('en-US', { hour12: true }) || 'N/A'}</p>
`;

            card.addEventListener("click", () => {
              document.getElementById("update-taskdetails-id").value = detail.id;
              document.getElementById("delete-taskdetails-id").value = detail.id;
            });
            container.appendChild(card);
          });
        })
        .catch(err => {
          console.error(err);
          alert("Error fetching task details.");
        });
    }
  
    document.getElementById("taskdetails-create-form").addEventListener("submit", async (e) => {
      e.preventDefault();
      const form = e.target;
      const data = {
        status: form.status.value,
        percentageCompleted: parseInt(form.percentageCompleted.value),
        hoursWorked: parseFloat(form.hoursWorked.value),
        comment: form.comment.value
      };      
  
      try {
        const res = await fetch(`http://localhost:8080/tasks/${taskId}/taskDetails`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: "Bearer " + localStorage.getItem("token"),
          },
          body: JSON.stringify(data),
        });
        if (!res.ok) throw new Error("Create failed");
        alert("Task detail created.");
        form.reset();
        showForm("info-form");
        fetchTaskDetails();
      } catch (err) {
        console.error(err);
        alert("Failed to create task detail.");
      }
    });
  
    document.getElementById("taskdetails-update-form").addEventListener("submit", async (e) => {
      e.preventDefault();
      const id = document.getElementById("update-taskdetails-id").value;
      const form = e.target;
      const data = {
        status: form.status.value,
        percentageCompleted: parseInt(form.percentageCompleted.value),
        hoursWorked: parseFloat(form.hoursWorked.value),
        comment: form.comment.value
      };      
  
      try {
        const res = await fetch(`http://localhost:8080/tasks/${taskId}/taskDetails/${id}`, {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: "Bearer " + localStorage.getItem("token"),
          },
          body: JSON.stringify(data),
        });
        if (!res.ok) throw new Error("Update failed");
        alert("Task detail updated.");
        form.reset();
        showForm("info-form");
        fetchTaskDetails();
      } catch (err) {
        console.error(err);
        alert("Failed to update task detail.");
      }
    });
  
    document.getElementById("taskdetails-delete-form").addEventListener("submit", async (e) => {
      e.preventDefault();
      const id = document.getElementById("delete-taskdetails-id").value;
  
      try {
        const res = await fetch(`http://localhost:8080/tasks/${taskId}/taskDetails/${id}`, {
          method: "DELETE",
          headers: {
            Authorization: "Bearer " + localStorage.getItem("token"),
          },
        });
        if (!res.ok) throw new Error("Delete failed");
        alert("Task detail deleted.");
        showForm("info-form");
        fetchTaskDetails();
      } catch (err) {
        console.error(err);
        alert("Failed to delete task detail.");
      }
    });
  
    buttons.forEach(btn => {
      btn.addEventListener("click", () => showForm(btn.dataset.target));
    });
  });
  