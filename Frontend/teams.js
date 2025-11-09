document.addEventListener('DOMContentLoaded', () => {
  const token = localStorage.getItem("token");
if(!token){
    window.location.href = "login.html";
}

  const buttons = document.querySelectorAll('.nav-button');
  const forms = document.querySelectorAll('.form-section');
  const baseUrl = "http://localhost:8080/teams";
  const urlParams = new URLSearchParams(window.location.search);
  const taskId = urlParams.get("taskId");

  const assignedTeamContainer = document.getElementById("assigned-team-container");
  const teamListContainer = document.getElementById("team-list");

  function showForm(formId) {
    forms.forEach(form => form.style.display = form.id === formId ? 'block' : 'none');
    buttons.forEach(btn => btn.classList.toggle('active', btn.dataset.target === formId));
  }

  showForm('info-form');
  fetchAssignedTeam();
  fetchTeams();

  function fetchAssignedTeam() {
    fetch(`http://localhost:8080/teams/${taskId}/team`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    .then(res => {
      if (!res.ok) throw new Error("No team assigned");
      return res.json();
    })
    .then(team => {
      assignedTeamContainer.innerHTML = `
        <div class="team-card assigned">
          <p><strong>ID:</strong> ${team.id}</p>
          <p><strong>Name:</strong> ${team.name}</p>
          <button class="action-btn remove-btn" data-id="${team.id}">Remove</button>
        </div>
      `;
      document.querySelector(".remove-btn").addEventListener("click", () => removeTeamFromTask(team.id));
    })
    .catch(() => {
      assignedTeamContainer.innerHTML = `<h3>No team currently assigned to this task.</h3>`;
    });
  }

  function fetchTeams() {
    fetch(`${baseUrl}/my-Teams`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    .then(res => res.json())
    .then(teams => {
      teamListContainer.innerHTML = "";
      if (teams.length === 0) {
        teamListContainer.innerHTML = "<p>No teams found.</p>";
        return;
      }

      fetch(`http://localhost:8080/teams?my-Teams`, {
        headers: { Authorization: `Bearer ${token}` }
      })
      .then(res => res.ok ? res.json() : null)
      .then(assigned => {
        const assignedId = assigned ? assigned.id : null;

        teams.forEach(team => {
          const card = document.createElement("div");
          card.className = "team-card";
          card.innerHTML = `
            <p><strong>ID:</strong> ${team.id}</p>
            <p><strong>Name:</strong> ${team.name}</p>
            <a href="team-members.html?teamId=${team.id}" class="view-details-btn">Manage Members</a>
            ${assignedId === team.id 
              ? '<span class="assigned-label">Assigned</span>' 
              : `<button class="action-btn assign-btn">Assign</button>`}
          `;

          if (assignedId !== team.id) {
            const assignBtn = card.querySelector(".assign-btn");
            assignBtn.addEventListener("click", () => assignToTask(team.id));
          }

          card.addEventListener("click", () => {
            document.getElementById("update-team-id").value = team.id;
            document.getElementById("delete-team-id").value = team.id;
          });

          teamListContainer.appendChild(card);
        });
      });
    })
    .catch(err => {
      console.error(err);
      alert("Error fetching teams.");
    });
  }

  async function assignToTask(teamId) {
    const confirmed = confirm("Are you sure you want to assign this team to the task?");
    if (!confirmed) return;

    try {
      const res = await fetch(`http://localhost:8080/teams/tasks/${taskId}/assign-to-team/${teamId}`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json"
        }
      });

      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || "Failed to assign team");
      }

      alert("Team assigned successfully!");
      fetchAssignedTeam();
      fetchTeams();
    } catch (err) {
      console.error(err);
      alert("Error: " + err.message);
    }
  }

  async function removeTeamFromTask(teamId) {
    const confirmed = confirm("Are you sure you want to unassign this team?");
    if (!confirmed) return;

    try {
      const res = await fetch(`http://localhost:8080/teams/tasks/${taskId}/remove-team/${teamId}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json"
        }
      });

      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || "Failed to unassign");
      }

      alert("Team unassigned successfully!");
      fetchAssignedTeam();
      fetchTeams();
    } catch (err) {
      console.error(err);
      alert("Error: " + err.message);
    }
  }

  // Nav buttons
  buttons.forEach(btn => btn.addEventListener("click", () => showForm(btn.dataset.target)));

  // Create
  document.getElementById("team-create-form").addEventListener("submit", async (e) => {
    e.preventDefault();
    const data = { name: e.target.name.value };

    try {
      const res = await fetch(baseUrl, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify(data)
      });
      if (!res.ok) throw new Error("Create failed");
      alert("Team created.");
      e.target.reset();
      showForm("info-form");
      fetchTeams();
    } catch (err) {
      console.error(err);
      alert("Failed to create team.");
    }
  });

  // Update
  document.getElementById("team-update-form").addEventListener("submit", async (e) => {
    e.preventDefault();
    const id = document.getElementById("update-team-id").value;
    const data = { name: e.target.name.value };

    try {
      const res = await fetch(`${baseUrl}/${id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify(data)
      });
      if (!res.ok) throw new Error("Update failed");
      alert("Team updated.");
      e.target.reset();
      showForm("info-form");
      fetchTeams();
    } catch (err) {
      console.error(err);
      alert("Failed to update team.");
    }
  });

  // Delete
  document.getElementById("team-delete-form").addEventListener("submit", async (e) => {
    e.preventDefault();
    const id = document.getElementById("delete-team-id").value;

    try {
      const res = await fetch(`${baseUrl}/${id}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` }
      });
      if (!res.ok) throw new Error("Delete failed");
      alert("Team deleted.");
      showForm("info-form");
      fetchTeams();
    } catch (err) {
      console.error(err);
      alert("Failed to delete team.");
    }
  });
});
